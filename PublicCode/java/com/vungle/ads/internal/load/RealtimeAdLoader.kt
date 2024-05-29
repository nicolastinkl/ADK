package com.vungle.ads.internal.load

import android.content.Context
import com.vungle.ads.AdMarkupInvalidError
import com.vungle.ads.AnalyticsClient
import com.vungle.ads.VungleError
import com.vungle.ads.internal.downloader.Downloader
import com.vungle.ads.internal.executor.Executors
import com.vungle.ads.internal.network.TpatSender
import com.vungle.ads.internal.network.VungleApiClient
import com.vungle.ads.internal.omsdk.OMInjector
import com.vungle.ads.internal.util.PathProvider

class RealtimeAdLoader(
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
        val adMarkup = adRequest.adMarkup
        // Support V2 only.
        if (adMarkup == null) {
            AnalyticsClient.logError(
                VungleError.INVALID_BID_PAYLOAD,
                "Unable to create data object from payload string.",
                adRequest.placement.referenceId
            )
            onAdLoadFailed(AdMarkupInvalidError())
            return
        }

        val adPayload = adMarkup.getAdPayload()

        if (adMarkup.version != 2 || adPayload == null) {
            AnalyticsClient.logError(
                VungleError.INVALID_ADUNIT_BID_PAYLOAD,
                "The ad response did not contain valid ad markup.",
                adRequest.placement.referenceId,
                eventId = adMarkup.getEventId()
            )
            onAdLoadFailed(AdMarkupInvalidError())
            return
        }

        handleAdMetaData(adPayload)
    }

    override fun onAdLoadReady() {
        sendWinNotification(advertisement?.getWinNotifications())
    }

    private fun sendWinNotification(notifications: List<String>?) {
        if (notifications?.isEmpty() == true) {
            return
        }
        val tpatSender = TpatSender(
            vungleApiClient,
            adRequest.placement.referenceId,
            advertisement?.getCreativeId(),
            advertisement?.eventId(),
            sdkExecutors.ioExecutor,
            pathProvider,
        )
        notifications?.forEach { notificationUrl ->
            tpatSender.sendWinNotification(notificationUrl, sdkExecutors.jobExecutor)
        }
    }
}
