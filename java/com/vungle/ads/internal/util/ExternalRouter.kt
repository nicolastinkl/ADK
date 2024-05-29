package com.vungle.ads.internal.util

import android.content.Context
import android.content.Intent
import android.util.Log
import com.vungle.ads.AnalyticsClient
import com.vungle.ads.VungleError
import com.vungle.ads.internal.ui.PresenterAdOpenCallback
import java.lang.Exception
import java.net.URISyntaxException

object ExternalRouter {

    private val TAG = ExternalRouter::class.java.simpleName

    @JvmStatic
    fun launch(
        deeplinkUrl: String?,
        url: String?,
        context: Context,
        contextIsNotActivity: Boolean,
        leftApplicationCallback: ActivityManager.LeftApplicationCallback?,
        adOpenCallback: PresenterAdOpenCallback?
    ): Boolean {
        if (deeplinkUrl.isNullOrEmpty() && url.isNullOrEmpty()) {
            return false
        }
        try {
            val deeplinkIntent = getIntentFromUrl(deeplinkUrl, contextIsNotActivity)
            val fallbackIntent = getIntentFromUrl(url, contextIsNotActivity)
            ActivityManager.startWhenForeground(
                context,
                deeplinkIntent,
                fallbackIntent,
                leftApplicationCallback,
                adOpenCallback
            )
            return true
        } catch (e: Exception) {
            if (!deeplinkUrl.isNullOrEmpty()) {
                AnalyticsClient.logError(
                    VungleError.DEEPLINK_OPEN_FAILED,
                    "Fail to open $deeplinkUrl",
                    ""
                )
            } else {
                AnalyticsClient.logError(
                    VungleError.LINK_COMMAND_OPEN_FAILED,
                    "Fail to open $url",
                    ""
                )
            }
            Log.e(TAG, "Error while opening url" + e.localizedMessage)
        }
        Log.d(TAG, "Cannot open url $url")
        return false
    }

    private fun getIntentFromUrl(url: String?, contextIsNotActivity: Boolean): Intent? {
        var intent: Intent? = null
        try {
            intent = if (url.isNullOrEmpty()) null else Intent.parseUri(url, 0)
            if (intent != null && contextIsNotActivity) {  /* case for NativeAds API which can only use ApplicationContext */
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        } catch (e: URISyntaxException) {
            Log.e(TAG, "url format is not correct " + e.localizedMessage)
        }
        return intent
    }
}
