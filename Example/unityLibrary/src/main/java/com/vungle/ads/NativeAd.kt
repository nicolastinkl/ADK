package com.vungle.ads

import android.app.Application
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.IntDef
import com.vungle.ads.ServiceLocator.Companion.inject
import com.vungle.ads.internal.AdInternal
import com.vungle.ads.internal.Constants
import com.vungle.ads.internal.Constants.CHECKPOINT_0
import com.vungle.ads.internal.ImpressionTracker
import com.vungle.ads.internal.executor.Executors
import com.vungle.ads.internal.model.AdPayload
import com.vungle.ads.internal.model.Placement
import com.vungle.ads.internal.presenter.AdEventListener
import com.vungle.ads.internal.presenter.AdPlayCallback
import com.vungle.ads.internal.presenter.NativeAdPresenter
import com.vungle.ads.internal.presenter.NativePresenterDelegate
import com.vungle.ads.internal.protos.Sdk
import com.vungle.ads.internal.ui.WatermarkView
import com.vungle.ads.internal.ui.view.MediaView
import com.vungle.ads.internal.util.ImageLoader
import com.vungle.ads.internal.util.ThreadUtil

class NativeAd private constructor(context: Context, placementId: String, adConfig: AdConfig) :
    BaseAd(context, placementId, adConfig) {

    constructor(context: Context, placementId: String) : this(context, placementId, AdConfig()) {
        // We need activity context to show GDPR dialog(if required) and launch Google Play Store
        if (context is Application) throw InternalError(
            -1000,
            "Activity context is required to create NativeAd instance."
        )
    }

    companion object {
        const val TOP_LEFT = 0
        const val TOP_RIGHT = 1
        const val BOTTOM_LEFT = 2
        const val BOTTOM_RIGHT = 3
    }

    override fun constructAdInternal(context: Context): NativeAdInternal = NativeAdInternal(context)

    private val imageLoader by lazy { ImageLoader.instance.also { it.init(executors.ioExecutor) } }
    private val executors: Executors by inject(context)

    private var nativeAdAssetMap: MutableMap<String, String>? = null
    private var adIconView: ImageView? = null
    private var adContentView: MediaView? = null
    private val impressionTracker by lazy { ImpressionTracker(context) }

    // Root view for rendering our privacy icon.
    private var adRootView: FrameLayout? = null
    private var clickableViews: Collection<View>? = null
    private var adOptionsView: NativeAdOptionsView
    private var presenter: NativeAdPresenter? = null

    @IntDef(TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT)
    annotation class AdOptionsPosition

    @AdOptionsPosition
    var adOptionsPosition = TOP_RIGHT

    init {
        adOptionsView = NativeAdOptionsView(context)
    }

    override fun onAdLoaded(advertisement: AdPayload) {
        super.onAdLoaded(advertisement)
        nativeAdAssetMap = advertisement.getMRAIDArgsInMap()
    }

    private val adPlayCallback = object : AdPlayCallback {
        override fun onAdStart(id: String?) {
            adInternal.adState = AdInternal.AdState.PLAYING
            ThreadUtil.runOnUiThread {
                adListener?.onAdStart(this@NativeAd)
            }
        }

        override fun onAdImpression(id: String?) {
            ThreadUtil.runOnUiThread {
                adListener?.onAdImpression(this@NativeAd)
            }
            showToDisplayMetric.markEnd()
            AnalyticsClient.logMetric(showToDisplayMetric, placementId, creativeId, eventId)
            displayToClickMetric.markStart()
        }

        override fun onAdEnd(id: String?) {
            adInternal.adState = AdInternal.AdState.FINISHED
            ThreadUtil.runOnUiThread {
                adListener?.onAdEnd(this@NativeAd)
            }
        }

        override fun onAdClick(id: String?) {
            ThreadUtil.runOnUiThread {
                adListener?.onAdClicked(this@NativeAd)
            }
            displayToClickMetric.markEnd()
            AnalyticsClient.logMetric(displayToClickMetric, placementId, creativeId, eventId)
        }

        override fun onAdRewarded(id: String?) {
            //no-op
        }

        override fun onAdLeftApplication(id: String?) {
            ThreadUtil.runOnUiThread {
                adListener?.onAdLeftApplication(this@NativeAd)
            }
        }

        override fun onFailure(error: VungleError) {
            adInternal.adState = AdInternal.AdState.ERROR
            ThreadUtil.runOnUiThread {
                adListener?.onAdFailedToPlay(this@NativeAd, error)
            }
        }
    }

    /**
     * app icon url
     */
    fun getAppIcon() = nativeAdAssetMap?.get(NativeAdInternal.TOKEN_APP_ICON) ?: ""

    /**
     * app name
     */
    fun getAdTitle() = nativeAdAssetMap?.get(NativeAdInternal.TOKEN_APP_NAME) ?: ""

    /**
     * the ad description
     */
    fun getAdBodyText() = nativeAdAssetMap?.get(NativeAdInternal.TOKEN_APP_DESCRIPTION) ?: ""

    /**
     * call to action phrase
     */
    fun getAdCallToActionText() =
        nativeAdAssetMap?.get(NativeAdInternal.TOKEN_CTA_BUTTON_TEXT) ?: ""

    /**
     * app rating
     */
    fun getAdStarRating(): Double? {
        val ratingValue = nativeAdAssetMap?.get(NativeAdInternal.TOKEN_APP_RATING_VALUE) ?: ""
        return if (!TextUtils.isEmpty(ratingValue)) {
            try {
                java.lang.Double.valueOf(ratingValue)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    /**
     * sponsored text
     */
    fun getAdSponsoredText() = nativeAdAssetMap?.get(NativeAdInternal.TOKEN_SPONSORED_BY) ?: ""

    /**
     * Whether call to action url exists.
     */
    fun hasCallToAction() = getCtaUrl().isNotEmpty()

    /**
     * Vungle privacy icon url
     */
    internal fun getPrivacyIconUrl() =
        nativeAdAssetMap?.get(NativeAdInternal.TOKEN_VUNGLE_PRIVACY_ICON_URL) ?: ""

    /**
     * Vungle privacy target url
     */
    internal fun getPrivacyUrl() =
        nativeAdAssetMap?.get(NativeAdInternal.TOKEN_VUNGLE_PRIVACY_URL) ?: ""

    /**
     * Call to action url
     */
    internal fun getCtaUrl() = nativeAdAssetMap?.get(NativeAdInternal.TOKEN_CTA_BUTTON_URL) ?: ""

    private fun getMainImagePath() = nativeAdAssetMap?.get(NativeAdInternal.TOKEN_MAIN_IMAGE) ?: ""

    fun unregisterView() {
        if (adInternal.adState == AdInternal.AdState.FINISHED) {
            return
        }

        clickableViews?.forEach {
            it.setOnClickListener(null)
        }

        nativeAdAssetMap?.clear()
        impressionTracker.destroy()
        adContentView?.destroy()
        adOptionsView.destroy()
        presenter?.detach()
    }

    /**
     * Register the given view for this NativeAd to handle impressions and clicks.
     *
     * @param rootView       NativeAd root layout, should containing NativeAd for display
     * @param mediaView      NativeAd content view such as Image, Video
     * @param adIconView     App Icon view
     * @param clickableViews a list of all views that should handle taps event
     */
    fun registerViewForInteraction(
        rootView: FrameLayout,
        mediaView: MediaView,
        adIconView: ImageView?,
        clickableViews: Collection<View>?
    ) {
        AnalyticsClient.logMetric(
            SingleValueMetric(Sdk.SDKMetric.SDKMetricType.PLAY_AD_API),
            placementId, creativeId, eventId
        )
        val error = adInternal.canPlayAd(true)
        if (error != null) {
            if (adInternal.isErrorTerminal(error.code)) {
                adInternal.adState = AdInternal.AdState.ERROR
                nativeAdAssetMap?.clear()
            }
            adListener?.onAdFailedToPlay(
                this@NativeAd,
                error
            )
            return
        }

        responseToShowMetric.markEnd()
        AnalyticsClient.logMetric(responseToShowMetric, placementId, creativeId, eventId)
        showToDisplayMetric.markStart()

        this.adRootView = rootView
        this.adContentView = mediaView
        this.adIconView = adIconView
        this.clickableViews = clickableViews

        presenter =
            NativeAdPresenter(
                context,
                adInternal as NativePresenterDelegate,
                adInternal.advertisement,
                executors.jobExecutor
            )
        val omSdkData = nativeAdAssetMap?.get(NativeAdInternal.TOKEN_OM_SDK_DATA) ?: ""
        presenter?.initOMTracker(omSdkData)
        presenter?.startTracking(rootView)

        presenter?.setEventListener(AdEventListener(adPlayCallback, adInternal.placement))

        adOptionsView.setOnClickListener {
            presenter?.processCommand(NativeAdPresenter.OPEN_PRIVACY, getPrivacyUrl())
        }
        (clickableViews ?: listOf(mediaView)).forEach {
            it.setOnClickListener {
                presenter?.processCommand(NativeAdPresenter.DOWNLOAD, getCtaUrl())
            }
        }

        //render privacy icon
        adOptionsView.renderTo(rootView, adOptionsPosition)

        // track impression
        impressionTracker.addView(rootView) {
            presenter?.processCommand(NativeAdPresenter.VIDEO_VIEWED)
            presenter?.processCommand(NativeAdPresenter.TPAT, CHECKPOINT_0)
            presenter?.onImpression()
        }

        displayImage(getMainImagePath(), mediaView.getMainImage())
        displayImage(getAppIcon(), adIconView)
        displayImage(getPrivacyIconUrl(), adOptionsView.getPrivacyIcon())

        adConfig.getWatermark()?.let {
            val imageView = WatermarkView(rootView.context, it)
            rootView.addView(imageView)
            imageView.bringToFront()
        }

        presenter?.prepare()
    }

    /**
     * Perform the CTA Action on Click event.
     */
    fun performCTA() {
        presenter?.processCommand(NativeAdPresenter.DOWNLOAD, getCtaUrl())
    }

    private fun displayImage(path: String?, imageView: ImageView?) {
        imageLoader.displayImage(path) {
            if (imageView != null) {
                ThreadUtil.runOnUiThread {
                    imageView.setImageBitmap(it)
                }
            }
        }
    }
}

internal class NativeAdInternal(context: Context) : AdInternal(context), NativePresenterDelegate {
    companion object {
        const val TOKEN_APP_NAME = "APP_NAME"
        const val TOKEN_APP_DESCRIPTION = "APP_DESCRIPTION"
        const val TOKEN_CTA_BUTTON_TEXT = "CTA_BUTTON_TEXT"
        const val TOKEN_CTA_BUTTON_URL = "CTA_BUTTON_URL"
        const val TOKEN_APP_RATING_VALUE = "APP_RATING_VALUE"
        const val TOKEN_SPONSORED_BY = "SPONSORED_BY"
        const val TOKEN_VUNGLE_PRIVACY_ICON_URL = "VUNGLE_PRIVACY_ICON_URL"
        const val TOKEN_VUNGLE_PRIVACY_URL = "VUNGLE_PRIVACY_URL"
        const val TOKEN_APP_ICON = "APP_ICON"
        const val TOKEN_MAIN_IMAGE = "MAIN_IMAGE"
        const val TOKEN_OM_SDK_DATA = "OM_SDK_DATA"
    }

    override fun getPlacementRefId(): String? {
        return placement?.referenceId
    }

    override fun getImpressionUrls(): List<String>? {
        return bidPayload?.impression
    }

    override fun isValidAdTypeForPlacement(placement: Placement): Boolean {
        return placement.isNative()
    }

    override fun isValidAdSize(adSize: String): Boolean {
        return true // Don't care about the adSize for native ad type
    }

    override fun getAdSizeForAdRequest(): String {
        return Constants.AD_REQUEST_DEFAULT_SIZE
    }
}
