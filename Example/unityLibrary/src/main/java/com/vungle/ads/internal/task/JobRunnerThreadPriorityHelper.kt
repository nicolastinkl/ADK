package com.vungle.ads.internal.task

import android.os.Process

class JobRunnerThreadPriorityHelper : ThreadPriorityHelper {
    override fun makeAndroidThreadPriority(jobInfo: JobInfo): Int {
        val priority = jobInfo.priority
        val delta = Math.min(0, priority - JobInfo.Priority.NORMAL)
        val processPriority = Process.THREAD_PRIORITY_BACKGROUND + Math.abs(delta)
        return Math.min(Process.THREAD_PRIORITY_LOWEST, processPriority)
    }
}