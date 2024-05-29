package com.vungle.ads.internal.task

import android.content.Context
import com.vungle.ads.internal.util.PathProvider

class VungleJobCreator(val context: Context, val pathProvider: PathProvider) : JobCreator {

    @Throws(UnknownTagException::class)
    override fun create(tag: String): Job {
        if (tag.isEmpty()) {
            throw UnknownTagException("Job tag is null")
        }
        return when (tag) {
            CleanupJob.TAG -> CleanupJob(context, pathProvider)
            ResendTpatJob.TAG -> ResendTpatJob(context, pathProvider)
            else -> throw UnknownTagException("Unknown Job Type $tag")
        }
    }
}
