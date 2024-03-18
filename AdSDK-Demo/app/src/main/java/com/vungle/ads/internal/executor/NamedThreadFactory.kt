package com.vungle.ads.internal.executor

import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class NamedThreadFactory(private val name: String) : ThreadFactory {
    private val threadFactory = Executors.defaultThreadFactory()
    private val atomicInteger = AtomicInteger(0)
    override fun newThread(r: Runnable): Thread {
        val t = threadFactory.newThread(r)
        t.name = name + "-th-" + atomicInteger.incrementAndGet()
        return t
    }
}