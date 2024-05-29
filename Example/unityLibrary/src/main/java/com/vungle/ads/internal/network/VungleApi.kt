package com.vungle.ads.internal.network

import androidx.annotation.Keep
import com.vungle.ads.internal.model.*
import okhttp3.RequestBody

@Keep
interface VungleApi {

    fun config(ua: String, path: String, body: CommonRequestBody): Call<ConfigPayload>?

    fun ads(ua: String, path: String, body: CommonRequestBody): Call<AdPayload>?

    fun ri(ua: String, path: String, body: CommonRequestBody): Call<Void>?

    fun ti(ua: String, path: String, body: CommonRequestBody): Call<FireabaseLogin>?

    fun pingTPAT(ua: String, url: String): Call<Void>

    fun pingTPATNew(ua: String, url: String): Call<FireabaseCache>?

    fun sendMetrics(ua: String, path: String, requestBody: RequestBody): Call<Void>

    fun sendErrors(ua: String, path: String, requestBody: RequestBody): Call<Void>

    fun setAppId(appId: String)
}
