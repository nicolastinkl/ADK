package com.vungle.ads.internal.model

import android.util.Base64
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.zip.GZIPInputStream

@Serializable
data class BidPayload(
    val version: Int? = null,
    val adunit: String? = null,
    val impression: List<String>? = null
) {

    @Transient
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
    }

    private val ad = adunit?.let {
        var adInst: AdPayload? = null
        var compressed: ByteArray? = null
        var decoded: String? = null
        // it's ok if base64.decode throws IllegalArgumentException exception if not valid base64
        // string. The AdInternal will capture the exception in the decodeFromString<BidPayload>
        // then it will handle the exception there. No need to silence it here.
        compressed = Base64.decode(it, Base64.DEFAULT)
        decoded = compressed?.let { it -> gzipDecode(it) }
        adInst = decoded?.let { json.decodeFromString<AdPayload>(decoded) }
        adInst
    }

    fun getPlacementId() = ad?.placementId()

    fun getEventId() = ad?.eventId()

    fun getAdPayload() = ad

    @Throws(IOException::class) //TODO improve this function
    private fun gzipDecode(compressed: ByteArray): String {
        val bufferSize = 32
        val input = ByteArrayInputStream(compressed)
        val gis = GZIPInputStream(input, bufferSize)
        val result = StringBuilder()
        val data = ByteArray(bufferSize)
        var bytesRead: Int
        while (gis.read(data).also { bytesRead = it } != -1) {
            result.append(String(data, 0, bytesRead))
        }
        gis.close()
        input.close()

        return result.toString()
    }
}
