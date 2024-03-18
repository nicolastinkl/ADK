package com.vungle.ads.internal.bidding

import android.content.Context
import android.util.Base64
import com.vungle.ads.AnalyticsClient
import com.vungle.ads.ServiceLocator
import com.vungle.ads.VungleError
import com.vungle.ads.internal.ConfigManager
import com.vungle.ads.internal.model.RtbRequest
import com.vungle.ads.internal.model.RtbToken
import com.vungle.ads.internal.network.VungleApiClient
import com.vungle.ads.internal.util.ActivityManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPOutputStream

class BidTokenEncoder(
    private val context: Context
) {

    private var ordinalView = 0
    private val json = Json { explicitNulls = false }

    private var enterBackgroundTime = 0L

    companion object {
        private const val TOKEN_VERSION = 4
    }

    init {
        ActivityManager.addLifecycleListener(object : ActivityManager.LifeCycleCallback() {
            override fun onResume() {
                super.onResume()
                if (System.currentTimeMillis() > enterBackgroundTime + ConfigManager.getSessionTimeoutInSecond() * 1000L) {
                    // Reset ordinal view count
                    ordinalView = 0
                    enterBackgroundTime = 0L
                }
            }

            override fun onPause() {
                super.onPause()
                enterBackgroundTime = System.currentTimeMillis()
            }
        })
    }

    fun encode(): String? {
        ordinalView++
        return bidTokenV4()
    }

    private fun bidTokenV4():String? {
        return try {
            val token: String = constructV4Token()
            val os = ByteArrayOutputStream(token.length)
            val gos = GZIPOutputStream(os)
            gos.write(token.toByteArray())
            gos.close()
            val compressed = os.toByteArray()
            val base64 = Base64.encodeToString(compressed, Base64.NO_WRAP)
            os.close()
            "$TOKEN_VERSION:$base64"
        } catch (e: IOException) {
            AnalyticsClient.logError(
                VungleError.GZIP_ENCODE_ERROR,
                "Fail to gzip bidtoken ${e.localizedMessage}"
            )
            null
        }
    }

    fun constructV4Token(): String {
        val vungleApiClient: VungleApiClient by ServiceLocator.inject(context)
        val commonRequestBody = vungleApiClient.requestBody()
        val body = RtbToken(
            commonRequestBody.device,
            commonRequestBody.user,
            commonRequestBody.ext,
            RtbRequest(VungleApiClient.headerUa),
            ordinalView)
        return json.encodeToString(body)
    }

}
