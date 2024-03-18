package com.vungle.ads.internal.load

import android.content.Context
import android.util.Log
import com.vungle.ads.*
import com.vungle.ads.AdRetryActiveError
import com.vungle.ads.AdRetryError
import com.vungle.ads.AnalyticsClient
import com.vungle.ads.InternalError
import com.vungle.ads.VungleError
import com.vungle.ads.VungleError.Companion.NETWORK_ERROR
import com.vungle.ads.VungleError.Companion.NETWORK_TIMEOUT
import com.vungle.ads.internal.downloader.Downloader
import com.vungle.ads.internal.executor.Executors
import com.vungle.ads.internal.model.AdPayload
import com.vungle.ads.internal.model.Placement
import com.vungle.ads.internal.network.Call
import com.vungle.ads.internal.network.Callback
import com.vungle.ads.internal.network.Response
import com.vungle.ads.internal.network.VungleApiClient
import com.vungle.ads.internal.omsdk.OMInjector
import com.vungle.ads.internal.util.PathProvider
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class DefaultAdLoader(
    context: Context,
    vungleApiClient: VungleApiClient,
    sdkExecutors: Executors,
    omInjector: OMInjector,
    downloader: Downloader,
    pathProvider: PathProvider,
    adRequest: AdRequest
) : BaseAdLoader(
    context, vungleApiClient, sdkExecutors, omInjector, downloader, pathProvider, adRequest
) {

    override fun requestAd() {
        fetchAdMetadata(adRequest.requestAdSize, adRequest.placement)
    }

    override fun onAdLoadReady() {
        // Do nothing.
    }

    private fun fetchAdMetadata(
        adSize: String,
        placement: Placement
    ) {
        if (vungleApiClient.checkIsRetryAfterActive(placement.referenceId)) {
            onAdLoadFailed(AdRetryActiveError().logError())
            return
        }
        val adsCall = vungleApiClient.requestAd(
            placement.referenceId, adSize, placement.headerBidding
        )
        if (adsCall == null) {
            onAdLoadFailed(AdFailedToDownloadError())
            return
        }
        adsCall.enqueue(object : Callback<AdPayload> {
            override fun onResponse(call: Call<AdPayload>?, response: Response<AdPayload>?) {

                sdkExecutors.backgroundExecutor.execute {
                    if (vungleApiClient.getRetryAfterHeaderValue(placement.referenceId) > 0) {
                        onAdLoadFailed(AdRetryError().logError())
                        return@execute
                    }
                    if (response?.isSuccessful == false) {
                        AnalyticsClient.logError(
                            VungleError.API_FAILED_STATUS_CODE,
                            "Failed to get a successful response from the API call",
                            placement.referenceId
                        )
                        onAdLoadFailed(NoServeError())
                        return@execute
                    }
                    val adPayload = response?.body()
                    //Log.d("adPayload",response?.body().toString() +"")
                    if (adPayload?.adUnit() == null) {
                        AnalyticsClient.logError(
                            VungleError.AD_RESPONSE_EMPTY, "Ad response is empty.",
                            placement.referenceId
                        )
                        onAdLoadFailed(
                            NoServeError()
                        )
                        return@execute
                    }

                    handleAdMetaData(adPayload)
                }
            }

            override fun onFailure(call: Call<AdPayload>?, t: Throwable?) {
                sdkExecutors.backgroundExecutor.execute {
                    val error: VungleError = retrofitToVungleError(t)
                    onAdLoadFailed(error)

                    when (error.code) {
                        NETWORK_TIMEOUT -> {
                            AnalyticsClient.logError(
                                VungleError.AD_RESPONSE_TIMED_OUT,
                                "Timeout for ads call.",
                                placement.referenceId,
                                advertisement?.getCreativeId(),
                                advertisement?.eventId(),
                            )
                        }

                        NETWORK_ERROR -> {
                            AnalyticsClient.logError(
                                VungleError.API_REQUEST_ERROR,
                                "Ads request error.",
                                placement.referenceId,
                                advertisement?.getCreativeId(),
                                advertisement?.eventId(),
                            )
                        }

                        else -> {
                            AnalyticsClient.logError(
                                VungleError.API_RESPONSE_DECODE_ERROR,
                                "Unable to decode ads response.",
                                placement.referenceId,
                                advertisement?.getCreativeId(),
                                advertisement?.eventId(),
                            )
                        }
                    }

                }
            }
        })

    }

    private fun retrofitToVungleError(throwable: Throwable?): VungleError {
        return when (throwable) {
            is UnknownHostException -> {
                AdFailedToDownloadError()
            }

            is SocketTimeoutException -> {
                InternalError(NETWORK_TIMEOUT)
            }

            is IOException -> {
                InternalError(NETWORK_ERROR)
            }

            else -> {
                AdFailedToDownloadError()
            }
        }
    }

}
