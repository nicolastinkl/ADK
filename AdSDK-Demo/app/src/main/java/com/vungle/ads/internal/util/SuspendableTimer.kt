package com.vungle.ads.internal.util

import android.os.CountDownTimer
import androidx.annotation.VisibleForTesting

internal class SuspendableTimer(
    private val durationSecs: Double,
    private val repeats: Boolean,
    private val onTick: () -> Unit = {},
    private val onFinish: () -> Unit
) {
    private var isPaused: Boolean = false
    private var isCanceled: Boolean = false
    @VisibleForTesting
    internal var nextDurationSecs = this.durationSecs
    @VisibleForTesting
    internal var startTimeMillis: Long = 0
    @VisibleForTesting
    internal var timer: CountDownTimer? = null
    private val durationMillis: Long
        get() {
            return (this.durationSecs * 1000).toLong()
        }
    @VisibleForTesting
    internal val elapsedMillis: Long
        get() {
            if (this.isPaused) {
                return this.durationMillis - this.nextDurationMillis
            }
            return System.currentTimeMillis() - this.startTimeMillis
        }
    private val elapsedSecs: Double
        get() {
            return (elapsedMillis / 1000).toDouble()
        }
    private val nextDurationMillis: Long
        get() {
            return (this.nextDurationSecs * 1000).toLong()
        }

    fun start() {
        this.startTimeMillis = System.currentTimeMillis()
        this.timer = createCountdown(this.nextDurationMillis)
        this.timer?.start()
    }

    fun reset() {
        cancel()
        start()
    }

    fun pause() {
        if (this.timer == null) {
            return
        }
        this.nextDurationSecs = this.nextDurationSecs - this.elapsedSecs
        this.isPaused = true
        this.timer?.cancel()
        this.timer = null
    }

    fun resume() {
        if (!this.isPaused) {
            return
        }
        this.isPaused = false
        this.start()
    }

    fun cancel() {
        this.isPaused = false
        this.isCanceled = true
        this.timer?.cancel()
        this.timer = null
    }

    private fun createCountdown(duration: Long): CountDownTimer {
        return object : CountDownTimer(duration, duration) {
            // Required implementation.
            override fun onTick(millisUntilFinished: Long) {
                val t = this@SuspendableTimer
                t.onTick()
            }

            override fun onFinish() {
                val t = this@SuspendableTimer
                t.onFinish()
                if (t.repeats && !t.isCanceled) {
                    t.nextDurationSecs = t.durationSecs
                    t.start()

                } else {
                    t.cancel()
                }
            }
        }
    }
}
