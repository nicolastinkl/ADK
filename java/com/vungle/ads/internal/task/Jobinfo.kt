package com.vungle.ads.internal.task

import android.os.Bundle
import android.util.Log
import androidx.annotation.IntDef

class JobInfo(val jobTag: String) : Cloneable {
    var updateCurrent = false
        private set
    var delay: Long = 0
        private set
    private var rescheduleTimeout: Long = 0
    private var nextRescheduleTimeout: Long = 0
    var extras = Bundle()
        private set

    @ReschedulePolicy
    private var reschedulePolicy = ReschedulePolicy.EXPONENTIAL

    @Priority
    var priority = Priority.NORMAL
        private set

    @NetworkType
    var requiredNetworkType = NetworkType.ANY
        private set

    fun setUpdateCurrent(updateCurrent: Boolean): JobInfo {
        this.updateCurrent = updateCurrent
        return this
    }

    fun setExtras(extras: Bundle): JobInfo {
        this.extras = extras
        return this
    }

    fun setDelay(delay: Long): JobInfo {
        this.delay = delay
        return this
    }

    fun setReschedulePolicy(
        rescheduleTimeout: Long,
        @ReschedulePolicy delayCriteria: Int
    ): JobInfo {
        this.rescheduleTimeout = rescheduleTimeout
        reschedulePolicy = delayCriteria
        return this
    }

    fun setPriority(@Priority priority: Int): JobInfo {
        this.priority = priority
        return this
    }

    fun makeNextRescedule(): Long {
        if (rescheduleTimeout == 0L) return 0
        if (nextRescheduleTimeout == 0L) {
            nextRescheduleTimeout = rescheduleTimeout
        } else if (reschedulePolicy == ReschedulePolicy.EXPONENTIAL) {
            nextRescheduleTimeout *= 2
        }
        return nextRescheduleTimeout
    }

    fun copy(): JobInfo? {
        try {
            return super.clone() as JobInfo
        } catch (e: CloneNotSupportedException) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
        return null
    }

    fun setRequiredNetworkType(@NetworkType requiredNetworkType: Int): JobInfo {
        this.requiredNetworkType = requiredNetworkType
        return this
    }

    @IntDef(value = [ReschedulePolicy.LINEAR, ReschedulePolicy.EXPONENTIAL])
    @Retention(AnnotationRetention.SOURCE)
    annotation class ReschedulePolicy {
        companion object {
            const val LINEAR = 0
            const val EXPONENTIAL = 1
        }
    }

    @IntDef(value = [Priority.LOWEST, Priority.LOW, Priority.NORMAL, Priority.HIGH, Priority.HIGHEST, Priority.CRITICAL])
    @Retention(AnnotationRetention.SOURCE)
    annotation class Priority {
        companion object {
            const val LOWEST = 0
            const val LOW = 1
            const val NORMAL = 2
            const val HIGH = 3
            const val HIGHEST = 4
            const val CRITICAL = 5
        }
    }

    @IntDef(value = [NetworkType.ANY, NetworkType.CONNECTED])
    annotation class NetworkType {
        companion object {
            const val ANY = 0
            const val CONNECTED = 1
        }
    }

    companion object {
        private const val TAG = "JobInfo"
    }
}
