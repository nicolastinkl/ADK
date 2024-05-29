package com.vungle.ads

import android.content.Context
import com.vungle.ads.internal.AdInternal
import com.vungle.ads.internal.Constants
import com.vungle.ads.internal.presenter.AdPlayCallback
import com.vungle.ads.internal.protos.Sdk
import com.vungle.ads.internal.util.ThreadUtil

abstract class BaseFullscreenAd(context: Context, placementId: String, adConfig: AdConfig) :
    BaseAd(context, placementId, adConfig), FullscreenAd {

    override fun play(context: Context?) {
        AnalyticsClient.logMetric(
            SingleValueMetric(Sdk.SDKMetric.SDKMetricType.PLAY_AD_API),
            placementId, creativeId, eventId
        )
        responseToShowMetric.markEnd()
        AnalyticsClient.logMetric(responseToShowMetric, placementId, creativeId, eventId)
        showToDisplayMetric.markStart()
        adInternal.play(context, object : AdPlayCallback {
            override fun onAdStart(id: String?) {
                ThreadUtil.runOnUiThread {
                    adListener?.onAdStart(this@BaseFullscreenAd)
                }
            }

            override fun onAdImpression(id: String?) {
                ThreadUtil.runOnUiThread {
                    adListener?.onAdImpression(this@BaseFullscreenAd)
                }
                showToDisplayMetric.markEnd()
                AnalyticsClient.logMetric(showToDisplayMetric, placementId, creativeId, eventId)
                displayToClickMetric.markStart()
            }

            override fun onAdEnd(id: String?) {
                ThreadUtil.runOnUiThread {
                    adListener?.onAdEnd(this@BaseFullscreenAd)
                }
            }

            override fun onAdClick(id: String?) {
                ThreadUtil.runOnUiThread {
                    adListener?.onAdClicked(this@BaseFullscreenAd)
                }

                displayToClickMetric.markEnd()
                AnalyticsClient.logMetric(displayToClickMetric, placementId, creativeId, eventId)
            }

            override fun onAdRewarded(id: String?) {
                ThreadUtil.runOnUiThread {
                    val rewardedAdListener: RewardedAdListener? =
                        adListener as? RewardedAdListener
                    rewardedAdListener?.onAdRewarded(this@BaseFullscreenAd)
                }
            }

            override fun onAdLeftApplication(id: String?) {
                ThreadUtil.runOnUiThread {
                    adListener?.onAdLeftApplication(this@BaseFullscreenAd)
                }
            }

            override fun onFailure(error: VungleError) {
                ThreadUtil.runOnUiThread {
                    adListener?.onAdFailedToPlay(this@BaseFullscreenAd, error)
                }
            }
        })
    }

}

internal abstract class FullscreenAdInternal(context: Context) : AdInternal(context) {
    override fun isValidAdSize(adSize: String): Boolean {
        return true // Don't care about the adSize for fullscreen ad type
    }

    override fun getAdSizeForAdRequest(): String {
        return Constants.AD_REQUEST_DEFAULT_SIZE
    }


}
