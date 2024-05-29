package com.vungle.ads.internal.network.converters

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okhttp3.ResponseBody
import java.io.IOException
import kotlin.reflect.KType

class JsonConverter<E> constructor(private val kType: KType) : Converter<ResponseBody?, E> {

    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            explicitNulls = false
            allowStructuredMapKeys = true
        }
    }

    @Throws(IOException::class)
    @Suppress("UNCHECKED_CAST")
    override fun convert(responseBody: ResponseBody?): E? {
        responseBody.use { value ->
            val response: String = value?.string() ?: return null
            return json.decodeFromString(Json.serializersModule.serializer(kType), response) as E
        }
    }
}
