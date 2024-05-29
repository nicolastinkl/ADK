package com.vungle.ads.internal.network

import android.util.Log
import com.vungle.ads.AnalyticsClient
import com.vungle.ads.TpatRetryFailure
import com.vungle.ads.internal.persistence.FilePreferences
import com.vungle.ads.internal.protos.Sdk
import com.vungle.ads.internal.util.PathProvider
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.Executor


class TpatSender(
    val vungleApiClient: VungleApiClient,
    val placementId: String?,
    val creativeId: String?,
    val eventId: String?,
    ioExecutor: Executor,
    pathProvider: PathProvider,
) {
    companion object {
        private const val TAG: String = "TpatSender"
        private const val FAILED_TPATS = "FAILED_TPATS"
        private const val MAX_RETRIES = 5
    }

    private val tpatFilePreferences = FilePreferences(ioExecutor, pathProvider, "failedTpats")

    fun sendWinNotification(urlString: String, executor: Executor) {
        executor.execute {
            val error = vungleApiClient.pingTPAT(urlString)
            if (error != null) {
                AnalyticsClient.logError(
                    Sdk.SDKError.Reason.AD_WIN_NOTIFICATION_ERROR,
                    "Fail to send $urlString, error: ${error.description}",
                    placementId, creativeId, eventId
                )
            }
        }
    }

    fun sendTpat(urlString: String, executor: Executor) {
        executor.execute {
            val storedTpats = getStoredTpats()
            val attemptNumber = storedTpats[urlString] ?: 0
            val error = vungleApiClient.pingTPAT(urlString)
            if (error == null) {
                if (attemptNumber != 0) {
                    storedTpats.remove(urlString)
                    saveStoredTpats(storedTpats)
                }
            } else {
                if (!error.errorIsTerminal) {
                    if (attemptNumber >= MAX_RETRIES) {
                        storedTpats.remove(urlString)
                        saveStoredTpats(storedTpats)
                        TpatRetryFailure(urlString).logErrorNoReturnValue()
                    } else {
                        storedTpats[urlString] = attemptNumber + 1
                        saveStoredTpats(storedTpats)
                    }
                }
                Log.e(TAG, "TPAT failed with ${error.description}, url:$urlString")
                if (error.reason == Sdk.SDKMetric.SDKMetricType.NOTIFICATION_REDIRECT_VALUE) {
                    AnalyticsClient.logMetric(
                        metricType = Sdk.SDKMetric.SDKMetricType.NOTIFICATION_REDIRECT,
                        placementId = placementId,
                        metaData = urlString
                    )
                } else {
                    AnalyticsClient.logError(
                        Sdk.SDKError.Reason.TPAT_ERROR,
                        "Fail to send $urlString, error: ${error.description}",
                        placementId, creativeId, eventId
                    )
                }
            }
        }
    }

    private fun getStoredTpats(): HashMap<String, Int> {
        val storedTpats = tpatFilePreferences.getString(FAILED_TPATS)
        return if (storedTpats != null) {
            Json.decodeFromString(storedTpats)
        } else {
            HashMap()
        }
    }

    private fun saveStoredTpats(tpats: HashMap<String, Int>) {
        tpatFilePreferences
            .put(FAILED_TPATS, Json.encodeToString(tpats))
            .apply()
    }

    internal fun resendStoredTpats(executor: Executor) {
        getStoredTpats().forEach { (url, _) -> sendTpat(url, executor) }
    }


    fun pingUrl(url: String, executor: Executor) {
        executor.execute {
            val error = vungleApiClient.pingTPAT(url)
            if (error != null) {
                Log.e(TAG, "Ping URL failed with ${error.description}, url:$url")
            }
        }
    }
}
