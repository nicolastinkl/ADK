package com.vungle.ads.internal.downloader

import com.vungle.ads.AnalyticsClient
import com.vungle.ads.TimeIntervalMetric
import com.vungle.ads.internal.protos.Sdk
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

class DownloadRequest {

    var networkType = 0
    var url: String? = null
    var path: String? = null
    var pauseOnConnectionLost = false
    private var priority = AtomicReference<Priority>()
    var id: String? = null
    var cookieString: String? = null
    private val cancelled = AtomicBoolean(false)
    var advertisementId: String? = null
    var isTemplate: Boolean = false
    var isMainVideo: Boolean = false
    var placementId: String? = null
    var creativeId: String? = null
    var eventId: String? = null

    private var downloadDuration: TimeIntervalMetric? = null

    override fun toString(): String {
        return "DownloadRequest{" +
                "networkType=" + networkType +
                ", priority=" + priority +
                ", url='" + url + '\'' +
                ", path='" + path + '\'' +
                ", pauseOnConnectionLost=" + pauseOnConnectionLost +
                ", id='" + id + '\'' +
                ", cookieString='" + cookieString + '\'' +
                ", cancelled=" + cancelled +
                ", advertisementId=" + advertisementId +
                ", placementId=" + placementId +
                ", creativeId=" + creativeId +
                ", eventId=" + eventId +
                '}'
    }

    enum class Priority(val priority: Int) {
        CRITICAL(-Int.MAX_VALUE), //for internal usage in AssetDownloader only
        HIGHEST(0),
        HIGH(1),
        LOWEST(Int.MAX_VALUE)

    }

    constructor(
        priority: Priority,
        url: String?,
        path: String?,
        cookieString: String?,
        isTemplate: Boolean = false,
        isMainVideo: Boolean = false,
        placementId: String? = null,
        creativeId: String? = null,
        eventId: String? = null,
    ) : this(
        Downloader.NetworkType.ANY, priority,
        url, path, false, cookieString, null, isTemplate, isMainVideo, placementId, creativeId, eventId
    )

    constructor(
        @Downloader.NetworkType networkType: Int,
        priority: Priority,
        url: String?,
        path: String?,
        pauseOnConnectionLost: Boolean,
        cookieString: String?,
        advertisementId: String?,
        isTemplate: Boolean = false,
        isMainVideo: Boolean = false,
        placementId: String? = null,
        creativeId: String? = null,
        eventId: String? = null,
    ) {
        this.url = url
        this.networkType = networkType
        this.priority.set(priority)
        this.path = path
        this.pauseOnConnectionLost = pauseOnConnectionLost
        this.cookieString = cookieString
        this.advertisementId = advertisementId
        this.isTemplate = isTemplate
        this.isMainVideo = isMainVideo
        this.placementId = placementId
        this.creativeId = creativeId
        this.eventId = eventId
    }

    fun cancel() {
        cancelled.set(true)
    }

    fun isCancelled(): Boolean {
        return cancelled.get()
    }

    fun setPriority(priority: Priority) {
        this.priority.set(priority)
    }

    fun getPriority(): Int {
        return priority.get().priority
    }

    fun startRecord() {
        downloadDuration =
            TimeIntervalMetric(Sdk.SDKMetric.SDKMetricType.TEMPLATE_DOWNLOAD_DURATION_MS)
        downloadDuration?.markStart()
    }

    fun stopRecord() {
        downloadDuration?.let {
            it.markEnd()
            AnalyticsClient.logMetric(
                timeIntervalMetric = it,
                placementId = placementId,
                creativeId = creativeId,
                eventId = eventId,
                metaData = url
            )
        }
    }

}
