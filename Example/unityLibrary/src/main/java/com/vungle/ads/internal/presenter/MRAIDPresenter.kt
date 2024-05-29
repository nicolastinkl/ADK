package com.vungle.ads.internal.presenter

import android.content.pm.ActivityInfo
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewRenderProcess
import androidx.annotation.VisibleForTesting
import com.appsflyer.AppsFlyerLib
import com.vungle.ads.*
import com.vungle.ads.ServiceLocator.Companion.inject
import com.vungle.ads.SingleValueMetric
import com.vungle.ads.InternalError
import com.vungle.ads.PrivacyUrlError
import com.vungle.ads.VungleError
import com.vungle.ads.internal.ClickCoordinateTracker
import com.vungle.ads.internal.ConfigManager
import com.vungle.ads.internal.Constants.DEEPLINK_CLICK
import com.vungle.ads.internal.executor.Executors
import com.vungle.ads.internal.model.AdPayload
import com.vungle.ads.internal.model.BidPayload
import com.vungle.ads.internal.model.CommonRequestBody
import com.vungle.ads.internal.model.Placement
import com.vungle.ads.internal.network.*
import com.vungle.ads.internal.omsdk.OMTracker
import com.vungle.ads.internal.privacy.PrivacyConsent
import com.vungle.ads.internal.privacy.PrivacyManager
import com.vungle.ads.internal.protos.Sdk
import com.vungle.ads.internal.ui.PresenterAdOpenCallback
import com.vungle.ads.internal.ui.PresenterAppLeftCallback
import com.vungle.ads.internal.ui.VungleWebClient
import com.vungle.ads.internal.ui.view.MRAIDAdWidget
import com.vungle.ads.internal.ui.view.WebViewAPI
import com.vungle.ads.internal.util.ExternalRouter
import com.vungle.ads.internal.util.FileUtility
import com.vungle.ads.internal.util.HandlerScheduler
import com.vungle.ads.internal.util.JsonUtil
import com.vungle.ads.internal.util.PathProvider
import com.vungle.ads.internal.util.SuspendableTimer
import com.vungle.ads.internal.util.ThreadUtil
import kotlinx.serialization.json.JsonObject
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean

class MRAIDPresenter(
    private val adWidget: MRAIDAdWidget,
    private val advertisement: AdPayload,
    private val placement: Placement,
    private val vungleWebClient: VungleWebClient,
    private var executor: Executor,
    private val omTracker: OMTracker,
    private val bidPayload: BidPayload?
) :
    WebViewAPI.MraidDelegate,
    WebViewAPI.WebClientErrorHandler {

    @VisibleForTesting var bus: AdEventListener? = null
    private var adViewed = false
    @VisibleForTesting internal val isDestroying = AtomicBoolean(false)
    private val sendReportIncentivized = AtomicBoolean(false)

    @VisibleForTesting internal var adStartTime: Long? = null
    @VisibleForTesting internal var userId: String? = null
    @VisibleForTesting internal val vungleApiClient: VungleApiClient by inject(adWidget.context)
    private val executors: Executors by inject(adWidget.context)
    private val pathProvider: PathProvider by inject(adWidget.context)

    private var presenterDelegate: PresenterDelegate? = null

    private val scheduler by lazy { HandlerScheduler() }

    @VisibleForTesting internal var heartbeatEnabled = false

    @VisibleForTesting internal val suspendableTimer by lazy {
        SuspendableTimer(HEARTBEAT_INTERVAL, true, onFinish = {
            AnalyticsClient.logError(
                VungleError.AD_CLOSED_MISSING_HEARTBEAT,
                "Error ad template missing Heartbeat",
                placement.referenceId,
                advertisement.getCreativeId(),
                advertisement.eventId()
            )
            reportErrorAndCloseAd(InternalError(VungleError.HEARTBEAT_ERROR))
        })
    }

    /**
     * If true, the back button will close the advertisement.
     */
    @VisibleForTesting internal var backEnabled = false

    @VisibleForTesting internal val clickCoordinateTracker: ClickCoordinateTracker by lazy {
        ClickCoordinateTracker(adWidget.context, advertisement, executor)
    }

    companion object {
        private const val TAG = "MRAIDPresenter"

        @VisibleForTesting internal const val CLOSE = "close"
        @VisibleForTesting internal const val CONSENT_ACTION = "consentAction"
        @VisibleForTesting internal const val ACTION_WITH_VALUE = "actionWithValue"
        @VisibleForTesting internal const val VIDEO_VIEWED = "videoViewed"
        @VisibleForTesting internal const val VIDEO_LENGTH = "videoLength"
        @VisibleForTesting internal const val TPAT = "tpat"
        private const val ACTION = "action"
        @VisibleForTesting internal const val OPEN = "open"
        private const val OPEN_NON_MRAID = "openNonMraid"
        private const val USE_CUSTOM_CLOSE = "useCustomClose"
        private const val USE_CUSTOM_PRIVACY = "useCustomPrivacy"
        @VisibleForTesting internal const val OPEN_PRIVACY = "openPrivacy"
        @VisibleForTesting internal const val SUCCESSFUL_VIEW = "successfulView"
        @VisibleForTesting internal const val SET_ORIENTATION_PROPERTIES = "setOrientationProperties"
        @VisibleForTesting internal const val CREATIVE_HEARTBEAT = "creativeHeartbeat"
        @VisibleForTesting internal const val ERROR = "error"

        private const val HEARTBEAT_INTERVAL = 6.0
    }

    fun setEventListener(listener: AdEventListener?) {
        bus = listener
    }

    internal fun setPresenterDelegate(presenterDelegate: PresenterDelegate?) {
        this.presenterDelegate = presenterDelegate
    }

    fun onViewConfigurationChanged() {
        vungleWebClient.notifyPropertiesChange(true)
    }

    fun start() {
        Log.d(TAG, "start()")
        adWidget.resumeWeb()
        setAdVisibility(true)
        if(ConfigManager.adLoadOptimizationEnabled()) {
            recordPlayAssetMetric()
        }
    }

    fun stop() {
        Log.d(TAG, "stop()")
        adWidget.pauseWeb()
        setAdVisibility(false)
    }

    fun detach(@MRAIDAdWidget.AdStopReason stopReason: Int) {
        Log.d(TAG, "detach()")
        val isChangingConfigurations =
            stopReason and MRAIDAdWidget.AdStopReason.IS_CHANGING_CONFIGURATION != 0
        val isFinishing = stopReason and MRAIDAdWidget.AdStopReason.IS_AD_FINISHING != 0

        vungleWebClient.setWebViewObserver(null)
        vungleWebClient.setMraidDelegate(null)

        if (!isChangingConfigurations && isFinishing && !isDestroying.getAndSet(true)) {
            bus?.onNext("end", null, placement.referenceId)
        }

        val delay = omTracker.stop()
        adWidget.destroyWebView(delay)
        if (heartbeatEnabled) {
            suspendableTimer.cancel()
        }
    }

    fun setAdVisibility(isViewable: Boolean) {
        vungleWebClient.setAdVisibility(isViewable)
    }

    fun onViewTouched(event: MotionEvent?) {
        event?.let {
            clickCoordinateTracker.trackCoordinate(it)
        }
    }

    private fun closeView() {
        adWidget.close()
    }

    fun handleExit() {
        if (backEnabled) {
            // Pass the action through the bridge. The template will then decide what to do.
            adWidget.showWebsite("javascript:window.vungle.mraidBridgeExt.requestMRAIDClose()")
        }
    }

    override fun processCommand(command: String, arguments: JsonObject): Boolean {
        val uiHandler = Handler(Looper.getMainLooper())
        when (command) {
            CLOSE -> {
                closeView()
                return true
            }
            CONSENT_ACTION -> {
                val action: String? = JsonUtil.getContentStringValue(arguments, "event")
                val consentStatus =
                    if (action == PrivacyConsent.OPT_OUT.getValue()) PrivacyConsent.OPT_OUT.getValue() else PrivacyConsent.OPT_IN.getValue()
                PrivacyManager.updateGdprConsent(consentStatus, "vungle_modal", null)
                AppsFlyerLib.getInstance().logEvent(adWidget.context,""+action,null);
                return true
            }
            ACTION_WITH_VALUE -> {
                val action: String? = JsonUtil.getContentStringValue(arguments, "event")
                val value: String? = JsonUtil.getContentStringValue(arguments, "value")

                if (action == VIDEO_VIEWED) {
                    var position = 0f
                    try {
                        position = value?.toFloat() ?: 0f
                    } catch (nfe: NumberFormatException) {
                        Log.e(TAG, "value for videoViewed is null !")
                    }

                    if (bus != null && position > 0 && !adViewed) {
                        adViewed = true
                        bus?.onNext("adViewed", null, placement.referenceId)

                        bidPayload?.let {
                            val tpatSender = TpatSender(
                                vungleApiClient,
                                placement.referenceId,
                                advertisement.getCreativeId(),
                                advertisement.eventId(),
                                executors.ioExecutor,
                                pathProvider,
                            )
                            it.impression?.forEach { url ->
                                tpatSender.sendTpat(url, executor)
                            }
                        }
                    }
                }

                if (action == VIDEO_LENGTH) {
                    uiHandler.post {
                        vungleWebClient.notifyPropertiesChange(true)
                    }
                }

                uiHandler.post {
                    adWidget.visibility = View.VISIBLE
                }
                return true
            }
            TPAT -> {
                val event: String? = JsonUtil.getContentStringValue(arguments, "event")
                if (event.isNullOrEmpty()) {
                    AnalyticsClient.logError(
                        VungleError.EMPTY_TPAT_ERROR,
                        "Empty tpat key",
                        placement.referenceId,
                        advertisement.getCreativeId(),
                        advertisement.eventId()
                    )
                    return true
                }
                val tpatSender = TpatSender(
                    vungleApiClient,
                    placement.referenceId,
                    advertisement.getCreativeId(),
                    advertisement.eventId(),
                    executors.ioExecutor,
                    pathProvider,
                )
                advertisement.getTpatUrls(event)?.forEach { url ->
                    tpatSender.sendTpat(url, executor)
                }
                return true
            }
            ACTION -> {
                return true
            }
            OPEN_NON_MRAID -> {

                return true
            }
            OPEN -> {
                val deeplinkUrl: String? = advertisement.adUnit()?.deeplinkUrl
                val url: String? = JsonUtil.getContentStringValue(arguments, "url")
                if (deeplinkUrl.isNullOrEmpty() && (url.isNullOrEmpty())) {
                    Log.e(TAG, "CTA destination URL is not configured properly")
                } else {
                    ExternalRouter.launch(deeplinkUrl, url, adWidget.context, true,
                        PresenterAppLeftCallback(bus, placement.referenceId),
                        object : PresenterAdOpenCallback {
                            override fun onDeeplinkClick(opened: Boolean) {
                                val deeplinkClickTpatUrls = advertisement.getTpatUrls(
                                    DEEPLINK_CLICK,
                                    opened.toString()
                                )
                                val tpatSender = TpatSender(
                                    vungleApiClient,
                                    placement.referenceId,
                                    advertisement.getCreativeId(),
                                    advertisement.eventId(),
                                    executors.ioExecutor,
                                    pathProvider,
                                )
                                deeplinkClickTpatUrls?.forEach { url ->
                                    tpatSender.sendTpat(url, executor)
                                }
                            }
                        })
                }

                bus?.onNext("open", "adClick", placement.referenceId)
                return true
            }
            USE_CUSTOM_CLOSE -> {
                return true
            }
            USE_CUSTOM_PRIVACY -> {
                return true
            }
            OPEN_PRIVACY -> {
                val url: String? =  JsonUtil.getContentStringValue(arguments, "url")
                //val decodedUrl =   JsonUtil.getContentStringValue(arguments, "url")
                //val url: String? =URLDecoder.decode(decodedUrl, StandardCharsets.UTF_8.toString())
//                Log.e("url: ", "   >>  $url")
                if (url.isNullOrEmpty() || !FileUtility.isValidUrl(url)) {
                    PrivacyUrlError(url ?: "nonePrivacyUrl")
                        .setPlacementId(placement.referenceId)
                        .setCreativeId(advertisement.getCreativeId())
                        .setEventId(advertisement.eventId())
                        .logErrorNoReturnValue()
                    return true
                }
                val launched = ExternalRouter.launch(
                    null,
                    url,
                    adWidget.context,
                    true,
                    PresenterAppLeftCallback(bus, placement.referenceId),
                    null
                )
                if (!launched) {
                    PrivacyUrlError(url).logErrorNoReturnValue()
                }
                return true
            }
            SUCCESSFUL_VIEW -> {
                bus?.onNext("successfulView", null, placement.referenceId)
                if (placement.isIncentivized && ConfigManager.isReportIncentivizedEnabled()
                    && !sendReportIncentivized.getAndSet(true)
                ) {
                    executor.execute {
                        val requestParam = CommonRequestBody.RequestParam(
                            placementReferenceId = placement.referenceId,
                            appId = advertisement.appId(),
                            adStartTime = adStartTime,
                            user = userId
                        )
                        val riCall = vungleApiClient.ri(requestParam)
                        if (riCall == null) {
                            Log.e(TAG, "Invalid ri call.")
                            return@execute
                        }
                        riCall.enqueue(object : Callback<Void> {
                            override fun onResponse(
                                call: Call<Void>?,
                                response: Response<Void>?
                            ) {
                                Log.d(TAG, "send RI success")
                            }

                            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                                Log.d(TAG, "send RI Failure")
                            }
                        })
                    }
                }
                return true
            }
            ERROR -> {
                /*
                * mraid://error?code=60007&errorMessage=Programmatic%20fullscreen%20failed%20to%20decode&fatal=true
                * */
                val errorCode: String? = JsonUtil.getContentStringValue(arguments, "code")
                val fatal: String? = JsonUtil.getContentStringValue(arguments, "fatal")
                val isFatal = fatal.toBoolean()
                val errorMsg: String? = JsonUtil.getContentStringValue(arguments, "errorMessage")
                val reason =
                    if (isFatal) VungleError.AD_CLOSED_TEMPLATE_ERROR
                    else VungleError.MRAID_ERROR
                AnalyticsClient.logError(
                    reason,
                    "$errorCode : $errorMsg",
                    placement.referenceId,
                    advertisement.getCreativeId(),
                    advertisement.eventId()
                )
                ThreadUtil.runOnUiThread {
                    val exception = InternalError(VungleError.CREATIVE_ERROR)
                    handleWebViewException(exception, isFatal, "$errorCode : $errorMsg")
                }
                return true
            }
            SET_ORIENTATION_PROPERTIES -> {
                val forceOrientation: String? =
                    JsonUtil.getContentStringValue(arguments, "forceOrientation")
                if (!forceOrientation.isNullOrEmpty()) {
                    when (forceOrientation.lowercase(Locale.ENGLISH)) {
                        "landscape" -> {
                            adWidget.setOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
                        }
                        "portrait" -> {
                            adWidget.setOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
                        }
                    }
                }
                return true
            }
            CREATIVE_HEARTBEAT -> {
                if (heartbeatEnabled) {
                    uiHandler.post { suspendableTimer.reset() }
                }
                return true
            }
            else -> {
                /// Unknown command, but not a fatal error
                AnalyticsClient.logError(
                    VungleError.MRAID_JS_CALL_EMPTY,
                    "Unknown MRAID Command: $command",
                    placement.referenceId,
                    advertisement.getCreativeId(),
                    advertisement.eventId()
                )
                Log.w(TAG, "processCommand# Unknown MRAID Command: $command")
                return true
            }
        }
    }

    private fun makeBusError(reason: VungleError) {
        bus?.onError(reason, placement.referenceId)
    }

    private fun reportErrorAndCloseAd(reason: VungleError) {
        makeBusError(reason)
        closeView()
    }

    override fun onReceivedError(errorDesc: String, didCrash: Boolean) {
        if (didCrash) {
            reportErrorAndCloseAd(InternalError(VungleError.AD_RENDER_NETWORK_ERROR))
        }
    }

    override fun onWebRenderingProcessGone(view: WebView?, didCrash: Boolean?): Boolean {
        handleWebViewException(InternalError(VungleError.WEB_CRASH), true)
        return true
    }

    override fun onRenderProcessUnresponsive(
        webView: WebView?,
        webViewRenderProcess: WebViewRenderProcess?
    ) {
        val exception = InternalError(VungleError.WEBVIEW_RENDER_UNRESPONSIVE)
        handleWebViewException(exception, true)
    }

    private fun handleWebViewException(
        reason: VungleError,
        fatal: Boolean,
        errorMessage: String? = null
    ) {
        Log.e(TAG, "handleWebViewException: ${reason.localizedMessage}, fatal: $fatal, errorMsg: $errorMessage")

        if (fatal) {
            makeBusError(reason)
            closeView()
        }
    }

    private fun loadMraid(template: File): Boolean {
        val dest = template.parent?.let { File(it) }

        /// Now that the mraid.js file has been constructed, load the index.html for the advertisement.
        val indexHtml = File(dest?.path + File.separator + "index.html")
        if (!indexHtml.exists()) {
            AnalyticsClient.logError(
                VungleError.AD_HTML_FAILED_TO_LOAD,
                "Fail to load html ${indexHtml.path}",
                placement.referenceId,
                advertisement.getCreativeId(),
                advertisement.eventId()
            )
            return false
        }
        adWidget.showWebsite("file://" + indexHtml.path)
        // Record if this play uses remote url.
        //recordPlayRemoteUrl()
        return true
    }

    fun prepare() {
        isDestroying.set(false)

        adWidget.linkWebView(vungleWebClient)

        // Process the advertisement settings
        advertisement.adConfig?.getSettings()?.let {
            if (it > 0) {
                backEnabled = (it and AdConfig.IMMEDIATE_BACK) == AdConfig.IMMEDIATE_BACK
            }
        }

        heartbeatEnabled = ConfigManager.heartbeatEnabled()

        // Check if the advertisement has a specified orientation, then lock the window to that
        val requestedOrientation = when (advertisement.adConfig?.adOrientation) {
            AdConfig.PORTRAIT -> {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            }
            AdConfig.LANDSCAPE -> {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
            else -> {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR
            }
        }
        adWidget.setOrientation(requestedOrientation)

        omTracker.start()

        vungleWebClient.setMraidDelegate(this)
        vungleWebClient.setErrorHandler(this)

        val assetDir = advertisement.assetDirectory
        if (assetDir == null || !assetDir.exists()) {
            reportErrorAndCloseAd(AdNotLoadedCantPlay())
            return
        }

        val template = File(assetDir.path + File.separator + AdPayload.KEY_TEMPLATE)
        val ret = loadMraid(template)
        if (!ret) {
            reportErrorAndCloseAd(AdNotLoadedCantPlay())
            return
        }

        adStartTime = System.currentTimeMillis()

        userId = presenterDelegate?.getUserId()

        val titleText = presenterDelegate?.getAlertTitleText() ?: ""
        val bodyText = presenterDelegate?.getAlertBodyText() ?: ""
        val continueText = presenterDelegate?.getAlertContinueButtonText() ?: ""
        val closeText = presenterDelegate?.getAlertCloseButtonText() ?: ""
        advertisement.setIncentivizedText(titleText, bodyText, continueText, closeText)

        val collectedConsent =
            ConfigManager.getGDPRIsCountryDataProtected() && "unknown" == PrivacyManager.getConsentStatus()
        vungleWebClient.setConsentStatus(
            collectedConsent,
            ConfigManager.getGDPRConsentTitle(),
            ConfigManager.getGDPRConsentMessage(),
            ConfigManager.getGDPRButtonAccept(),
            ConfigManager.getGDPRButtonDeny()
        )

        if (collectedConsent) {
            PrivacyManager.updateGdprConsent("opted_out_by_timeout", "vungle_modal", "")
        }

        /// Enable the back button once the delay has elapsed. If no delay, enable immediately
        val delay = advertisement.getShowCloseDelay(placement.isIncentivized)
        if (delay > 0) {
            /// Use a simple timer to enact the delay mechanism
            scheduler.schedule({ backEnabled = true }, delay.toLong())
        } else {
            backEnabled = true
        }

        bus?.onNext("start", null, placement.referenceId)
        if (heartbeatEnabled) {
            suspendableTimer.start()
        }
    }

    private fun recordPlayAssetMetric() {
        val playAssetMetricType = if (advertisement.assetsFullyDownloaded) {
            Sdk.SDKMetric.SDKMetricType.LOCAL_ASSETS_USED
        } else {
            Sdk.SDKMetric.SDKMetricType.REMOTE_ASSETS_USED
        }
        val playAssetMetric = SingleValueMetric(playAssetMetricType)
        AnalyticsClient.logMetric(playAssetMetric,
            placement.referenceId, advertisement.getCreativeId(), advertisement.eventId())
    }

}
