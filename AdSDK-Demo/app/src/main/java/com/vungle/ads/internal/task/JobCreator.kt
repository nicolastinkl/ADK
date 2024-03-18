package com.vungle.ads.internal.task

fun interface JobCreator {
    @Throws(UnknownTagException::class)
    fun create(tag: String): Job
}