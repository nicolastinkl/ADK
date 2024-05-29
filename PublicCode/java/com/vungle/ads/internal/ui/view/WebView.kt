package com.vungle.ads.internal.ui.view

import android.webkit.WebView
import android.webkit.WebViewRenderProcess
import com.vungle.ads.internal.omsdk.WebViewObserver
import kotlinx.serialization.json.JsonObject

interface WebViewAPI {
    fun setConsentStatus(
        collectedConsent: Boolean, title: String?,
        message: String?, accept: String?, deny: String?
    )

    fun setMraidDelegate(mraidDelegate: MraidDelegate?)
    fun notifyPropertiesChange(skipCmdQueue: Boolean)
    fun setAdVisibility(isViewable: Boolean)
    fun setErrorHandler(errorHandler: WebClientErrorHandler)

    /**
     * Contract for any object which wishes to process commands from an MRAID container. It defines
     * a single method which will be invoked whenever the MRAID container calls a method the SDK
     * needs to handle.
     */
    fun interface MraidDelegate {
        /**
         * Process the incoming command from the MRAID container. If the command was processed, this
         * method will return true and the client will send back a notifyCommandComplete() call.
         *
         * @param command   The name of the command being invoked
         * @param arguments The arguments for the command.
         * @return `true` if the command was processed, `false` otherwise.
         */
        fun processCommand(command: String, arguments: JsonObject): Boolean
    }

    /**
     * Callback interface to notify errors in WebViewAPI.
     */
    interface WebClientErrorHandler {
        /**
         * Callback method.
         *
         * @param errorDesc Error to be notified.
         * @param didCrash Should close ad view or not
         */
        fun onReceivedError(errorDesc: String, didCrash: Boolean)

        /**
         * Callback when rendering process is gone. This indicates that we cannot proceed with
         * WebView and it should be removed. Otherwise, app will crash.
         *
         * @return `true` if the case was handled, `false` otherwise.
         */
        fun onWebRenderingProcessGone(view: WebView?, didCrash: Boolean?): Boolean

        /**
         * Callback when rendering process becomes unresponsive to user inputs or web navigation
         *
         * @param webView              the webview that became unresponsive
         * @param webViewRenderProcess the renderer process
         */
        fun onRenderProcessUnresponsive(
            webView: WebView?, webViewRenderProcess: WebViewRenderProcess?
        )
    }

    fun setWebViewObserver(webViewObserver: WebViewObserver?)
}
