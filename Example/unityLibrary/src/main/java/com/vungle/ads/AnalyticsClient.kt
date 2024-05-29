package com.vungle.ads

import android.os.Build
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import com.vungle.ads.internal.executor.VungleThreadPoolExecutor
import com.vungle.ads.internal.network.VungleApiClient
import com.vungle.ads.internal.protos.Sdk
import com.vungle.ads.internal.util.ActivityManager
import com.vungle.ads.internal.util.Logger
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

/**
 * Main Class for tracking analytics
 */

object AnalyticsClient {

    enum class LogLevel(val level: Int) {
        ERROR_LOG_LEVEL_OFF(0), ERROR_LOG_LEVEL_ERROR(1), ERROR_LOG_LEVEL_DEBUG(2);

        companion object {
            fun fromValue(logLevel: Int): LogLevel {
                when (logLevel) {
                    ERROR_LOG_LEVEL_DEBUG.level -> {
                        return ERROR_LOG_LEVEL_DEBUG
                    }
                    ERROR_LOG_LEVEL_ERROR.level -> {
                        return ERROR_LOG_LEVEL_ERROR
                    }
                    ERROR_LOG_LEVEL_OFF.level -> {
                        return ERROR_LOG_LEVEL_OFF
                    }
                }
                return ERROR_LOG_LEVEL_ERROR
            }
        }
    }

    private val TAG: String = AnalyticsClient::class.java.simpleName
    @VisibleForTesting
    internal val errors: BlockingQueue<Sdk.SDKError.Builder> =
        LinkedBlockingQueue()
    @VisibleForTesting
    internal val metrics: BlockingQueue<Sdk.SDKMetric.Builder> =
        LinkedBlockingQueue()
    @VisibleForTesting
    internal val pendingErrors: BlockingQueue<Sdk.SDKError.Builder> =
        LinkedBlockingQueue()
    @VisibleForTesting
    internal val pendingMetrics: BlockingQueue<Sdk.SDKMetric.Builder> =
        LinkedBlockingQueue()

    /**
     * Max time before any queued items are sent out
     */
    private const val refreshTimeMillis = 5000L

    /**
     * Max number of items queued before we send them out
     */
    private const val maxBatchSize = 20
    private var maxErrorLogLevel = Integer.MAX_VALUE

    @VisibleForTesting
    internal var vungleApiClient: VungleApiClient? = null
    @VisibleForTesting
    internal var executor: VungleThreadPoolExecutor? = null
    @VisibleForTesting
    internal var metricsEnabled = false

    private var logLevel = LogLevel.ERROR_LOG_LEVEL_ERROR
    private var paused = false

    @VisibleForTesting
    internal var refreshEnabled = true

    internal fun init(
        vungleApiClient: VungleApiClient,
        executor: VungleThreadPoolExecutor,
        errorLogLevel: Int,
        metricsEnabled: Boolean,
    ) {

        this.executor = executor
        this.vungleApiClient = vungleApiClient
        this.metricsEnabled = metricsEnabled

        try {
            if (pendingErrors.isNotEmpty()) {
                pendingErrors.drainTo(errors)
            }
        } catch (ex: Exception) {
            Logger.e(TAG, "Failed to add pendingErrors to errors queue.", ex)
        }
        try {
            if (pendingMetrics.isNotEmpty()) {
                pendingMetrics.drainTo(metrics)
            }
        } catch (ex: Exception) {
            Logger.e(TAG, "Failed to add pendingMetrics to metrics queue.", ex)
        }

        if (refreshEnabled) {
            Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(
                    {
                        // Execute all tasks in the same thread
                        executor.execute {
                            report()
                        }
                    },
                    0,
                    refreshTimeMillis,
                    TimeUnit.MILLISECONDS
                )
        }

        maxErrorLogLevel = errorLogLevel
        logLevel = LogLevel.fromValue(errorLogLevel)

        when (errorLogLevel) {
            LogLevel.ERROR_LOG_LEVEL_DEBUG.level -> {
                Logger.enable(true)
            }
            LogLevel.ERROR_LOG_LEVEL_ERROR.level -> {
                Logger.enable(false)
            }
            LogLevel.ERROR_LOG_LEVEL_OFF.level -> {
                Logger.enable(false)
            }
        }
        ActivityManager.instance.addListener(object : ActivityManager.LifeCycleCallback() {
            override fun onResume() {
                super.onResume()
                resume()
            }

            override fun onPause() {
                super.onPause()
                pause()
            }
        })
    }

    @Synchronized
    internal fun logError(
        reason: Sdk.SDKError.Reason,
        message: String,
        placementRefId: String? = null,
        creativeId: String? = null,
        eventId: String? = null,
    ) {
        try {
            if (executor == null) {
                val error = genSDKError(reason, message, placementRefId, creativeId, eventId)
                pendingErrors.put(error)
                return
            }

            executor?.execute {
                logErrorInSameThread(reason, message, placementRefId, creativeId, eventId)
            }
        } catch (ex: Exception) {
            Logger.e(
                TAG, "Cannot logError $reason, $message, $placementRefId, $creativeId," +
                        "$eventId", ex
            )
        }
    }

    private fun genSDKError(
        reason: Sdk.SDKError.Reason,
        message: String,
        placementRefId: String? = null,
        creativeId: String? = null,
        eventId: String? = null,
    ): Sdk.SDKError.Builder {
        return Sdk.SDKError.newBuilder()
            .setOs("Android")
            .setOsVersion(Build.VERSION.SDK_INT.toString())
            .setMake(Build.MANUFACTURER)
            .setModel(Build.MODEL)
            .setReason(reason)
            .setMessage(message)
            .setAt(System.currentTimeMillis())
            .setPlacementReferenceId(placementRefId ?: "")
            .setCreativeId(creativeId ?: "")
            .setEventId(eventId ?: "")
    }

    @Synchronized
    private fun logErrorInSameThread(
        reason: Sdk.SDKError.Reason,
        message: String,
        placementRefId: String? = null,
        creativeId: String? = null,
        eventId: String? = null,
    ) {
        if (logLevel == LogLevel.ERROR_LOG_LEVEL_OFF) {
            return
        }
        try {
            val error = genSDKError(reason, message, placementRefId, creativeId, eventId)

            this.errors.put(error)
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "Logging error: $reason with message: $message")
            }
            if (errors.size >= maxBatchSize) {
                report()
            }
        } catch (ex: Exception) {
            Logger.e(TAG, "Cannot logError", ex)
        }

    }

    @Synchronized
    internal fun logError(
        reasonCode: Int,
        message: String,
        placementRefId: String? = null,
        creativeId: String? = null,
        eventId: String? = null,
    ) {
        logError(Sdk.SDKError.Reason.forNumber(reasonCode), message, placementRefId, creativeId, eventId)
    }

    @Synchronized
    internal fun logMetric(
        metricType: Sdk.SDKMetric.SDKMetricType,
        metricValue: Long = 0,
        placementId: String? = null,
        creativeId: String? = null,
        eventId: String? = null,
        metaData: String? = null,
    ) {
        try {
            if (executor == null) {
                val metric =
                    genMetric(metricType, metricValue, placementId, creativeId, eventId, metaData)
                pendingMetrics.put(metric)
                return
            }

            executor?.execute {
                logMetricInSameThread(
                    metricType,
                    metricValue,
                    placementId,
                    creativeId,
                    eventId,
                    metaData
                )
            }
        } catch (ex: Exception) {
            Logger.e(
                TAG, "Cannot logMetric $metricType, $metricValue, $placementId, $creativeId," +
                        "$eventId, $metaData", ex
            )
        }
    }

    private fun genMetric(
        metricType: Sdk.SDKMetric.SDKMetricType,
        metricValue: Long = 0,
        placementId: String? = null,
        creativeId: String? = null,
        eventId: String? = null,
        metaData: String? = null,
    ): Sdk.SDKMetric.Builder {
        return Sdk.SDKMetric.newBuilder()
            .setType(metricType)
            .setValue(metricValue)
            .setMake(Build.MANUFACTURER)
            .setModel(Build.MODEL)
            .setOs("Android")
            .setOsVersion(Build.VERSION.SDK_INT.toString())
            .setPlacementReferenceId(placementId ?: "")
            .setCreativeId(creativeId ?: "")
            .setEventId(eventId ?: "")
            .setMeta(metaData ?: "")
    }

    @Synchronized
    private fun logMetricInSameThread(
        metricType: Sdk.SDKMetric.SDKMetricType,
        metricValue: Long = 0,
        placementId: String? = null,
        creativeId: String? = null,
        eventId: String? = null,
        metaData: String? = null,
    ) {
        if (!metricsEnabled) {
            return
        }
        try {
            val metric =
                genMetric(metricType, metricValue, placementId, creativeId, eventId, metaData)

            this.metrics.put(metric)
            if (BuildConfig.DEBUG) {
                Log.d(TAG,
                    "Logging Metric $metricType with value $metricValue for placement $placementId"
                )
            }
            if (metrics.size >= maxBatchSize) {
                report()
            }
        } catch (ex: Exception) {
            Logger.e(TAG, "Cannot logMetrics", ex)
        }
    }

    @Synchronized
    internal fun logMetric(
        metric: Metric,
        placementId: String? = null,
        creativeId: String? = null,
        eventId: String? = null,
        metaData: String? = null,
    ) {
        logMetric(
            metric.metricType,
            metric.getValue(),
            placementId,
            creativeId,
            eventId,
            metaData ?: metric.meta
        )
    }

    @Synchronized
    internal fun logMetric(
        singleValueMetric: SingleValueMetric,
        placementId: String? = null,
        creativeId: String? = null,
        eventId: String? = null,
        metaData: String? = null,
    ) {
        logMetric(
            singleValueMetric as Metric,
            placementId,
            creativeId,
            eventId,
            metaData
        )
    }

    @Synchronized
    internal fun logMetric(
        timeIntervalMetric: TimeIntervalMetric,
        placementId: String? = null,
        creativeId: String? = null,
        eventId: String? = null,
        metaData: String? = timeIntervalMetric.meta,
    ) {
        logMetric(
            timeIntervalMetric as Metric,
            placementId,
            creativeId,
            eventId,
            metaData
        )
    }

    @Synchronized
    internal fun logMetric(
        oneShotTimeIntervalMetric: OneShotTimeIntervalMetric,
        placementId: String? = null,
        creativeId: String? = null,
        eventId: String? = null,
        metaData: String? = null,
        ) {
        if (!oneShotTimeIntervalMetric.isLogged()) {
            logMetric(
                oneShotTimeIntervalMetric as TimeIntervalMetric,
                placementId,
                creativeId,
                eventId,
                metaData
            )
            oneShotTimeIntervalMetric.markLogged()
        }
    }

    @Synchronized
    private fun report() {
        if (paused) {
            return
        }

        if (logLevel != LogLevel.ERROR_LOG_LEVEL_OFF && errors.size > 0) {
            flushErrors()
        }

        if (metricsEnabled && metrics.size > 0) {
            flushMetrics()
        }
    }

    @WorkerThread
    private fun flushMetrics() {
        Logger.d(TAG, message = "Sending ${this.metrics.size} metrics")
        val currentSendingMetrics: BlockingQueue<Sdk.SDKMetric.Builder> =
            LinkedBlockingQueue()
        metrics.drainTo(currentSendingMetrics)
        if (currentSendingMetrics.isEmpty()) {/* There is a somewhat slim chance that upon getting
             here there is nothing to send. And we don't need those empty calls to server */
            return
        }
        vungleApiClient?.reportMetrics(currentSendingMetrics, object : RequestListener {
            override fun onSuccess() {
                Logger.d(TAG, message = "Sent ${currentSendingMetrics.size} metrics")
            }

            override fun onFailure() {
                Logger.d(
                    TAG,
                    message = "Failed to send ${currentSendingMetrics.size} metrics"
                )
                metrics.addAll(currentSendingMetrics)
            }

        })
    }

    @WorkerThread
    private fun flushErrors() {
        Logger.d(TAG, message = "Sending ${this.errors.size} errors")
        val currentSendingErrors: BlockingQueue<Sdk.SDKError.Builder> =
            LinkedBlockingQueue()
        errors.drainTo(currentSendingErrors)
        if (currentSendingErrors.isEmpty()) {/* There is a somewhat slim chance that upon getting
             here there is nothing to send. And we don't need those empty calls to server */
            return
        }
        vungleApiClient?.reportErrors(currentSendingErrors, object : RequestListener {
            override fun onSuccess() {
                Logger.d(TAG, message = "Sent ${currentSendingErrors.size} errors")
            }

            override fun onFailure() {
                Logger.d(
                    TAG,
                    message = "Failed to send ${currentSendingErrors.size} errors"
                )
                errors.addAll(currentSendingErrors)
            }
        })
    }

    fun pause() {
        paused = true
    }

    fun resume() {
        paused = false
    }

    interface RequestListener {
        fun onSuccess()
        fun onFailure()
    }

}
