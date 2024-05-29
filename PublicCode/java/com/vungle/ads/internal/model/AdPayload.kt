package com.vungle.ads.internal.model

import androidx.annotation.VisibleForTesting
import com.vungle.ads.AdConfig
import com.vungle.ads.AnalyticsClient
import com.vungle.ads.BannerAdSize
import com.vungle.ads.VungleError
import com.vungle.ads.internal.Constants.CHECKPOINT_0
import com.vungle.ads.internal.Constants.DEEPLINK_CLICK
import com.vungle.ads.internal.Constants.DEEPLINK_SUCCESS_KEY
import com.vungle.ads.internal.Constants.REMOTE_PLAY_KEY
import com.vungle.ads.internal.util.FileUtility
import com.vungle.ads.internal.util.FileUtility.isValidUrl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.*
import java.io.File
import java.util.regex.Pattern

@Serializable
class AdPayload(private val ads: List<PlacementAdUnit>? = null) {

    companion object {
        const val FILE_SCHEME = "file://"
        const val TYPE_VUNGLE_MRAID = "vungle_mraid"
        const val KEY_TEMPLATE = "template"
        const val KEY_POSTROLL = "postroll"
        const val INCENTIVIZED_TITLE_TEXT = "INCENTIVIZED_TITLE_TEXT"
        const val INCENTIVIZED_BODY_TEXT = "INCENTIVIZED_BODY_TEXT"
        const val INCENTIVIZED_CLOSE_TEXT = "INCENTIVIZED_CLOSE_TEXT"
        const val INCENTIVIZED_CONTINUE_TEXT = "INCENTIVIZED_CONTINUE_TEXT"
        private const val UNKNOWN = "unknown"

        const val TPAT_CLICK_COORDINATES_URLS = "video.clickCoordinates"
    }

    private val ad: PlacementAdUnit?
        get() = ads?.let {
            if (it.isNotEmpty()) {
                it[0]
            } else {
                null
            }
        }
    private val adMarkup: AdUnit? get() = ad?.adMarkup

    fun placementId() = ad?.placementReferenceId

    fun eventId() = adMarkup?.id

    fun appId() = adMarkup?.advAppId

    fun adUnit() = adMarkup

    private var mraidFiles: MutableMap<String, String> = java.util.HashMap()

    @VisibleForTesting
    var incentivizedTextSettings: MutableMap<String, String> = java.util.HashMap()

    var assetsFullyDownloaded: Boolean = false

    fun getAdType() = adMarkup?.adType

    @Transient
    var adConfig: AdConfig? = null

    @Transient
    var adSize: BannerAdSize? = null

    @Transient
    var assetDirectory: File? = null
        private set

    fun omEnabled(): Boolean {
        return adMarkup?.viewability?.om?.isEnabled ?: false
    }

    fun isClickCoordinatesTrackingEnabled(): Boolean {
        return adMarkup?.clickCoordinatesEnabled ?: false
    }

    fun getDownloadableUrls(): Map<String, String> {
        val ret = HashMap<String, String>()
        if (!isNativeTemplateType()) {
            adMarkup?.templateURL?.let { url ->
                if (isValidUrl(url)) {
                    ret[KEY_TEMPLATE] = url
                }
            }
        }

        adMarkup?.templateSettings?.cacheableReplacements?.forEach {
            it.value.url?.let { httpUrl ->
                if (isValidUrl(httpUrl)) {
                    val fileName = FileUtility.guessFileName(httpUrl, it.value.extension)
                    ret[fileName] = httpUrl
                }
            }
        }

        return ret
    }

    fun getMainVideoUrl(): String? {
        return adMarkup?.templateSettings?.cacheableReplacements?.let {
            it["MAIN_VIDEO"]?.let { mainVideoToken ->
                val url = mainVideoToken.url
                if (isValidUrl(url)) url else null
            }
        }
    }

    fun getTpatUrls(event: String, value: String? = null): List<String>? {
        if (adMarkup?.tpat?.containsKey(event) == false) {
            AnalyticsClient.logError(
                VungleError.INVALID_TPAT_KEY,
                "Invalid tpat key: $event",
                placementId(),
                getCreativeId(),
                eventId(),
            )
            return null
        }
        val urls = adMarkup?.tpat?.get(event)
        if (urls.isNullOrEmpty()) {
            AnalyticsClient.logError(
                VungleError.EMPTY_TPAT_ERROR,
                "Empty tpat key: $event",
                placementId(),
                getCreativeId(),
                eventId(),
            )
            return null
        }
        when (event) {
            CHECKPOINT_0 -> {
                val tpat0Urls = mutableListOf<String>()
                urls.forEach {
                    val url = it.replace(
                        Pattern.quote(REMOTE_PLAY_KEY).toRegex(),
                        "${!assetsFullyDownloaded}"
                    )
                    tpat0Urls.add(url)
                }

                return tpat0Urls
            }

            DEEPLINK_CLICK -> {
                val tpatDeepLinkClickUrls = mutableListOf<String>()
                urls.forEach {
                    val url = it.replace(
                        Pattern.quote(DEEPLINK_SUCCESS_KEY).toRegex(), value ?: ""
                    )
                    tpatDeepLinkClickUrls.add(url)
                }

                return tpatDeepLinkClickUrls
            }
        }

        return urls
    }

    fun hasExpired(): Boolean {
        return adMarkup?.expiry?.run {
            this < System.currentTimeMillis() / 1000L
        } == true
    }

    fun getWinNotifications(): List<String>? {
        return adMarkup?.notification
    }

    fun isNativeTemplateType(): Boolean {
        return "native" == adMarkup?.templateType
    }

    fun templateType(): String? {
        return adMarkup?.templateType
    }

    fun setIncentivizedText(
        title: String,
        body: String,
        keepWatching: String,
        close: String
    ) {
        if (title.isNotEmpty()) {
            incentivizedTextSettings[INCENTIVIZED_TITLE_TEXT] = title
        }
        if (body.isNotEmpty()) {
            incentivizedTextSettings[INCENTIVIZED_BODY_TEXT] = body
        }
        if (keepWatching.isNotEmpty()) {
            incentivizedTextSettings[INCENTIVIZED_CONTINUE_TEXT] = keepWatching
        }
        if (close.isNotEmpty()) {
            incentivizedTextSettings[INCENTIVIZED_CLOSE_TEXT] = close
        }
    }

    fun setMraidAssetDir(dir: File, downloadedAssets: List<String>) {
        requireNotNull(adMarkup?.templateSettings) { "Advertisement does not have MRAID Arguments!" }
        assetDirectory = dir
        adMarkup?.templateSettings?.cacheableReplacements?.forEach {
            it.value.url?.let { httpUrl ->
                if (isValidUrl(httpUrl)) {
                    val fileName = FileUtility.guessFileName(httpUrl, it.value.extension)
                    val file = File(dir, fileName)
                    //Saving file urls to mraidFiles map if file exists
                    if (file.exists() && downloadedAssets.contains(file.absolutePath)) {
                        mraidFiles[it.key] = FILE_SCHEME + file.path
                    }
                }
            }
        }
    }

    fun setAssetFullyDownloaded() {
        assetsFullyDownloaded = true
    }

    fun getMRAIDArgsInMap(): MutableMap<String, String> {
        requireNotNull(adMarkup?.templateSettings) { "Advertisement does not have MRAID Arguments!" }

        val resultMap = mutableMapOf<String, String>()
        adMarkup?.templateSettings?.normalReplacements?.let {
            resultMap.putAll(it)
        }
        adMarkup?.templateSettings?.cacheableReplacements?.forEach {
            it.value.url?.let { url ->
                resultMap[it.key] = url
            }
        }

        if (mraidFiles.isNotEmpty()) {
            resultMap.putAll(mraidFiles)
        }

        if (incentivizedTextSettings.isNotEmpty()) { // replacing incentivized dialog text
            resultMap.putAll(incentivizedTextSettings)
        }

        return resultMap
    }

    fun createMRAIDArgs(): JsonObject {
        val resultMap = getMRAIDArgsInMap()

        val ret = buildJsonObject {
            resultMap.forEach {
                put(it.key, it.value)
            }
        }

        return ret
    }

    fun getShowCloseDelay(incentivized: Boolean?): Int {
        if (incentivized == true) {
            return adMarkup?.showCloseIncentivized?.let {
                it * 1000
            } ?: 0
        }
        return adMarkup?.showClose?.let {
            it * 1000
        } ?: 0
    }

    fun getCreativeId(): String {
        var creativeId: String? = null
        adMarkup?.campaign?.run {
            if (isNotEmpty()) {
                val campaignArr = split("\\|".toRegex()).toTypedArray()
                if (campaignArr.size >= 2) {
                    creativeId = campaignArr[1]
                }
            }
        }

        return creativeId ?: UNKNOWN
    }

    /// This expiry is the UTC value
    fun getExpiry(): Int {
        return adMarkup?.expiry ?: 0
    }

    @Serializable
    data class PlacementAdUnit(
        @SerialName("placement_reference_id") val placementReferenceId: String? = null,
        @SerialName("ad_markup") val adMarkup: AdUnit? = null
    )

    @Serializable
    data class AdUnit(
        val id: String? = null,
        val adType: String? = null,
        val adSource: String? = null,
        val campaign: String? = null,
        val expiry: Int? = null,
        @SerialName("app_id") val advAppId: String? = null,
        val callToActionUrl: String? = null,
        val deeplinkUrl: String? = null,
        @SerialName("click_coordinates_enabled") val clickCoordinatesEnabled: Boolean? = null,
        @Serializable(with = TpatSerializer::class) val tpat: Map<String, List<String>>? = null,
        val templateURL: String? = null,
        val templateId: String? = null,
        @SerialName("template_type") val templateType: String? = null,
        val templateSettings: TemplateSettings? = null,
        @SerialName("bid_token") val bidToken: String? = null,
        @SerialName("ad_market_id") val adMarketId: String? = null,
        val info: String? = null,
        val sleep: Int? = null,
        val viewability: Viewability? = null,
        val adExt: String? = null,
        val notification: List<String>? = null,
        @SerialName("load_ad") val loadAdUrls: List<String>? = null,
        val timestamp: Int? = null,
        val showCloseIncentivized: Int? = 0,
        val showClose: Int? = 0,
        @SerialName("error_code") val errorCode: Int? = null
    )

    object TpatSerializer : JsonTransformingSerializer<Map<String, List<String>>>(
        MapSerializer(
            String.serializer(),
            ListSerializer(String.serializer())
        )
    ) {
        // Filter out top-level key value pair with the key "moat"
        override fun transformDeserialize(element: JsonElement): JsonElement =
            JsonObject(element.jsonObject.filterNot { (k, _) ->
                k == "moat"
            })
    }

    @Serializable
    data class TemplateSettings(
        @SerialName("normal_replacements") val normalReplacements: Map<String, String>? = null,
        @SerialName("cacheable_replacements") val cacheableReplacements: Map<String, CacheableReplacement>? = null
    )

    @Serializable
    data class CacheableReplacement(val url: String? = null, val extension: String? = null)

    @Serializable
    data class Viewability(val om: ViewabilityInfo? = null)

    @Serializable
    data class ViewabilityInfo(
        @SerialName("is_enabled") val isEnabled: Boolean? = null,
        @SerialName("extra_vast") val extraVast: String? = null
    )
}
