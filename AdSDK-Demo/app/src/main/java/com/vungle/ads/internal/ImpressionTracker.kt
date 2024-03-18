package com.vungle.ads.internal

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.VisibleForTesting
import androidx.core.view.ViewCompat
import java.lang.ref.WeakReference
import java.util.*

/**
 * Impression tracker to determine when view become visible or invisible.
 */
class ImpressionTracker @VisibleForTesting internal constructor(
    context: Context,
    // Views that are being tracked, mapped to the min viewable percentage
    private val trackedViews: MutableMap<View, TrackingInfo>,
    // Handler for visibility
    private val visibilityHandler: Handler
) {
    // A rect to use for hit testing. Create this once to avoid excess garbage collection
    private val clipRect = Rect()

    // Listener that passes the view when a visibility check occurs
    fun interface ImpressionListener {
        fun onImpression(view: View?)
    }

    @VisibleForTesting
    val onPreDrawListener: ViewTreeObserver.OnPreDrawListener

    @VisibleForTesting
    var weakViewTreeObserver: WeakReference<ViewTreeObserver?>

    @VisibleForTesting
    internal class TrackingInfo {
        var minViewablePercent = 0
        var impressionListener: ImpressionListener? = null
    }

    // Runnable to run on each visibility loop
    private val visibilityRunnable: VisibilityRunnable

    // Whether the visibility runnable is scheduled
    private var isVisibilityScheduled = false

    private var setViewTreeObserverSucceed = false

    constructor(context: Context) : this(context, WeakHashMap<View, TrackingInfo>(10), Handler()) {}

    private fun setViewTreeObserver(context: Context?, view: View?): Boolean {
        val originalViewTreeObserver = weakViewTreeObserver.get()
        if (originalViewTreeObserver != null && originalViewTreeObserver.isAlive) {
            return true
        }
        val rootView = getTopView(context, view)
        if (rootView == null) {
            Log.d(TAG, "Unable to set ViewTreeObserver due to no available root view.")
            return false
        }
        val viewTreeObserver = rootView.viewTreeObserver
        if (!viewTreeObserver.isAlive) {
            Log.d(TAG, "The root view tree observer was not alive")
            return false
        }
        weakViewTreeObserver = WeakReference(viewTreeObserver)
        viewTreeObserver.addOnPreDrawListener(onPreDrawListener)
        return true
    }

    /**
     * Tracks the given view for impression.
     */
    fun addView(view: View, listener: ImpressionListener?) {
        setViewTreeObserverSucceed = setViewTreeObserver(view.context, view)

        // Find the view if already tracked
        var trackingInfo = trackedViews[view]
        if (trackingInfo == null) {
            trackingInfo = TrackingInfo()
            trackedViews[view] = trackingInfo
            scheduleVisibilityCheck()
        }
        trackingInfo.minViewablePercent = MIN_VISIBILITY_PERCENTAGE
        trackingInfo.impressionListener = listener
    }

    @VisibleForTesting
    fun removeView(view: View) {
        trackedViews.remove(view)
    }

    fun clear() {
        trackedViews.clear()
        visibilityHandler.removeMessages(0)
        isVisibilityScheduled = false
    }

    /**
     * Destroy the impression tracker.
     */
    fun destroy() {
        clear()
        val viewTreeObserver = weakViewTreeObserver.get()
        if (viewTreeObserver != null && viewTreeObserver.isAlive) {
            viewTreeObserver.removeOnPreDrawListener(onPreDrawListener)
        }
        weakViewTreeObserver.clear()
    }

    private fun getTopView(context: Context?, view: View?): View? {
        var topView: View? = null
        if (context is Activity) {
            topView = context.window.decorView.findViewById(android.R.id.content)
        }
        if (topView == null && view != null) {
            if (!ViewCompat.isAttachedToWindow(view)) {
                Log.w(TAG, "Trying to call View#rootView() on an unattached View.")
            }
            val rootView = view.rootView
            if (rootView != null) {
                topView = rootView.findViewById(android.R.id.content)
            }

            if (topView == null) {
                topView = rootView
            }

        }
        return topView
    }

    private fun scheduleVisibilityCheck() {
        if (isVisibilityScheduled) {
            return
        }
        isVisibilityScheduled = true
        visibilityHandler.postDelayed(visibilityRunnable, VISIBILITY_THROTTLE_MILLIS.toLong())
    }

    @VisibleForTesting
    internal inner class VisibilityRunnable : Runnable {
        private val visibleViews: ArrayList<View> = ArrayList()
        override fun run() {
            isVisibilityScheduled = false
            for ((view, value) in trackedViews) {
                val minPercentageViewed = value.minViewablePercent
                if (isVisible(view, minPercentageViewed)) {
                    visibleViews.add(view)
                }
            }
            for (view in visibleViews) {
                val info = trackedViews[view]
                info?.impressionListener?.onImpression(view)
                removeView(view)
            }
            visibleViews.clear()

            if (trackedViews.isNotEmpty() && !setViewTreeObserverSucceed) {
                scheduleVisibilityCheck()
            }
        }

    }

    /**
     * Whether the view is at least certain amount visible.
     */
    private fun isVisible(view: View?, minPercentageViewed: Int): Boolean {
        if (view == null || view.visibility != View.VISIBLE || view.parent == null) {
            return false
        }
        if (!view.getGlobalVisibleRect(clipRect)) {
            return false
        }
        val visibleViewArea = clipRect.height().toLong() * clipRect.width()
        val totalViewArea = view.height.toLong() * view.width
        return if (totalViewArea <= 0) {
            false
        } else 100 * visibleViewArea >= minPercentageViewed * totalViewArea
    }

    companion object {
        private val TAG = ImpressionTracker::class.java.simpleName

        // The minimum percent of the ad to be on screen
        private const val MIN_VISIBILITY_PERCENTAGE = 1

        // Time interval to use for throttling visibility checks.
        private const val VISIBILITY_THROTTLE_MILLIS = 100
    }

    init {
        visibilityRunnable = VisibilityRunnable()
        onPreDrawListener = ViewTreeObserver.OnPreDrawListener {
            scheduleVisibilityCheck()
            true
        }
        weakViewTreeObserver = WeakReference(null)
        setViewTreeObserverSucceed = setViewTreeObserver(context, null)
    }
}
