package com.vungle.ads.internal.executor

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.TimeUnit

class SDKExecutors : Executors {
    private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()

    /**
     * Executor to execute IO file, db calls.
     */
    private var IO_EXECUTOR: VungleThreadPoolExecutor

    /**
     * Executor to execute business logic and wait for results from other executors.
     */
    private var BACKGROUND_EXECUTOR: VungleThreadPoolExecutor

    /**
     * Executor to execute tasks and jobs.
     */
    private var JOB_EXECUTOR: VungleThreadPoolExecutor

    /**
     * Executor to execute error and metrics.
     */
    private var LOGGER_EXECUTOR: VungleThreadPoolExecutor

    /**
     * Executor to execute long task to get user agent
     */
    private var UA_EXECUTOR: VungleThreadPoolExecutor

    /**
     * Executor to execute AssetDownloader tasks.
     */
    private var DOWNLOADER_EXECUTOR: VungleThreadPoolExecutor

    /**
     * Executor for offloading some work from main thread maintaining higher priority
     */
    private var OFFLOAD_EXECUTOR: VungleThreadPoolExecutor

    init {
        JOB_EXECUTOR = VungleThreadPoolExecutor(
            NUMBER_OF_CORES,
            NUMBER_OF_CORES,
            JOBS_KEEP_ALIVE_TIME_SECONDS.toLong(),
            TimeUnit.SECONDS,
            LinkedBlockingQueue(),
            NamedThreadFactory("vng_jr")
        )
        IO_EXECUTOR = VungleThreadPoolExecutor(
            SINGLE_CORE_POOL_SIZE,
            SINGLE_CORE_POOL_SIZE,
            IO_KEEP_ALIVE_TIME_SECONDS.toLong(),
            TimeUnit.SECONDS,
            LinkedBlockingQueue(),
            NamedThreadFactory("vng_io")
        )
        LOGGER_EXECUTOR = VungleThreadPoolExecutor(
            SINGLE_CORE_POOL_SIZE,
            SINGLE_CORE_POOL_SIZE,
            VUNGLE_KEEP_ALIVE_TIME_SECONDS.toLong(),
            TimeUnit.SECONDS,
            LinkedBlockingQueue(),
            NamedThreadFactory("vng_logger")
        )
        BACKGROUND_EXECUTOR = VungleThreadPoolExecutor(
            SINGLE_CORE_POOL_SIZE,
            SINGLE_CORE_POOL_SIZE,
            VUNGLE_KEEP_ALIVE_TIME_SECONDS.toLong(),
            TimeUnit.SECONDS,
            LinkedBlockingQueue(),
            NamedThreadFactory("vng_background")
        )
        UA_EXECUTOR = VungleThreadPoolExecutor(
            SINGLE_CORE_POOL_SIZE,
            SINGLE_CORE_POOL_SIZE,
            VUNGLE_KEEP_ALIVE_TIME_SECONDS.toLong(),
            TimeUnit.SECONDS,
            LinkedBlockingQueue(),
            NamedThreadFactory("vng_ua")
        )
        DOWNLOADER_EXECUTOR = VungleThreadPoolExecutor(
            4,
            4,
            1,
            TimeUnit.SECONDS,
            PriorityBlockingQueue(),
            NamedThreadFactory("vng_down")
        )
        OFFLOAD_EXECUTOR = VungleThreadPoolExecutor(
            SINGLE_CORE_POOL_SIZE,
            SINGLE_CORE_POOL_SIZE,
            VUNGLE_KEEP_ALIVE_TIME_SECONDS.toLong(),
            TimeUnit.SECONDS,
            LinkedBlockingQueue(),
            NamedThreadFactory("vng_ol")
        )
    }

    override val backgroundExecutor: VungleThreadPoolExecutor
        get() = BACKGROUND_EXECUTOR
    override val ioExecutor: VungleThreadPoolExecutor
        get() = IO_EXECUTOR
    override val jobExecutor: VungleThreadPoolExecutor
        get() = JOB_EXECUTOR
    override val loggerExecutor: VungleThreadPoolExecutor
        get() = LOGGER_EXECUTOR
    override val offloadExecutor: VungleThreadPoolExecutor
        get() = OFFLOAD_EXECUTOR
    override val uaExecutor: VungleThreadPoolExecutor
        get() = UA_EXECUTOR
    override val downloaderExecutor: VungleThreadPoolExecutor
        get() = DOWNLOADER_EXECUTOR

    companion object {
        private const val SINGLE_CORE_POOL_SIZE = 1
        private const val IO_KEEP_ALIVE_TIME_SECONDS = 5
        private const val VUNGLE_KEEP_ALIVE_TIME_SECONDS = 10
        private const val JOBS_KEEP_ALIVE_TIME_SECONDS = 1
    }
}
