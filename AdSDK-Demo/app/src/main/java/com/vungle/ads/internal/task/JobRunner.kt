package com.vungle.ads.internal.task

/*
 * Interface for Job executor
 * There is room for improvements
 * In case we need more criterias we may create info class and use in instead of 3 methods,
 * however method count with current implementation should be less then method count for new class.
 */
interface JobRunner {
    fun execute(jobInfo: JobInfo)
    fun cancelPendingJob(tag: String)
}