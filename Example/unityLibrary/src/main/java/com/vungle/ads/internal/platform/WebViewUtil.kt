package com.vungle.ads.internal.platform

import android.content.Context
import android.os.Build
import android.util.AndroidRuntimeException
import android.util.Log
import android.webkit.WebSettings
import androidx.core.util.Consumer
import java.lang.Exception

class WebViewUtil(private val context: Context) {

    companion object {
        private val TAG = WebViewUtil::class.java.simpleName
    }

    fun getUserAgent(consumer: Consumer<String?>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                val userAgent = WebSettings.getDefaultUserAgent(
                    context
                )
                consumer.accept(userAgent)
            } catch (e: Exception) {
                if (e is AndroidRuntimeException) {
                    Log.e(TAG, "WebView could be missing here")
                }
                consumer.accept(null)
            }
            return
        }
        consumer.accept(null)
    }
}
