package com.vungle.ads.internal.task

fun interface ThreadPriorityHelper {
    /**
     * Computes Linux Process priority based on job info internal job priority
     *
     * @param jobInfo to compute priority for
     * @return [android.os.Process] thread priority
     */
    fun makeAndroidThreadPriority(jobInfo: JobInfo): Int
}