package com.vungle.ads

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.vungle.ads.internal.AdInternal
import com.vungle.ads.internal.load.AdLoaderCallback
import com.vungle.ads.internal.model.AdPayload
import com.vungle.ads.internal.protos.Sdk
import com.vungle.ads.internal.util.ThreadUtil

/**
 * This class has attributes that we would want to share with Publishers for
 * any Ad that they have requested for, and all relevant info related to  the
 * Ad available in response to the request made
 */
abstract class BaseAd(
    val context: Context,
    val placementId: String,
    val adConfig: AdConfig
) : Ad {

    var adListener : BaseAdListener? = null
    val adInternal: AdInternal by lazy { constructAdInternal(context) }
    /* Metrics */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal val requestToResponseMetric : TimeIntervalMetric =
        TimeIntervalMetric(Sdk.SDKMetric.SDKMetricType.AD_REQUEST_TO_RESPONSE_DURATION_MS)
    internal val responseToShowMetric : TimeIntervalMetric =
        TimeIntervalMetric(Sdk.SDKMetric.SDKMetricType.AD_RESPONSE_TO_SHOW_DURATION_MS)
    internal val showToDisplayMetric : TimeIntervalMetric =
        TimeIntervalMetric(Sdk.SDKMetric.SDKMetricType.AD_SHOW_TO_DISPLAY_DURATION_MS)
    internal val displayToClickMetric : OneShotTimeIntervalMetric =
        OneShotTimeIntervalMetric(Sdk.SDKMetric.SDKMetricType.AD_DISPLAY_TO_CLICK_DURATION_MS)

    internal abstract fun constructAdInternal(context: Context): AdInternal

    override fun canPlayAd(): Boolean {
        return adInternal.canPlayAd() == null
    }

    var creativeId: String? = null
        private set
    var eventId: String? = null
        private set

    override fun load(adMarkup: String?) {
        requestToResponseMetric.markStart()
        adInternal.loadAd(
            placementId, adMarkup,
            object : AdLoaderCallback {
                override fun onSuccess(advertisement: AdPayload) {
                    onAdLoaded(advertisement)
                    onLoadSuccess(this@BaseAd, adMarkup)
                }

                override fun onFailure(error: VungleError) {
                    onLoadFailure(this@BaseAd, error)
                }
            }
        )
    }

    internal open fun onAdLoaded(advertisement: AdPayload) {
        advertisement.adConfig = adConfig
        creativeId = advertisement.getCreativeId()
        eventId = advertisement.eventId()
    }

    internal open fun onLoadSuccess(baseAd: BaseAd, adMarkup: String?) {
        ThreadUtil.runOnUiThread {
            adListener?.onAdLoaded(this)
        }
        onLoadEnd()
    }

    internal open fun onLoadFailure(baseAd: BaseAd, vungleError: VungleError) {
        ThreadUtil.runOnUiThread {
            adListener?.onAdFailedToLoad(this, vungleError)
        }
        onLoadEnd()
    }

    /* Both success and failure trigger this call */
    private fun onLoadEnd() {
        requestToResponseMetric.markEnd()
        AnalyticsClient.logMetric(
            requestToResponseMetric, placementId, creativeId, eventId
        )
        responseToShowMetric.markStart()
    }
}


interface Ad {
    fun canPlayAd(): Boolean?
    fun load(adMarkup: String? = null)
}

interface FullscreenAd : Ad {
    fun play(context: Context? = null)
}
