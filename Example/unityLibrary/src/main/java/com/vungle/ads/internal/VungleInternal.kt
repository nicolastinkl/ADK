package com.vungle.ads.internal

import android.content.Context
import com.vungle.ads.BuildConfig
import com.vungle.ads.ServiceLocator
import com.vungle.ads.internal.bidding.BidTokenEncoder
import com.vungle.ads.internal.executor.FutureResult
import com.vungle.ads.internal.executor.SDKExecutors
import com.vungle.ads.internal.util.ConcurrencyTimeoutProvider
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit


internal class VungleInternal {

    /**
     * Will return an encoded string of advertisement bid tokens.
     * This method might be called from adapter side.
     *
     * Optional: Pass in null or empty String to make this filter return all encoded advertisements.

     * @return an encoded string contains available bid tokens digest. In rare cases, this can return null value
     */
    fun getAvailableBidTokens(context: Context): String? {
        val provider: ConcurrencyTimeoutProvider by ServiceLocator.inject(context)
        val sdkExecutors: SDKExecutors by ServiceLocator.inject(context)
        val bidTokenEncoder: BidTokenEncoder by ServiceLocator.inject(context)
        val futureResult: FutureResult<String?> = FutureResult(sdkExecutors.ioExecutor
            .submit(Callable<String?> {
                bidTokenEncoder.encode()
            })
        )
        return futureResult.get(provider.getTimeout(), TimeUnit.MILLISECONDS)
    }

    fun getSdkVersion() = BuildConfig.VERSION_NAME
}
