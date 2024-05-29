package com.vungle.ads.internal.presenter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import com.vungle.ads.AnalyticsClient
import com.vungle.ads.PrivacyUrlError
import com.vungle.ads.ServiceLocator.Companion.inject
import com.vungle.ads.VungleError
import com.vungle.ads.internal.ConfigManager
import com.vungle.ads.internal.Constants.DEEPLINK_CLICK
import com.vungle.ads.internal.executor.Executors
import com.vungle.ads.internal.model.AdPayload
import com.vungle.ads.internal.network.TpatSender
import com.vungle.ads.internal.network.VungleApiClient
import com.vungle.ads.internal.omsdk.NativeOMTracker
import com.vungle.ads.internal.privacy.PrivacyConsent
import com.vungle.ads.internal.privacy.PrivacyManager
import com.vungle.ads.internal.ui.PresenterAdOpenCallback
import com.vungle.ads.internal.ui.PresenterAppLeftCallback
import com.vungle.ads.internal.util.ExternalRouter
import com.vungle.ads.internal.util.FileUtility
import com.vungle.ads.internal.util.PathProvider
import java.util.concurrent.Executor

class NativeAdPresenter(
    private val context: Context,
    private val delegate: NativePresenterDelegate,
    private val advertisement: AdPayload?,
    private var executor: Executor
) {

    private var bus: AdEventListener? = null

    private val vungleApiClient: VungleApiClient by inject(context)
    private val executors: Executors by inject(context)
    private val pathProvider: PathProvider by inject(context)

    private var currentDialog: Dialog? = null

    /**
     * track if adviewed callback has been invoked. cannot send more than once per ad
     */
    private var adViewed = false

    private var omTracker: NativeOMTracker? = null

    companion object {
        private val TAG = NativeAdPresenter::class.simpleName

        const val VIDEO_VIEWED = "videoViewed"
        const val TPAT = "tpat"
        const val OPEN_PRIVACY = "openPrivacy"
        const val DOWNLOAD = "download"
    }

    fun setEventListener(listener: AdEventListener?) {
        bus = listener
    }

    fun processCommand(action: String, value: String? = null) {
        when (action) {
            VIDEO_VIEWED -> {
                if (bus == null || adViewed) {
                    return
                }
                adViewed = true
                bus?.onNext("adViewed", null, delegate.getPlacementRefId())
                val tpatSender = TpatSender(
                    vungleApiClient,
                    delegate.getPlacementRefId(),
                    advertisement?.getCreativeId(),
                    advertisement?.eventId(),
                    executors.ioExecutor,
                    pathProvider,
                )
                delegate.getImpressionUrls()?.forEach { url ->
                    tpatSender.sendTpat(url, executor)
                }
                return
            }

            TPAT -> {
                if (value.isNullOrEmpty()) {
                    AnalyticsClient.logError(
                        VungleError.EMPTY_TPAT_ERROR,
                        "Empty tpat key",
                        delegate.getPlacementRefId(),
                        advertisement?.getCreativeId()
                    )
                    return
                }
                val urls: List<String>? = advertisement?.getTpatUrls(value)
                if (urls.isNullOrEmpty()) {
                    AnalyticsClient.logError(
                        VungleError.INVALID_TPAT_KEY,
                        "Invalid tpat key: $value",
                        delegate.getPlacementRefId(),
                        advertisement?.getCreativeId()
                    )
                } else {
                    val tpatSender = TpatSender(
                        vungleApiClient,
                        delegate.getPlacementRefId(),
                        advertisement?.getCreativeId(),
                        advertisement?.eventId(),
                        executors.ioExecutor,
                        pathProvider,
                    )
                    urls.forEach { url ->
                        tpatSender.sendTpat(url, executor)
                    }
                }
                return
            }

            OPEN_PRIVACY -> {
                onPrivacy(value)
                return
            }

            DOWNLOAD -> {
                onDownload(value)
                return
            }

            else -> {
                Log.w(TAG, "Unknown native ad action: $action")
            }
        }
    }

    private fun onDownload(ctaUrl: String?) {
        // ping tpat
        val urls = advertisement?.getTpatUrls("clickUrl")
        val tpatSender = TpatSender(
            vungleApiClient,
            delegate.getPlacementRefId(),
            advertisement?.getCreativeId(),
            advertisement?.eventId(),
            executors.ioExecutor,
            pathProvider,
        )
        if (urls.isNullOrEmpty()) {
            AnalyticsClient.logError(
                VungleError.EMPTY_TPAT_ERROR,
                "Empty tpat key: clickUrl",
                delegate.getPlacementRefId(),
                advertisement?.getCreativeId()
            )
        } else {
            urls.forEach { url ->
                tpatSender.sendTpat(url, executor)
            }
        }

        // ping ctaUrl
        ctaUrl?.let {
            tpatSender.sendTpat(it, executor)
        }

        val deeplinkUrl = advertisement?.adUnit()?.deeplinkUrl
        ExternalRouter.launch(
            deeplinkUrl,
            ctaUrl,
            context,
            true,
            PresenterAppLeftCallback(bus, null),
            object : PresenterAdOpenCallback {
                override fun onDeeplinkClick(opened: Boolean) {
                    val deeplinkClickTpatUrls = advertisement?.getTpatUrls(
                        DEEPLINK_CLICK,
                        opened.toString()
                    )
                    deeplinkClickTpatUrls?.forEach { url ->
                        tpatSender.sendTpat(url, executor)
                    }
                }
            })

        bus?.onNext("open", "adClick", delegate.getPlacementRefId())
    }

    private fun onPrivacy(privacyUrl: String?) {
        privacyUrl?.let {
            if (!FileUtility.isValidUrl(privacyUrl)) {
                PrivacyUrlError(it)
                    .setPlacementId(delegate.getPlacementRefId())
                    .setCreativeId(advertisement?.getCreativeId())
                    .setEventId(advertisement?.eventId())
                    .logErrorNoReturnValue()
                return
            }
            val launched = ExternalRouter.launch(
                null, it, context, true,
                PresenterAppLeftCallback(bus, delegate.getPlacementRefId()), null
            )
            if(!launched) {
                PrivacyUrlError(it).logErrorNoReturnValue()
            }
        }
    }

    fun prepare() {
        start()
        bus?.onNext("start", null, delegate.getPlacementRefId())
    }

    private fun start() {
        if (needShowGdpr()) {
            showGdpr()
        }
    }

    fun detach() {
        omTracker?.stop()
        currentDialog?.run {
            if (isShowing) {
                dismiss()
            }
        }

        bus?.onNext("end", null, delegate.getPlacementRefId())
    }

    private fun needShowGdpr(): Boolean {
        return ConfigManager.getGDPRIsCountryDataProtected() && "unknown" == PrivacyManager.getConsentStatus()
    }

    private fun showGdpr() {
        PrivacyManager.updateGdprConsent("opted_out_by_timeout", "vungle_modal", null)

        val listener =
            DialogInterface.OnClickListener { _, which ->
                var consented = "opted_out_by_timeout"
                if (which == AlertDialog.BUTTON_NEGATIVE) {
                    consented = PrivacyConsent.OPT_OUT.getValue()
                } else if (which == AlertDialog.BUTTON_POSITIVE) {
                    consented = PrivacyConsent.OPT_IN.getValue()
                }

                PrivacyManager.updateGdprConsent(consented, "vungle_modal", null)

                start()
            }

        val dialogTitle = ConfigManager.getGDPRConsentTitle()
        val dialogBody = ConfigManager.getGDPRConsentMessage()
        val dialogContinue = ConfigManager.getGDPRButtonAccept()
        val dialogClose = ConfigManager.getGDPRButtonDeny()

        val dialogBuilder =
            AlertDialog.Builder(ContextThemeWrapper(context, context.applicationInfo.theme))
        if (!dialogTitle.isNullOrEmpty()) {
            dialogBuilder.setTitle(dialogTitle)
        }
        if (!dialogBody.isNullOrEmpty()) {
            dialogBuilder.setMessage(dialogBody)
        }
        dialogBuilder.setPositiveButton(dialogContinue, listener)
        dialogBuilder.setNegativeButton(dialogClose, listener)
        dialogBuilder.setCancelable(false)
        val dialog = dialogBuilder.create()
        dialog.setOnDismissListener {
            currentDialog = null
        }

        currentDialog = dialog

        dialog.show()
    }

    fun initOMTracker(omSdkData: String) {
        val adOmEnabled = advertisement?.omEnabled() ?: false
        if (omSdkData.isNotEmpty() && ConfigManager.omEnabled() && adOmEnabled) {
            omTracker = NativeOMTracker(omSdkData)
        }
    }

    fun startTracking(rootView: View) {
        omTracker?.start(rootView)
    }

    fun onImpression() {
        omTracker?.impressionOccurred()
    }

}
