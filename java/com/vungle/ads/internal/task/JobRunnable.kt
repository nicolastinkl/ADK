package com.vungle.ads.internal.task

import android.os.Process
import android.util.Log

class JobRunnable(
    private val jobinfo: JobInfo,
    private val creator: JobCreator,
    private val jobRunner: JobRunner,
    private val threadPriorityHelper: ThreadPriorityHelper?
) : PriorityRunnable() {

    @JobInfo.Priority
    override val priority: Int
        get() = jobinfo.priority

    override fun run() {
        if (threadPriorityHelper != null) {
            try {
                val targetPriority = threadPriorityHelper.makeAndroidThreadPriority(jobinfo)
                Process.setThreadPriority(targetPriority)
                Log.d(
                    TAG,
                    "Setting process thread prio = " + targetPriority + " for " + jobinfo.jobTag
                )
            } catch (throwable: Throwable) {
                Log.e(TAG, "Error on setting process thread priority")
            }
        }
        try {
            val jobTag = jobinfo.jobTag
            val params = jobinfo.extras
            Log.d(TAG, "Start job " + jobTag + "Thread " + Thread.currentThread().name)
            val job = creator.create(jobTag)
            val result = job.onRunJob(params, jobRunner)
            Log.d(TAG, "On job finished $jobTag with result $result")
            if (result == Job.Result.RESCHEDULE) {
                val nextReschedule = jobinfo.makeNextRescedule()
                if (nextReschedule > 0) {
                    jobinfo.setDelay(nextReschedule)
                    jobRunner.execute(jobinfo)
                    Log.d(TAG, "Rescheduling $jobTag in $nextReschedule")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Cannot create job" + e.localizedMessage)
        }
    }

    companion object {
        private val TAG = JobRunnable::class.java.simpleName
    }
}
