package com.vungle.ads.internal.task

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.annotation.VisibleForTesting
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executor
import kotlin.math.min

/**
 * The VungleJobRunner is responsible for creating jobs.
 */
class VungleJobRunner(
    private val creator: JobCreator,
    private val executor: Executor,
    private val threadPriorityHelper: ThreadPriorityHelper?
) : JobRunner {

    private val pendingJobs: MutableList<PendingJob>
    private val pendingRunnable: Runnable
    private var nextCheck = Long.MAX_VALUE

    private class PendingJob(val uptimeMillis: Long, var info: JobInfo?)
    private class PendingRunnable(var runner: WeakReference<VungleJobRunner>) :
        Runnable {
        override fun run() {
            val ref = runner.get()
            ref?.executePendingJobs()
        }
    }

    @Synchronized
    override fun execute(jobInfo: JobInfo) {
        // make a copy to avoid mutable side effects
        val jobInfoCopy = jobInfo.copy()
        jobInfoCopy?.let {
            val jobTag = it.jobTag
            val delay = it.delay

            //clearing delay
            it.setDelay(0)
            if (it.updateCurrent) {
                for (job in pendingJobs) {
                    if (job.info?.jobTag == jobTag) {
                        Log.d(TAG, "replacing pending job with new $jobTag")
                        pendingJobs.remove(job)
                    }
                }
            }
            pendingJobs.add(PendingJob(SystemClock.uptimeMillis() + delay, it))
            executePendingJobs()
        }
    }

    @VisibleForTesting
    internal fun getPendingJobSize() = pendingJobs.size

    @Synchronized
    override fun cancelPendingJob(tag: String) {
        val jobsToRemove: MutableList<PendingJob> = ArrayList()
        for (pendingJob in pendingJobs) {
            if (pendingJob.info?.jobTag == tag) {
                jobsToRemove.add(pendingJob)
            }
        }
        pendingJobs.removeAll(jobsToRemove)
    }

    @Synchronized
    private fun executePendingJobs() {
        val now = SystemClock.uptimeMillis()
        var nextCheck = Long.MAX_VALUE
        for (job in pendingJobs) {
            if (now >= job.uptimeMillis) {
                // TODO: Need NetworkProvider or not?
                pendingJobs.remove(job)
                job.info?.let {
                    executor.execute(JobRunnable(it, creator, this, threadPriorityHelper))
                }
            } else {
                nextCheck = min(nextCheck, job.uptimeMillis)
            }
        }
        if (nextCheck != Long.MAX_VALUE && nextCheck != this.nextCheck) {
            handler.removeCallbacks(pendingRunnable)
            handler.postAtTime(pendingRunnable, TAG, nextCheck)
        }
        this.nextCheck = nextCheck
    }

    companion object {
        private val handler = Handler(Looper.getMainLooper())
        private val TAG = VungleJobRunner::class.java.simpleName
    }

    init {
        pendingJobs = CopyOnWriteArrayList()
        pendingRunnable = PendingRunnable(WeakReference(this))
    }
}
