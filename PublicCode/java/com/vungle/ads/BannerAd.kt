package com.vungle.ads

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.vungle.ads.internal.AdInternal
import com.vungle.ads.internal.model.AdPayload
import com.vungle.ads.internal.model.Placement
import com.vungle.ads.internal.presenter.AdPlayCallback
import com.vungle.ads.internal.presenter.AdPlayCallbackWrapper
import com.vungle.ads.internal.protos.Sdk
import com.vungle.ads.internal.util.Logger
import com.vungle.ads.internal.util.ThreadUtil

class BannerAd private constructor(
    context: Context,
    placementId: String,
    private var adSize: BannerAdSize,
    adConfig: AdConfig
) :
    BaseAd(context, placementId, adConfig) {

    constructor(context: Context, placementId: String, adSize: BannerAdSize) : this(
        context,
        placementId,
        adSize,
        AdConfig()
    )

    private var bannerView: BannerView? = null

    override fun constructAdInternal(context: Context): BannerAdInternal =
        BannerAdInternal(context, adSize)

    private val adPlayCallback = (adInternal as BannerAdInternal).wrapCallback(
        object : AdPlayCallback {
            override fun onAdStart(id: String?) {
                ThreadUtil.runOnUiThread {
                    adListener?.onAdStart(this@BannerAd)
                }
            }

            override fun onAdImpression(id: String?) {
                ThreadUtil.runOnUiThread {
                    adListener?.onAdImpression(this@BannerAd)
                }
                showToDisplayMetric.markEnd()
                AnalyticsClient.logMetric(showToDisplayMetric, placementId, creativeId, eventId)
                displayToClickMetric.markStart()
            }

            override fun onAdEnd(id: String?) {
                ThreadUtil.runOnUiThread {
                    adListener?.onAdEnd(this@BannerAd)
                }
            }

            override fun onAdClick(id: String?) {
                ThreadUtil.runOnUiThread {
                    adListener?.onAdClicked(this@BannerAd)
                }
                displayToClickMetric.markEnd()
                AnalyticsClient.logMetric(displayToClickMetric, placementId, creativeId, eventId)
            }

            override fun onAdRewarded(id: String?) {
                // no-op
            }

            override fun onAdLeftApplication(id: String?) {
                ThreadUtil.runOnUiThread {
                    adListener?.onAdLeftApplication(this@BannerAd)
                }
            }

            override fun onFailure(error: VungleError) {
                ThreadUtil.runOnUiThread {
                    adListener?.onAdFailedToPlay(this@BannerAd, error)
                }
            }
        })

    fun finishAd() {
        bannerView?.finishAdInternal(true)
    }

    fun getBannerView(): BannerView? {
        AnalyticsClient.logMetric(SingleValueMetric(Sdk.SDKMetric.SDKMetricType.PLAY_AD_API),
            placementId, creativeId, eventId)
        if (bannerView != null) {
            return bannerView
        }
        val error = adInternal.canPlayAd(true)
        if (error != null) {
            if (adInternal.isErrorTerminal(error.code)) {
                adInternal.adState = AdInternal.AdState.ERROR
            }
            ThreadUtil.runOnUiThread {
                adListener?.onAdFailedToPlay(
                    this@BannerAd,
                    error
                )
            }
            return null
        }

        // the advertisement already be checked in canPlayAd() function
        val advertisement = adInternal.advertisement ?: return null
        val placement = adInternal.placement ?: return null

        adInternal.cancelDownload()

        try {
            bannerView = BannerView(
                context,
                placement,
                advertisement,
                adSize,
                adConfig,
                adPlayCallback,
                adInternal.bidPayload
            )
        } catch (ex: InstantiationException) {
            Logger.e("BannerAd", "Can not create banner view: ${ex.message}", ex)
            return null
        } finally {
            responseToShowMetric.markEnd()
            AnalyticsClient.logMetric(responseToShowMetric, placementId, creativeId, eventId)
        }

        showToDisplayMetric.markStart()

        return bannerView
    }
}

internal class BannerAdInternal(context: Context, private val adSize: BannerAdSize) :
    AdInternal(context) {
    internal fun wrapCallback(adPlayCallback: AdPlayCallback) =
        object : AdPlayCallbackWrapper(adPlayCallback) {
            override fun onAdStart(id: String?) {
                adState = AdState.PLAYING
                super.onAdStart(id)
            }

            override fun onAdEnd(id: String?) {
                adState = AdState.FINISHED
                super.onAdEnd(id)
            }

            override fun onFailure(error: VungleError) {
                adState = AdState.ERROR
                super.onFailure(error)
            }
        }

    override fun isValidAdTypeForPlacement(placement: Placement): Boolean {
        return placement.isBanner()
    }

    override fun isValidAdSize(adSize: String): Boolean {
        var validSize = isBannerAdSize(adSize)
        if (validSize && placement?.isMREC() == true && adSize != BannerAdSize.VUNGLE_MREC.sizeName) {
            validSize = false
        } else if (validSize && placement?.isBannerNonMREC() == true && adSize == BannerAdSize.VUNGLE_MREC.sizeName) {
            validSize = false
        }

        if (!validSize) {
            AnalyticsClient.logError(
                VungleError.BANNER_VIEW_INVALID_SIZE,
                "Invalidate size $adSize for banner ad",
                placement?.referenceId,
                eventId = advertisement?.eventId(),
            )
        }

        return validSize
    }

    override fun getAdSizeForAdRequest(): String {
        return adSize.sizeName
    }

    override fun adLoadedAndUpdateConfigure(advertisement: AdPayload) {
        super.adLoadedAndUpdateConfigure(advertisement)
        advertisement.adSize = adSize
    }

    @VisibleForTesting
    internal fun isBannerAdSize(adSize: String): Boolean {
        return adSize == BannerAdSize.BANNER.sizeName || adSize == BannerAdSize.BANNER_LEADERBOARD.sizeName
                || adSize == BannerAdSize.BANNER_SHORT.sizeName || adSize == BannerAdSize.VUNGLE_MREC.sizeName
    }

}
