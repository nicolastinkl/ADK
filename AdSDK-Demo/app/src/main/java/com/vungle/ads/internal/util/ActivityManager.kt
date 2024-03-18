package com.vungle.ads.internal.util

import android.app.Activity
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.VisibleForTesting
import com.vungle.ads.AnalyticsClient
import com.vungle.ads.VungleError
import com.vungle.ads.internal.ui.PresenterAdOpenCallback
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

class ActivityManager private constructor() : Application.ActivityLifecycleCallbacks {
    open class LifeCycleCallback {
        open fun onStart() {
            //no-op
        }

        open fun onStop() {
            //no-op
        }

        open fun onResume() {
            //no-op
        }

        open fun onPause() {
            //no-op
        }
    }

    interface LeftApplicationCallback {
        fun onLeftApplication()
    }

    var isInitialized = false
        private set
    private var started = 0
    private var resumed = 0
    private val callbacks = CopyOnWriteArraySet<LifeCycleCallback>()
    private val adLeftCallbacks = ConcurrentHashMap<LeftApplicationCallback?, LifeCycleCallback>()

    //handle configuration changes
    private var handler: Handler? = null
    private var paused = true
    private var stopped = true
    private val configChangeRunnable = Runnable {
        if (resumed == 0 && !paused) {
            paused = true
            for (callback in callbacks) {
                callback.onPause()
            }
        }
        if (started == 0 && paused && !stopped) {
            stopped = true
            for (callback in callbacks) {
                callback.onStop()
            }
        }
    }

    fun init(context: Context) {
        if (this.isInitialized) return
        handler = Handler(Looper.getMainLooper())
        val app = context.applicationContext as Application
        app.registerActivityLifecycleCallbacks(this)
        this.isInitialized = true
    }

    @VisibleForTesting
    fun deInit(context: Context) {
        val app = context.applicationContext as Application
        app.unregisterActivityLifecycleCallbacks(this)
        started = 0
        resumed = 0
        paused = true
        stopped = true
        this.isInitialized = false
        callbacks.clear()
        adLeftCallbacks.clear()
    }

    @VisibleForTesting
    protected fun inForeground(): Boolean {
        return !this.isInitialized || started > 0
    }

    fun addListener(callback: LifeCycleCallback) {
        callbacks.add(callback)
    }

    private fun removeListener(callback: LifeCycleCallback) {
        callbacks.remove(callback)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        //no-op
    }

    override fun onActivityStarted(activity: Activity) {
        started++
        if (started == 1 && stopped) {
            stopped = false
            for (callback in callbacks) {
                callback.onStart()
            }
        }
    }

    override fun onActivityStopped(activity: Activity) {
        started = Math.max(0, started - 1)
        handler?.postDelayed(configChangeRunnable, CONFIG_CHANGE_DELAY)
    }

    override fun onActivityResumed(activity: Activity) {
        resumed++
        if (resumed == 1) {
            if (paused) {
                paused = false
                for (callback in callbacks) {
                    callback.onResume()
                }
            } else {
                handler?.removeCallbacks(configChangeRunnable)
            }
        }
    }

    override fun onActivityPaused(activity: Activity) {
        resumed = Math.max(0, resumed - 1)
        handler?.postDelayed(configChangeRunnable, CONFIG_CHANGE_DELAY)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        //no-op
    }

    override fun onActivityDestroyed(activity: Activity) {
        //no-op
    }

    fun addOnNextAppLeftCallback(leftCallback: LeftApplicationCallback?) {
        if (leftCallback == null) {
            return
        }
        if (!this.isInitialized) {
            leftCallback.onLeftApplication()
            return
        }
        val weakCallback = WeakReference(leftCallback)
        val cancelRunnable: Runnable = object : Runnable {
            override fun run() {
                handler?.removeCallbacks(this)
                removeOnNextAppLeftCallback(weakCallback.get())
            }
        }
        val callback: LifeCycleCallback = object : LifeCycleCallback() {
            var wasPaused = false
            override fun onStop() {
                super.onStop()
                val leftCallback = weakCallback.get()
                if (wasPaused && leftCallback != null && adLeftCallbacks.containsKey(leftCallback)) {
                    leftCallback.onLeftApplication()
                }
                removeOnNextAppLeftCallback(leftCallback)
                handler?.removeCallbacks(cancelRunnable)
            }

            override fun onResume() {
                super.onResume()
                handler?.postDelayed(
                    cancelRunnable,
                    CONFIG_CHANGE_DELAY * 2
                ) //wait for delayed onPause
            }

            override fun onPause() {
                super.onPause()
                wasPaused = true
                handler?.removeCallbacks(cancelRunnable)
            }
        }

        //handle starting from background and no activity resolved
        adLeftCallbacks[leftCallback] = callback
        if (inForeground()) {
            handler?.postDelayed(cancelRunnable, TIMEOUT)
            addListener(callback)
        } else {
            instance.addListener(object : LifeCycleCallback() {
                override fun onStart() {
                    instance.removeListener(this)
                    val callback = adLeftCallbacks[weakCallback.get()]
                    if (callback != null) {
                        handler?.postDelayed(cancelRunnable, TIMEOUT)
                        addListener(callback)
                    }
                }
            })
        }
    }

    private fun removeOnNextAppLeftCallback(leftCallback: LeftApplicationCallback?) {
        if (leftCallback == null) return
        val callback = adLeftCallbacks.remove(leftCallback)
        callback?.let { removeListener(it) }
    }

    companion object {
        val TAG = ActivityManager::class.java.simpleName
        val instance = ActivityManager()

        @VisibleForTesting
        val TIMEOUT: Long = 3000 //no activity to show timeout

        @VisibleForTesting
        val CONFIG_CHANGE_DELAY: Long = 700 //ms, max render delay, must be less than TIMEOUT
        fun startWhenForeground(
            context: Context,
            deepLinkOverrideIntent: Intent?,
            defaultIntent: Intent?,
            leftCallback: LeftApplicationCallback?,
            adOpenCallback: PresenterAdOpenCallback?
        ) {
            val weakContext = WeakReference(context)
            if (instance.inForeground()) {
                if (startActivityHandleException(
                        context,
                        deepLinkOverrideIntent,
                        defaultIntent,
                        adOpenCallback
                    )
                ) {
                    instance.addOnNextAppLeftCallback(leftCallback)
                }
            } else {
                instance.addListener(object : LifeCycleCallback() {
                    override fun onStart() {
                        super.onStart()
                        instance.removeListener(this)
                        val context = weakContext.get()
                        if (context != null && startActivityHandleException(
                                context,
                                deepLinkOverrideIntent,
                                defaultIntent,
                                adOpenCallback
                            )
                        ) {
                            instance.addOnNextAppLeftCallback(leftCallback)
                        }
                    }
                })
            }
        }

        fun startWhenForeground(
            context: Context, deeplinkOverrideIntent: Intent?,
            defaultIntent: Intent?, leftCallback: LeftApplicationCallback?
        ) {
            startWhenForeground(context, deeplinkOverrideIntent, defaultIntent, leftCallback, null)
        }

        private fun startActivityHandleException(
            context: Context,
            deepLinkOverrideIntent: Intent?,
            defaultIntent: Intent?,
            adOpenCallback: PresenterAdOpenCallback?
        ): Boolean {
            if (deepLinkOverrideIntent == null && defaultIntent == null) {
                return false
            }
            try {
                if (deepLinkOverrideIntent != null) {
                    context.startActivity(deepLinkOverrideIntent)

                    adOpenCallback?.onDeeplinkClick(true)
                } else {
                    context.startActivity(defaultIntent)
                }
            } catch (exception: Exception) {
                Log.e(
                    TAG,
                    "Cannot launch/find activity to handle the Implicit intent: $exception"
                )
                try {
                    if (deepLinkOverrideIntent != null) {
                        AnalyticsClient.logError(
                            VungleError.DEEPLINK_OPEN_FAILED,
                            "Fail to open ${deepLinkOverrideIntent.dataString}",
                            ""
                        )
                        adOpenCallback?.onDeeplinkClick(false)
                    }

                    if (deepLinkOverrideIntent == null || defaultIntent == null) {
                        return false
                    }
                    context.startActivity(defaultIntent)
                } catch (exception: Exception) {
                    return false
                }
                return true
            }
            return true
        }

        fun addLifecycleListener(listener: LifeCycleCallback) {
            instance.addListener(listener)
        }
    }
}