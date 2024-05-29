package com.vungle.ads.internal.task

import android.content.Context
import android.os.Bundle
import com.vungle.ads.ServiceLocator.Companion.inject
import com.vungle.ads.internal.executor.Executors
import com.vungle.ads.internal.network.TpatSender
import com.vungle.ads.internal.network.VungleApiClient
import com.vungle.ads.internal.task.Job.Result.Companion.SUCCESS
import com.vungle.ads.internal.util.PathProvider

/** */
class ResendTpatJob
internal constructor(val context: Context, val pathProvider: PathProvider) : Job {
    override fun onRunJob(bundle: Bundle, jobRunner: JobRunner): Int {
        val vungleApiClient: VungleApiClient by inject(context)
        val executors: Executors by inject(context)

        val tpatSender = TpatSender(
            vungleApiClient = vungleApiClient,
            placementId = null,
            creativeId = null,
            eventId = null,
            ioExecutor = executors.ioExecutor,
            pathProvider = pathProvider)
        tpatSender.resendStoredTpats(executors.jobExecutor)

        return SUCCESS
    }

    companion object {
        const val TAG: String = "ResendTpatJob"

        fun makeJobInfo(): JobInfo {
            return JobInfo(TAG)
                .setPriority(JobInfo.Priority.LOWEST)
                .setUpdateCurrent(true)
        }
    }
}