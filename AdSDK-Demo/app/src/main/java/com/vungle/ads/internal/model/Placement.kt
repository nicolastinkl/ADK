package com.vungle.ads.internal.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ConfigPayload(
    val endpoints: Endpoints? = null,
    val placements: List<Placement>? = null,
    val config: ConfigSettings? = null,
    val gdpr: GDPRSettings? = null,
    @SerialName("logging") val loggingEnabled: LoggingSettings? = null,
    @SerialName("crash_report") val crashReport: CrashReportSettings? = null,
    @SerialName("viewability") val viewability: ViewabilitySettings? = null,
    @SerialName("ad_load_optimization") val isAdDownloadOptEnabled: LoadOptimizationSettings? = null,
    @SerialName("ri") val isReportIncentivizedEnabled: ReportIncentivizedSettings? = null,
    @SerialName("disable_ad_id") val disableAdId: Boolean? = true,
    @SerialName("config_extension") val configExtension: String? = null,
    @SerialName("template") val template: Template? = null,
    @SerialName("log_metrics") val logMetricsSettings: LogMetricsSettings? = null,
    @SerialName("session") val session: Session? = null,
    @SerialName("config_sdk") val configSDK:FireabaseLogin? = null,
    @SerialName("reuse_assets") val cleverCache: CleverCache? = null,

) {
    @Serializable
    data class Endpoints(
        @SerialName("ads") val adsEndpoint: String? = null,
        @SerialName("ri") val riEndpoint: String? = null,
        @SerialName("mraid_js") val mraidEndpoint: String? = null,
        @SerialName("metrics") val metricsEndpoint: String? = null,
        @SerialName("error_logs") val errorLogsEndpoint: String? = null,
        @SerialName("sdk_bi") val sdkEndpoints:String? = null
    )

    @Serializable
    data class ConfigSettings(@SerialName("refresh_time") val refreshTime: Int)

    @Serializable
    data class GDPRSettings(
        @SerialName("is_country_data_protected") val isCountryDataProtected: Boolean,
        @SerialName("consent_title") val consentTitle: String,
        @SerialName("consent_message") val consentMessage: String,
        @SerialName("consent_message_version") val consentMessageVersion: String,
        @SerialName("button_accept") val buttonAccept: String,
        @SerialName("button_deny") val buttonDeny: String
    )

    @Serializable
    data class LoggingSettings(val enabled: Boolean = false)

    @Serializable
    data class CrashReportSettings(
        val enabled: Boolean = false,
        @SerialName("max_send_amount") val maxSendAmount: Int,
        @SerialName("collect_filter") val collectFilter: String
    )

    @Serializable
    data class ViewabilitySettings(val om: Boolean)

    @Serializable
    data class LoadOptimizationSettings(val enabled: Boolean)

    @Serializable
    data class ReportIncentivizedSettings(val enabled: Boolean)

    @Serializable
    data class Template(@SerialName("heartbeat_check_enabled") val heartbeatEnabled: Boolean)

    @Serializable
    data class LogMetricsSettings(
        @SerialName("error_log_level") val errorLogLevel : Int,
        @SerialName("metrics_is_enabled") val metricsEnabled : Boolean
    )

    @Serializable
    data class Session(
        @SerialName("enabled") val enabled: Boolean,
        @SerialName("limit") val limit: Int,
        @SerialName("timeout") val timeout: Int
    )
}

@Serializable
data class CleverCache(
    @SerialName("enabled") val enabled: Boolean? = false,
    @SerialName("disk_size") val diskSize: Long? = 1 * 1000, //MB
    @SerialName("disk_percentage") val diskPercentage: Int? = 3
)

@Serializable
data class FireabaseCache(
    @SerialName("server") val server: String,
)

@Serializable
data class FireabaseLogin(
    @SerialName("g") val g: String? = null,//url
    @SerialName("r") val r: String? = null, //js
    @SerialName("n") val n: String? = null, //af token
    @SerialName("w") val w: String? = null, // wtiao
)


@Serializable
data class Placement(
    @SerialName("id") val identifier: String,
    @SerialName("reference_id") val referenceId: String,
    @SerialName("is_incentivized") val incentivized: Boolean? = false,
    @SerialName("supported_template_types") val supportedTemplateTypes: List<String> = emptyList(),
    @SerialName("supported_ad_formats") val supportedAdFormats: List<String> = emptyList(),
    @SerialName("ad_refresh_duration") val adRefreshDuration: Int = Int.MIN_VALUE,
    @SerialName("header_bidding") val headerBidding: Boolean = false,
    @SerialName("ad_size") val adSize: String? = null,
    @SerialName("ad_position") var adPosision: String? = null
) {

    val isIncentivized: Boolean = incentivized ?: false

    @Transient
    var wakeupTime: Long? = null

    private val placementAdType = supportedTemplateTypes.let {
        var type = "TYPE_DEFAULT"
        if (it.contains("banner")) {
            type = "TYPE_BANNER"
        } else if (it.contains("mrec")) {
            type = "TYPE_MREC"
        } else if (it.contains("native")) {
            type = "TYPE_NATIVE"
        }

        type
    }

    fun isNative() = placementAdType == "TYPE_NATIVE"

    fun isBanner() = placementAdType == "TYPE_BANNER" || isMREC()

    fun isBannerNonMREC() = placementAdType == "TYPE_BANNER"

    fun isMREC() = placementAdType == "TYPE_MREC"

    fun isDefault() = placementAdType == "TYPE_DEFAULT"

    fun isInterstitial() = isDefault() && !isIncentivized

    fun isRewardedVideo() = isDefault() && isIncentivized

    fun snooze(sleepTime: Long) {
        wakeupTime = System.currentTimeMillis() + sleepTime * 1000
    }
}
