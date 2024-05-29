package com.vungle.ads.internal.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RelativeLayout
import androidx.annotation.VisibleForTesting
import com.vungle.ads.BuildConfig
import com.vungle.ads.internal.util.HandlerScheduler
import com.vungle.ads.internal.util.ViewUtility

class MRAIDAdWidget @Throws(InstantiationException::class) constructor(context: Context) :
    RelativeLayout(context) {
    private var webView: WebView? = null
    @VisibleForTesting internal var onViewTouchListener: OnViewTouchListener? = null
    @VisibleForTesting internal var closeDelegate: CloseDelegate? = null
    @VisibleForTesting internal var orientationDelegate: OrientationDelegate? = null

    @SuppressLint("ClickableViewAccessibility")
    private fun bindListeners() {
        onViewTouchListener?.let {
            webView?.setOnTouchListener { _, event -> it.onTouch(event) }
        }
    }

    private fun prepare() {
        webView?.let {
            it.setLayerType(LAYER_TYPE_HARDWARE, null)
            it.setBackgroundColor(Color.TRANSPARENT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                it.settings.mediaPlaybackRequiresUserGesture = false
            }
            it.visibility = GONE
        }
    }

    interface CloseDelegate {
        fun close()
    }

    interface OrientationDelegate {
        fun setOrientation(orientation: Int)
    }

    interface OnViewTouchListener {
        fun onTouch(event: MotionEvent?): Boolean
    }

    fun setCloseDelegate(closeDelegate: CloseDelegate) {
        this.closeDelegate = closeDelegate
    }

    fun goback(){

        val webView: WebView? = this.webView
        val str: String? = null
        if (webView != null) {
            if (webView.canGoBack()) {
                val webView2: WebView? = this.webView
                if (webView2 != null) {
                    webView2.goBack()
                    return
                } else {

                }
            }

        }
    }
    fun setOnViewTouchListener(onViewTouchListener: OnViewTouchListener?) {
        this.onViewTouchListener = onViewTouchListener
    }

    fun setOrientationDelegate(orientationDelegate: OrientationDelegate?) {
        this.orientationDelegate = orientationDelegate
    }

    fun close() {
        closeDelegate?.close()
    }

    fun setOrientation(requestedOrientation: Int) {
        orientationDelegate?.setOrientation(requestedOrientation)
    }

    fun linkWebView(vngWebViewClient: WebViewClient) {
        webView?.let {
            applyDefault(it)
            it.webViewClient = vngWebViewClient
        }
    }



    fun showWebsite(url: String) {
        Log.d(TAG, "loadUrl: $url")
        webView?.let {
            it.visibility = VISIBLE
            it.loadUrl(url)

        }
    }



    private fun applyDefault(webView: WebView) {
        val webSettings = webView.settings
        webSettings.builtInZoomControls = false
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.saveFormData = true
        webSettings.useWideViewPort = false
        webSettings.allowFileAccess = true

        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true
        webSettings.setSupportZoom(true)
        webSettings.displayZoomControls = true

//        webSettings.javaScriptCanOpenWindowsAutomatically = true
//        webSettings.loadWithOverviewMode = true
//        webSettings.domStorageEnabled = true
//        webSettings.setSupportMultipleWindows(true)
//        webSettings.displayZoomControls = true
        webSettings.userAgentString = getUserAgent(webView)

        webView.visibility = View.INVISIBLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.mediaPlaybackRequiresUserGesture = false
        }
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }

    private fun getUserAgent(webView: WebView): String? {
        var userAgent: String = try {
            WebSettings.getDefaultUserAgent(webView.context)
        } catch (e: Exception) {
            System.getProperty("http.agent")
        }
        userAgent += "app/p9game"
        return userAgent
    }
    fun pauseWeb() {
        webView?.onPause()
    }

    fun resumeWeb() {
        webView?.onResume()
    }

    val url: String?
        get() = webView?.url

    fun destroyWebView(webViewDestroyDelay: Long) {
        webView?.let {
            it.webChromeClient = null
            removeAllViews()
            if (webViewDestroyDelay <= 0) {
                DestroyRunnable(it).run()
            } else {
                HandlerScheduler().schedule(DestroyRunnable(it), webViewDestroyDelay)
            }
        }
    }

    private class DestroyRunnable(private val webView: WebView) : Runnable {
        override fun run() {
            try {
                webView.stopLoading()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    webView.webViewRenderProcessClient = null
                }
                webView.loadData("", null, null)
                webView.destroy()
            } catch (_: Throwable) {}
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        layoutParams?.let {
            it.height = ViewGroup.LayoutParams.MATCH_PARENT
            it.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
        webView?.layoutParams?.let {
            it.height = ViewGroup.LayoutParams.MATCH_PARENT
            it.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    class AudioContextWrapper(base: Context?) : ContextWrapper(base) {
        override fun getSystemService(name: String): Any {
            return if (AUDIO_SERVICE == name) {
                applicationContext.getSystemService(name)
            } else {
                super.getSystemService(name)
            }
        }
    }

    companion object {
        private const val TAG = "MRAIDAdWidget"
    }

    init {
        val matchParentLayoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        layoutParams = matchParentLayoutParams
        val webView = ViewUtility.getWebView(context)
        this.webView = webView
        webView?.layoutParams = matchParentLayoutParams
        webView?.tag = "VungleWebView"
        addView(webView, matchParentLayoutParams)
        bindListeners()
        prepare()
    }

    annotation class AdStopReason {
        companion object {
            const val IS_CHANGING_CONFIGURATION = 1 //1 << 0
            const val IS_AD_FINISHING = 1 shl 1
            const val IS_AD_FINISHED_BY_API = 1 shl 2
        }
    }

}
