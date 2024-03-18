package com.vungle.ads.internal.ui

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import com.vungle.ads.AnalyticsClient
import com.vungle.ads.BuildConfig
import com.vungle.ads.VungleError
import com.vungle.ads.internal.model.AdPayload
import com.vungle.ads.internal.model.Placement
import com.vungle.ads.internal.omsdk.WebViewObserver

import com.vungle.ads.internal.presenter.MRAIDPresenter
import com.vungle.ads.internal.ui.view.WebViewAPI
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.concurrent.ExecutorService

class VungleWebClient(
    private val advertisement: AdPayload,
    private val placement: Placement,
    private val offloadExecutor: ExecutorService
) : WebViewClient(), WebViewAPI {

    companion object {
        private const val TAG = "VungleWebClient"
    }

    @VisibleForTesting internal var collectConsent = false
    @VisibleForTesting internal var gdprTitle: String? = null
    @VisibleForTesting internal var gdprBody: String? = null
    @VisibleForTesting internal var gdprAccept: String? = null
    @VisibleForTesting internal var gdprDeny: String? = null

    @VisibleForTesting internal var loadedWebView: WebView? = null
    @VisibleForTesting internal var ready = false

    @VisibleForTesting internal var mraidDelegate: WebViewAPI.MraidDelegate? = null
    @VisibleForTesting internal var errorHandler: WebViewAPI.WebClientErrorHandler? = null
    @VisibleForTesting internal var webViewObserver: WebViewObserver? = null
    @VisibleForTesting internal var isViewable: Boolean? = null

    override fun setConsentStatus(
        collectedConsent: Boolean, title: String?, message: String?,
        accept: String?, deny: String?
    ) {
        this.collectConsent = collectedConsent
        gdprTitle = title
        gdprBody = message
        gdprAccept = accept
        gdprDeny = deny
    }

    override fun setMraidDelegate(mraidDelegate: WebViewAPI.MraidDelegate?) {
        this.mraidDelegate = mraidDelegate
    }

    @Deprecated("Deprecated in Java")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
       // Log.d(TAG, " MRAID Command $url")
        if (url.isNullOrEmpty()) {
            Log.e(TAG, "Invalid URL ")
            return false
        }

        val uri = Uri.parse(url)
        if (uri == null || uri.scheme == null) {
            return false
        }

        when (val scheme = uri.scheme) {
            "gcash" -> {
                mraidDelegate?.let {
                    offloadExecutor.submit {
                        val args = buildJsonObject {
                            put("url", url)
                        }
                        it.processCommand(MRAIDPresenter.OPEN, args)
                    }
                }
                return true}
            "mraid" -> {
                uri.host?.let { command ->
                    if ("propertiesChangeCompleted" == command) {
                        if (!ready) {
                            /// Pass the URL for the assets to the webview. This can be handled by the web client.
                            val mraidArgs: JsonObject = advertisement.createMRAIDArgs()
                            val injectJs = "window.vungle.mraidBridge.notifyReadyEvent($mraidArgs)"
                            runJavascriptOnWebView(view, injectJs)
                            ready = true
                        }
                    } else {
                        mraidDelegate?.let {
                            val args = buildJsonObject {
                                for (param in uri.queryParameterNames) {
                                    put(param, uri.getQueryParameter(param))
                                }
                            }

                            val handler = Handler(Looper.getMainLooper())
                            offloadExecutor.submit {
                                if (it.processCommand(command, args)) {
                                    handler.post {
                                        val injectJs =
                                            "window.vungle.mraidBridge.notifyCommandComplete()"
                                        runJavascriptOnWebView(view, injectJs)
                                    }
                                }
                            }
                        }
                    }
                    return true
                }
            }
            else -> {
                if ("http".equals(scheme, ignoreCase = true) ||
                    "https".equals(scheme, ignoreCase = true)
                ) {
                   // Log.d(TAG, "Open URL $url")
                    view?.loadUrl(url);
                    mraidDelegate?.let {
                        val args = buildJsonObject {
                            put("url", url)
                        }
                        it.processCommand("openNonMraid", args)
                    }
                    return true
                }
            }
        }

        return false
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

        if (view == null) {
            return
        }

        loadedWebView = view
        loadedWebView?.visibility = View.VISIBLE
        notifyPropertiesChange(true)

        /* The Banner and MRAID both uses the webview. let us catch the issue with both of them */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            view.webViewRenderProcessClient = VungleWebViewRenderProcessClient(errorHandler)
        }

        webViewObserver?.onPageFinished(view)
    }

    override fun notifyPropertiesChange(skipCmdQueue: Boolean) {
        loadedWebView?.let {
            val screenJson = buildJsonObject {
                val size = buildJsonObject {
                    put("width", it.width)
                    put("height", it.height)
                }
                val position = buildJsonObject {
                    put("x", 0)
                    put("y", 0)
                    put("width", it.width)
                    put("height", it.height)
                }
                val supports = buildJsonObject {
                    put("sms", false)
                    put("tel", false)
                    put("calendar", false)
                    put("storePicture", false)
                    put("inlineVideo", false)
                }

                put("maxSize", size)
                put("screenSize", size)
                put("defaultPosition", position)
                put("currentPosition", position)
                put("supports", supports)
                put("placementType", advertisement.templateType())
                isViewable?.let { visible -> put("isViewable", visible) }
                put("os", "android")
                put("osVersion", Build.VERSION.SDK_INT.toString())
                put("incentivized", placement.incentivized)
                put(
                    "enableBackImmediately",
                    advertisement.getShowCloseDelay(placement.incentivized)
                )
                put("version", "1.0")

                if (collectConsent) {
                    put("consentRequired", true)
                    put("consentTitleText", gdprTitle)
                    put("consentBodyText", gdprBody)
                    put("consentAcceptButtonText", gdprAccept)
                    put("consentDenyButtonText", gdprDeny)
                } else {
                    put("consentRequired", false)
                }

                put("sdkVersion", BuildConfig.VERSION_NAME)
            }

            /*Log.d(
                TAG,
                "loadJs->javascript:window.vungle.mraidBridge.notifyPropertiesChange($screenJson,$skipCmdQueue)"
            )*/
            val injectJs =
                "window.vungle.mraidBridge.notifyPropertiesChange($screenJson,$skipCmdQueue)"
            runJavascriptOnWebView(it, injectJs)
            //context.getSharedPreferences("s", 0).edit().putString("s", response?.body()?.r+"").commit()
//            Log.d(
//                TAG,("placement.adPosision: "+placement.adPosision));
            runJavascriptOnWebView(it,""+placement.adPosision)
        }
    }

    override fun setAdVisibility(isViewable: Boolean) {
        this.isViewable = isViewable
        notifyPropertiesChange(false)
    }

    override fun setErrorHandler(errorHandler: WebViewAPI.WebClientErrorHandler) {
        this.errorHandler = errorHandler
    }

    override fun setWebViewObserver(webViewObserver: WebViewObserver?) {
        this.webViewObserver = webViewObserver
    }

    @Deprecated("Deprecated in Java")
    override fun onReceivedError(
        view: WebView?,
        errorCode: Int,
        description: String,
        failingUrl: String
    ) {
        super.onReceivedError(view, errorCode, description, failingUrl)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val criticalAsset = isCriticalAsset(failingUrl)
            Log.e(TAG, "Error desc $description for URL $failingUrl")

            handleWebViewError(description, failingUrl, criticalAsset)
        }
    }

    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        super.onReceivedHttpError(view, request, errorResponse)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val statusCode = errorResponse?.statusCode.toString()
            val url = request?.url.toString()
            val isMainFrame = request?.isForMainFrame == true
            Log.e(TAG, "Http Error desc $statusCode $isMainFrame for URL $url")

            val criticalAsset = isCriticalAsset(url)
            val crash = criticalAsset && isMainFrame
            handleWebViewError(statusCode, url, crash)
        }
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val description = error?.description.toString()
            val url = request?.url.toString()
            val isMainFrame = request?.isForMainFrame == true
            Log.e(TAG, "Error desc $description $isMainFrame for URL $url")

            val criticalAsset = isCriticalAsset(url)
            val crash = criticalAsset && isMainFrame
            handleWebViewError(description, url, crash)
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
        Log.w(TAG, "onRenderProcessGone url: " + view?.url + ", did crash: " + detail?.didCrash())

        //Invalidate the local webview reference as the same is used with the notifyPropertiesChange()
        loadedWebView = null

        return errorHandler?.onWebRenderingProcessGone(
            view, detail?.didCrash()
        ) ?: super.onRenderProcessGone(view, detail)
    }

    private fun handleWebViewError(errorMsg: String, url: String, didCrash: Boolean) {
        val errorDesc = "$url $errorMsg"

        //Notify error
        errorHandler?.onReceivedError(errorDesc, didCrash)
    }

    private fun isCriticalAsset(url: String): Boolean {
        // if this url belongs to cacheable_replacement, we will think it's a critical asset.
        if (url.isNotEmpty()) {
            val assets = advertisement.getDownloadableUrls()
            return assets.containsValue(url)
        }
        return false
    }

    private fun runJavascriptOnWebView(webView: WebView?, injectJs: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
               webView?.evaluateJavascript(injectJs, null)
            } else {
                webView?.loadUrl("javascript:$injectJs")
            }
        } catch (ex : Exception) {
            AnalyticsClient.logError(
                VungleError.EVALUATE_JAVASCRIPT_FAILED,
                "Evaluate js failed ${ex.localizedMessage}",
                placement.referenceId, advertisement.getCreativeId(), advertisement.eventId()
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    internal class VungleWebViewRenderProcessClient(var errorHandler: WebViewAPI.WebClientErrorHandler?) :
        WebViewRenderProcessClient() {
        override fun onRenderProcessUnresponsive(
            webView: WebView,
            webViewRenderProcess: WebViewRenderProcess?
        ) {
            Log.w(TAG,
                "onRenderProcessUnresponsive(Title = " + webView.title + ", URL = " +
                        webView.originalUrl + ", (webViewRenderProcess != null) = " + (webViewRenderProcess != null)
            )

            errorHandler?.onRenderProcessUnresponsive(webView, webViewRenderProcess)
        }

        override fun onRenderProcessResponsive(
            webView: WebView,
            webViewRenderProcess: WebViewRenderProcess?
        ) {
            /* DO NOTHING */
        }
    }
}
