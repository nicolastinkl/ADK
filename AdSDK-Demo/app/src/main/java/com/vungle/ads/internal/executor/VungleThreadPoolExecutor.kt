package com.vungle.ads.internal.executor

import java.util.concurrent.*

class VungleThreadPoolExecutor(
    corePoolSize: Int,
    maximumPoolSize: Int,
    keepAliveTime: Long,
    unit: TimeUnit?,
    workQueue: BlockingQueue<Runnable?>?,
    threadFactory: ThreadFactory?
) : ThreadPoolExecutor(
    corePoolSize,
    maximumPoolSize,
    keepAliveTime,
    unit,
    workQueue,
    threadFactory
) {
    override fun execute(command: Runnable) {
        try {
            super.execute(command)
        } catch (ignored: OutOfMemoryError) {
            //ignored
        }
    }

    fun execute(command: Runnable, fail: Runnable?) {
        try {
            super.execute(command)
        } catch (ignored: OutOfMemoryError) {
            fail?.run()
        }
    }

    override fun submit(task: Runnable): Future<*> {
        return try {
            super.submit(task)
        } catch (ignored: OutOfMemoryError) {
            FutureResult<Any>(null)
        }
    }

    override fun <T> submit(task: Runnable, result: T): Future<T> {
        return try {
            super.submit(task, result)
        } catch (ignored: OutOfMemoryError) {
            return FutureResult<T>(null) as Future<T>
        }
    }

    fun submit(task: Runnable, fail: Runnable?): Future<*> {
        return try {
            super.submit(task)
        } catch (ignored: OutOfMemoryError) {
            fail?.run()
            FutureResult<Any>(null)
        }
    }

    override fun <T> submit(task: Callable<T>): Future<T> {
        return try {
            super.submit(task)
        } catch (ignored: OutOfMemoryError) {
            return FutureResult<T>(null) as Future<T>
        }
    }

    init {
        allowCoreThreadTimeOut(true)
    }
}