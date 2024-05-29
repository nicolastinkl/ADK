package com.vungle.ads.internal.util

import com.vungle.ads.BuildConfig
import java.util.concurrent.TimeUnit

class ConcurrencyTimeoutProvider {

    //Use Long.MAX_VALUE if in DEBUG mode
    private val OPERATION_TIMEOUT =
        if (BuildConfig.DEBUG) Long.MAX_VALUE else TimeUnit.SECONDS.toMillis(4)

    /**
     * Method to provide timeout in multithreading environment
     * based on current thread and DEBUG mode.
     * If in DEBUG or current thread is not main then Long.MAX_VALUE
     * will be used.
     *
     * @return timeout in millis
     */
    fun getTimeout(): Long {
        return if (ThreadUtil.isMainThread) OPERATION_TIMEOUT else Long.MAX_VALUE
    }
}
