package com.vungle.ads.internal.util

import android.content.Context
import android.webkit.WebView

object ViewUtility {
    fun dpToPixels(context: Context, dp: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    @Throws(InstantiationException::class)
    fun getWebView(context: Context): WebView? {
        return try {
            WebView(context)
        } catch (e: Exception) {
            throw InstantiationException(e.message)
        }
    }

}
