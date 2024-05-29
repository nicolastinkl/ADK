package com.vungle.ads

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.RelativeLayout
import com.vungle.ads.ServiceLocator.Companion.inject
import com.vungle.ads.internal.ConfigManager
import com.vungle.ads.internal.ImpressionTracker
import com.vungle.ads.internal.executor.Executors
import com.vungle.ads.internal.model.AdPayload
import com.vungle.ads.internal.model.BidPayload
import com.vungle.ads.internal.model.Placement
import com.vungle.ads.internal.omsdk.OMTracker
import com.vungle.ads.internal.presenter.AdEventListener
import com.vungle.ads.internal.presenter.AdPlayCallback
import com.vungle.ads.internal.presenter.MRAIDPresenter
import com.vungle.ads.internal.ui.VungleWebClient
import com.vungle.ads.internal.ui.WatermarkView
import com.vungle.ads.internal.ui.view.MRAIDAdWidget
import com.vungle.ads.internal.util.ViewUtility.dpToPixels
import java.util.concurrent.atomic.AtomicBoolean

class BannerView @Throws(InstantiationException::class) constructor(
    context: Context,
    placement: Placement,
    advertisement: AdPayload,
    adSize: BannerAdSize,
    adConfig: AdConfig,
    adPlayCallback: AdPlayCallback,
    bidPayload: BidPayload?
) : RelativeLayout(context) {

    private var calculatedPixelWidth = 0
    private var calculatedPixelHeight = 0

    private var adWidget: MRAIDAdWidget? = null
    private var presenter: MRAIDPresenter? = null
    private var imageView: WatermarkView? = null

    private var isOnImpressionCalled: Boolean = false
    private val destroyed = AtomicBoolean(false)
    private val presenterStarted = AtomicBoolean(false)

    private val impressionTracker by lazy { ImpressionTracker(context) }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)

        setAdVisibility(visibility == VISIBLE)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d(TAG, "onAttachedToWindow()")
        if (!presenterStarted.getAndSet(true)) {
            presenter?.prepare()
            presenter?.start()

            impressionTracker.addView(this) {
                Log.d(TAG, "ImpressionTracker checked the banner view become visible.")
                isOnImpressionCalled = true

                setAdVisibility(visibility == VISIBLE)
            }
        }

        renderAd()
    }

    private fun setAdVisibility(isVisible: Boolean) {
        if (!isOnImpressionCalled) {
            return
        }

        // The visibility change must be triggered after impression fired.
        if (!destroyed.get()) {
            presenter?.setAdVisibility(isVisible)
        }
    }

    fun finishAdInternal(isFinishedByApi: Boolean) {
        if (destroyed.get()) {
            return
        }
        destroyed.set(true)
        val flag = (MRAIDAdWidget.AdStopReason.IS_AD_FINISHING
                or if (isFinishedByApi) MRAIDAdWidget.AdStopReason.IS_AD_FINISHED_BY_API else 0)
        presenter?.stop()
        presenter?.detach(flag)
        impressionTracker.destroy()
        try {
            removeAllViews()
        } catch (e: Exception) {
            Log.d(TAG, "Removing webView error: $e")
        }
    }

    private fun renderAd() {
        if (visibility != VISIBLE) {
            return
        }

        if (adWidget != null && adWidget?.parent != this) {
            addView(adWidget, calculatedPixelWidth, calculatedPixelHeight)
            if (imageView != null) {
                addView(imageView, calculatedPixelWidth, calculatedPixelHeight)
                imageView?.bringToFront()
            }
        }

        val bannerLayoutParams = layoutParams
        if (bannerLayoutParams != null) {
            //Set Ad Size when Banner Ad is attached to Window
            bannerLayoutParams.height = calculatedPixelHeight
            bannerLayoutParams.width = calculatedPixelWidth
            requestLayout()
        }
    }

    companion object {
        private const val TAG = "BannerView"
    }

    init {
        //set Ad Size
        calculatedPixelHeight = dpToPixels(context, adSize.height)
        calculatedPixelWidth = dpToPixels(context, adSize.width)
        val adEventListener = object : AdEventListener(
            adPlayCallback,
            placement
        ) {}
//        setBackgroundColor(Color.TRANSPARENT)

        val adWidget: MRAIDAdWidget?
        try {
            adWidget = MRAIDAdWidget(context)
        } catch (ex: InstantiationException) {
            adEventListener.onError(
                AdCantPlayWithoutWebView().apply {
                    setPlacementId(placement.referenceId)
                    setEventId(advertisement.eventId())
                    setCreativeId(advertisement.getCreativeId())
                }.logError(),
                placement.referenceId
            )
            throw ex
        }
        this.adWidget = adWidget

        adWidget.setCloseDelegate(object : MRAIDAdWidget.CloseDelegate {
            override fun close() {
                finishAdInternal(false)
            }
        })

        val executors: Executors by inject(context)
        val omTrackerFactory: OMTracker.Factory by inject(context)
        val omTracker =
            omTrackerFactory.make(ConfigManager.omEnabled() && advertisement.omEnabled())
        val webClient = VungleWebClient(advertisement, placement, executors.offloadExecutor)

        webClient.setWebViewObserver(omTracker)
        presenter =
            MRAIDPresenter(
                adWidget,
                advertisement,
                placement,
                webClient,
                executors.jobExecutor,
                omTracker,
                bidPayload
            ).apply {
                setEventListener(adEventListener)
            }

        adConfig.getWatermark()?.let {
            imageView = WatermarkView(context, it)
        }

    }

}
