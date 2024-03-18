package com.vungle.ads.internal.util

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

internal object JsonUtil {

    fun getContentStringValue(json: JsonObject, key: String): String? {
        return try {
            json.getValue(key).jsonPrimitive.content
        } catch (ex: Exception) {
            null
        }
    }
}
