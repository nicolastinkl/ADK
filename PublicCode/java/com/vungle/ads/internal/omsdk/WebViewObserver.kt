package com.vungle.ads.internal.omsdk

import android.webkit.WebView

fun interface WebViewObserver {
    fun onPageFinished(webView: WebView)
}