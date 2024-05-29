package com.vungle.ads.internal

import android.net.Uri
import androidx.annotation.VisibleForTesting
import android.util.Log
import com.vungle.ads.AnalyticsClient
import com.vungle.ads.VungleError
import com.vungle.ads.internal.model.ConfigPayload
import com.vungle.ads.internal.model.Placement

object ConfigManager {
    const val TAG = "ConfigManager"
    private var config: ConfigPayload? = null
    private var endpoints: ConfigPayload.Endpoints? = null
    private var placements: List<Placement>? = null

    fun initWithConfig(config: ConfigPayload) {
        this.config = config
        endpoints = config.endpoints
        placements = config.placements
    }

    fun placements() = placements

    fun getPlacement(id: String): Placement? {
        return placements?.find {
            it.referenceId == id
        }
    }

    fun getAdsEndpoint(): String? {
        return endpoints?.adsEndpoint
    }

    fun getRiEndpoint(): String? {
        return endpoints?.riEndpoint
    }

    fun getMraidEndpoint(): String? {
        return endpoints?.mraidEndpoint
    }

    fun getMraidJsVersion(): String {
        return getMraidEndpoint()?.let {
            "mraid_${Uri.parse(it).lastPathSegment}"
        } ?: "mraid_1"
    }

    fun getGDPRConsentMessage(): String? {
        return config?.gdpr?.consentMessage
    }

    fun getGDPRConsentTitle(): String? {
        return config?.gdpr?.consentTitle
    }

    fun getGDPRButtonAccept(): String? {
        return config?.gdpr?.buttonAccept
    }

    fun getGDPRButtonDeny(): String? {
        return config?.gdpr?.buttonDeny
    }

    fun getGDPRConsentMessageVersion(): String {
        return config?.gdpr?.consentMessageVersion ?: ""
    }

    fun getGDPRIsCountryDataProtected(): Boolean {
        return config?.gdpr?.isCountryDataProtected ?: false
    }

    fun shouldDisableAdId(): Boolean {
        return config?.disableAdId ?: true
    }

    fun adLoadOptimizationEnabled(): Boolean {
        return config?.isAdDownloadOptEnabled?.enabled ?: false
    }

    fun isReportIncentivizedEnabled(): Boolean {
        return config?.isReportIncentivizedEnabled?.enabled ?: false
    }

    fun getConfigExtension(): String {
        return config?.configExtension ?: ""
    }

    fun omEnabled() = config?.viewability?.om ?: false

    fun heartbeatEnabled() = config?.template?.heartbeatEnabled ?: false

    fun getMetricsEndpoint(): String? {
        return endpoints?.metricsEndpoint
    }

    fun getErrorLoggingEndpoint(): String? {
        return endpoints?.errorLogsEndpoint
    }

    fun getMetricsEnabled(): Boolean {
        return config?.logMetricsSettings?.metricsEnabled ?: false
    }

    fun getLogLevel(): Int {
        return config?.logMetricsSettings?.errorLogLevel
            ?: AnalyticsClient.LogLevel.ERROR_LOG_LEVEL_ERROR.level
    }

    fun getSessionTimeoutInSecond(): Int {
        return config?.session?.timeout ?: 900
    }

    internal fun validateEndpoints(): Boolean {
        var valid = true
        if (endpoints?.adsEndpoint.isNullOrEmpty()) {
            AnalyticsClient.logError(
                VungleError.INVALID_ADS_ENDPOINT,
                "The ads endpoint was not provided in the config."
            )
            valid = false
        }
        if (endpoints?.riEndpoint.isNullOrEmpty()) {
            AnalyticsClient.logError(
                VungleError.INVALID_RI_ENDPOINT,
                "The ri endpoint was not provided in the config."
            )
        }
        if (endpoints?.mraidEndpoint.isNullOrEmpty()) {
            AnalyticsClient.logError(
                VungleError.MRAID_DOWNLOAD_JS_ERROR,
                "The mraid endpoint was not provided in the config."
            )
            valid = false
        }
        if (endpoints?.metricsEndpoint.isNullOrEmpty()) {
            AnalyticsClient.logError(
                VungleError.INVALID_METRICS_ENDPOINT,
                "The metrics endpoint was not provided in the config."
            )
        }
        if (endpoints?.errorLogsEndpoint.isNullOrEmpty()) {
            /* uncomment this if there is a fallback errorLogsEndpoint or logged errors are stored
            to send when valid endpoint is provided
            AnalyticsClient.logError(
                VungleError.INVALID_LOG_ERROR_ENDPOINT,
                "The error logging endpoint was not provided in the config."
            )
            */
            Log.e(TAG, "The error logging endpoint was not provided in the config.")
        }
        return valid
    }

    fun isCleverCacheEnabled(): Boolean {
        return config?.cleverCache?.enabled ?: false
    }

    fun getCleverCacheDiskSize(): Long {
        return config?.cleverCache?.diskSize?.also { return it * 1024 * 1024 }
            ?: (1000 * 1024 * 1024)
    }

    fun getCleverCacheDiskPercentage(): Int {
        return config?.cleverCache?.diskPercentage ?: 3
    }
}
