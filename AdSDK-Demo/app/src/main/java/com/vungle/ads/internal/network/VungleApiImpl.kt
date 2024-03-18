package com.vungle.ads.internal.network

import android.util.Log
import androidx.annotation.VisibleForTesting
import com.vungle.ads.AnalyticsClient
import com.vungle.ads.VungleError
import com.vungle.ads.internal.model.AdPayload
import com.vungle.ads.internal.model.BidPayload
import com.vungle.ads.internal.model.CommonRequestBody
import com.vungle.ads.internal.model.ConfigPayload
import com.vungle.ads.internal.model.FireabaseCache
import com.vungle.ads.internal.model.FireabaseLogin
import com.vungle.ads.internal.network.converters.EmptyResponseConverter
import com.vungle.ads.internal.network.converters.JsonConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.reflect.typeOf

class VungleApiImpl(@get:VisibleForTesting internal val okHttpClient: okhttp3.Call.Factory) :
    VungleApi {

    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            explicitNulls = false
            allowStructuredMapKeys = true
        }
        private const val VUNGLE_VERSION = "7.0.0"
    }

    private var appId: String? = null

    private val emptyResponseConverter = EmptyResponseConverter()

    override fun setAppId(appId: String) {
        this.appId = appId
    }

    private fun defaultBuilder(ua: String, path: String): Request.Builder {

        val builder = Request.Builder()
            .url(path)
            .addHeader("User-Agent", ua)
            .addHeader("Vungle-Version", VUNGLE_VERSION)
            .addHeader("Content-Type", "application/json")
        appId?.let {
            builder.addHeader("X-Vungle-App-Id", it)
        }

        return builder
    }

    private fun defaultProtoBufBuilder(ua: String, path: String): Request.Builder {
        val builder = Request.Builder()
            .url(path)
            .addHeader("User-Agent", ua)
            .addHeader("Vungle-Version", VUNGLE_VERSION)
            .addHeader("Content-Type", "application/x-protobuf")
        appId?.let {
            builder.addHeader("X-Vungle-App-Id", it)
        }

        return builder
    }

    override fun config(ua: String, path: String, body: CommonRequestBody): Call<ConfigPayload>? {

        //VungleDroid/7.1.0  https://config.ads.vungle.com/config
        // api.config(headerUa, baseUrl + "config", requestBody)
        // Serialization
        //CommonRequestBody.serializer(),
//    fix with new kotin json jar library
//        CommonRequestBody.serializer()
//        val jsonString = Json.encodeToString(CommonRequestBody.serializer(),body)
//        println(jsonString)
//        CommonRequestBody.serializer()
        return try {
            val requestBody = json.encodeToString(body)

            val request = defaultBuilder(ua, path)
                .post(requestBody.toRequestBody(null))
                .build()

            OkHttpCall(okHttpClient.newCall(request), JsonConverter(typeOf<ConfigPayload>()))
        } catch (e: Exception) {
            print("Exception :"+e.message)
            null
        }
    }

    override fun ads(ua: String, path: String, body: CommonRequestBody): Call<AdPayload>? {
        //Log.d("ads", "$ua  $path")
        // VungleDroid/7.1.0  https://adx-cn.ads.vungle.com/api/v7/ads
        return try {
            val requestBody = json.encodeToString(body)

            val request = defaultBuilder(ua, path)
                .post(requestBody.toRequestBody(null))
                .build()

            OkHttpCall(okHttpClient.newCall(request), JsonConverter(typeOf<AdPayload>()))
        } catch (e: Exception) {
            AnalyticsClient.logError(
                VungleError.API_REQUEST_ERROR,
                "Error with url: ${path}"
            )
            null
        }
    }

    override fun ri(ua: String, path: String, body: CommonRequestBody): Call<Void>? {
        return try {
            val requestBody = json.encodeToString(body)

            val request = defaultBuilder(ua, path)
                .post(requestBody.toRequestBody(null))
                .build()

            OkHttpCall(okHttpClient.newCall(request), emptyResponseConverter)
        } catch (e: Exception) {
            AnalyticsClient.logError(
                VungleError.API_REQUEST_ERROR,
                "Error with url: ${path}"
            )
            null
        }
    }

    override fun ti(ua: String, path: String, body: CommonRequestBody): Call<FireabaseLogin>? {

        return try {
            val requestBody = json.encodeToString(body)


            val request = defaultBuilder(ua, path)
                .post(requestBody.toRequestBody(null))
                .build()

            OkHttpCall(okHttpClient.newCall(request),  JsonConverter(typeOf<FireabaseLogin>()))
        } catch (e: Exception) {
            AnalyticsClient.logError(
                VungleError.API_REQUEST_ERROR,
                "Error with url: ${path}"
            )
            null
        }
    }


    override fun pingTPAT(ua: String, url: String): Call<Void> {
        val urlBuilder: HttpUrl.Builder = url.toHttpUrl().newBuilder()

        val request = defaultBuilder(ua, urlBuilder.build().toString())
            .get()
            .build()
        return OkHttpCall(okHttpClient.newCall(request), emptyResponseConverter)
    }


    override fun pingTPATNew(ua: String, url: String): Call<FireabaseCache>? {
        val urlBuilder: HttpUrl.Builder = url.toHttpUrl().newBuilder()

        val request = defaultBuilder(ua, urlBuilder.build().toString())
            .get()
            .build()
        return OkHttpCall(okHttpClient.newCall(request), JsonConverter(typeOf<FireabaseCache>()))
    }

    override fun sendMetrics(ua:String, path: String, requestBody: RequestBody): Call<Void> {
        val urlBuilder: HttpUrl.Builder = path.toHttpUrl().newBuilder()
        val request = defaultProtoBufBuilder(ua, urlBuilder.build().toString())
            .post(requestBody).build()

        return OkHttpCall(okHttpClient.newCall(request), emptyResponseConverter)
    }

    override fun sendErrors(ua: String, path: String, requestBody: RequestBody): Call<Void> {
        val urlBuilder: HttpUrl.Builder = path.toHttpUrl().newBuilder()
        val request = defaultProtoBufBuilder(ua, urlBuilder.build().toString())
            .post(requestBody).build()

        return OkHttpCall(okHttpClient.newCall(request), emptyResponseConverter)
    }
}
