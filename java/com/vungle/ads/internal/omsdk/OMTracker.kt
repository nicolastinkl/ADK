package com.vungle.ads.internal.omsdk

import android.webkit.WebView
import androidx.annotation.VisibleForTesting
import com.iab.omid.library.vungle.Omid
import com.iab.omid.library.vungle.adsession.*
import com.vungle.ads.BuildConfig
import java.util.concurrent.TimeUnit

class OMTracker private constructor(private val enabled: Boolean) : WebViewObserver {
    class Factory {
        fun make(enabled: Boolean): OMTracker {
            return OMTracker(enabled)
        }
    }

    private var started = false
    private var adSession: AdSession? = null

    /**
     * Starts OM session
     */
    fun start() {
        if (enabled && Omid.isActive()) {
            started = true
        }
    }

    /**
     * Stops OM session
     * @return timeout when [WebView] can be destroyed
     */
    fun stop(): Long {
        var delay = 0L
        if (started && adSession != null) {
            adSession?.finish()
            delay = DESTROY_DELAY_MS
        }
        started = false
        adSession = null
        return delay
    }

    override fun onPageFinished(webView: WebView) {
        if (started && adSession == null) {
            val adSessionConfiguration = AdSessionConfiguration.createAdSessionConfiguration(
                CreativeType.DEFINED_BY_JAVASCRIPT,
                ImpressionType.DEFINED_BY_JAVASCRIPT,
                Owner.JAVASCRIPT,
                Owner.JAVASCRIPT,
                false
            )
            val partner =
                Partner.createPartner(BuildConfig.OMSDK_PARTNER_NAME, BuildConfig.VERSION_NAME)
            val adSessionContext =
                AdSessionContext.createHtmlAdSessionContext(partner, webView, null, null)
            adSession = AdSession.createAdSession(adSessionConfiguration, adSessionContext)
            adSession?.registerAdView(webView)
            adSession?.start()
        }
    }

    companion object {
        @VisibleForTesting
        val DESTROY_DELAY_MS = TimeUnit.SECONDS.toMillis(1)
    }
}