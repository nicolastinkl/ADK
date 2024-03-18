package com.vungle.ads.internal.task

import android.os.Bundle
import androidx.annotation.IntDef

fun interface Job {
    @IntDef(Result.SUCCESS, Result.FAILURE, Result.RESCHEDULE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Result {
        companion object {
            const val SUCCESS = 0
            const val FAILURE = 1
            const val RESCHEDULE = 2
        }
    }

    @Result
    fun onRunJob(bundle: Bundle, jobRunner: JobRunner): Int
}
