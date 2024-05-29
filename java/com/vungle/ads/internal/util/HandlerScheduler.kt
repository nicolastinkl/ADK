package com.vungle.ads.internal.util

import android.os.Handler
import android.os.Looper
import android.os.SystemClock

class HandlerScheduler {
    private val handler = Handler(Looper.getMainLooper())
    fun schedule(runnable: Runnable, tag: String, delay: Long) {
        handler.postAtTime(runnable, tag, calculateTime(delay))
    }

    private fun calculateTime(delay: Long): Long {
        return SystemClock.uptimeMillis() + delay
    }

    fun schedule(runnable: Runnable, delay: Long) {
        handler.postAtTime(runnable, calculateTime(delay))
    }

    fun cancel(tag: String) {
        handler.removeCallbacksAndMessages(tag)
    }

    fun cancelAll() {
        handler.removeCallbacksAndMessages(null)
    }
}