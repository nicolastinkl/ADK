package com.vungle.ads.internal.executor

interface Executors {
    val backgroundExecutor: VungleThreadPoolExecutor
    val ioExecutor: VungleThreadPoolExecutor
    val jobExecutor: VungleThreadPoolExecutor
    val loggerExecutor: VungleThreadPoolExecutor
    val offloadExecutor: VungleThreadPoolExecutor
    val uaExecutor: VungleThreadPoolExecutor
    val downloaderExecutor: VungleThreadPoolExecutor
}
