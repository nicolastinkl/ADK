package com.vungle.ads.internal.util

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.VisibleForTesting
import java.util.concurrent.Executor

/**
 * Utility class for threading env
 */
object ThreadUtil {
    private val UI_HANDLER = Handler(Looper.getMainLooper())

    @VisibleForTesting
    internal var uiExecutor: Executor? = null

    /**
     * Checks if thread where this method is called is main.
     *
     * @return `true` if current thread is main `false` otherwise
     */
    val isMainThread: Boolean
        get() {
            val mainLooper = Looper.getMainLooper() ?: return false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) return mainLooper.isCurrentThread
            val myLooper = Looper.myLooper()
            return myLooper != null && mainLooper.thread == myLooper.thread
        }

    /**
     * Performs the given runnable on the main thread.
     */
    fun runOnUiThread(runnable: Runnable) {
        if (isMainThread) {
            runnable.run()
        } else {
            if (uiExecutor != null) {
                uiExecutor?.execute(runnable)
            } else {
                UI_HANDLER.post(runnable)
            }
        }
    }
}
