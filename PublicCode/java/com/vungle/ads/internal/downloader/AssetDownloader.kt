package com.vungle.ads.internal.downloader


import android.os.Build
import android.util.Log
import com.vungle.ads.*
import com.vungle.ads.VungleError.Companion.NO_SPACE_TO_DOWNLOAD_ASSETS
import com.vungle.ads.internal.ConfigManager
import com.vungle.ads.internal.downloader.AssetDownloadListener.DownloadError.Companion.DEFAULT_SERVER_CODE
import com.vungle.ads.internal.downloader.AssetDownloadListener.DownloadError.ErrorReason.Companion.DISK_ERROR
import com.vungle.ads.internal.downloader.AssetDownloadListener.DownloadError.ErrorReason.Companion.FILE_NOT_FOUND_ERROR
import com.vungle.ads.internal.downloader.AssetDownloadListener.DownloadError.ErrorReason.Companion.INTERNAL_ERROR
import com.vungle.ads.internal.downloader.AssetDownloadListener.DownloadError.ErrorReason.Companion.REQUEST_ERROR
import com.vungle.ads.internal.executor.VungleThreadPoolExecutor
import com.vungle.ads.internal.protos.Sdk
import com.vungle.ads.internal.task.PriorityRunnable
import com.vungle.ads.internal.util.FileUtility
import com.vungle.ads.internal.util.PathProvider
import okhttp3.Cache
import okhttp3.Call
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.http.RealResponseBody
import okio.BufferedSink
import okio.BufferedSource
import okio.GzipSource
import okio.appendingSink
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException
import java.lang.Long.min
import java.net.ProtocolException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

class AssetDownloader(
    private val downloadExecutor: VungleThreadPoolExecutor,
    private val pathProvider: PathProvider
) : Downloader {

    companion object {
        private const val TAG = "AssetDownloader"
        private const val MINIMUM_SPACE_REQUIRED_MB = 20 * 1024 * 1024
        private const val DOWNLOAD_CHUNK_SIZE = 2048 //Same as Okio Segment.SIZE
        private const val MAX_PERCENT: Long = 100
        private const val PROGRESS_STEP = 5
        private const val TIMEOUT = 30
        private const val CONTENT_ENCODING = "Content-Encoding"
        private const val CONTENT_TYPE = "Content-Type"
        private const val GZIP = "gzip"
        private const val IDENTITY = "identity"

    }

    private var okHttpClient: OkHttpClient
    private val progressStep = PROGRESS_STEP

    private val transitioning = mutableListOf<DownloadRequest>()

    init {
        val builder = OkHttpClient.Builder()
            .readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            .cache(null)
            .followRedirects(true)
            .followSslRedirects(true)
        if (ConfigManager.isCleverCacheEnabled()) {
            val diskSize = ConfigManager.getCleverCacheDiskSize()
            val diskPercentage = ConfigManager.getCleverCacheDiskPercentage()
            val maxDiskCapacity =
                pathProvider.getAvailableBytes(pathProvider.getCleverCacheDir().absolutePath) * diskPercentage / 100
            val diskCapacity = min(diskSize, maxDiskCapacity)
            if (diskCapacity > 0) {
                builder.cache(Cache(pathProvider.getCleverCacheDir(), diskCapacity))
            } else {
                Log.w(TAG, "cache disk capacity size <=0, no clever cache active.")
            }
        }

        okHttpClient = builder.build()
    }

    override fun download(
        downloadRequest: DownloadRequest?,
        downloadListener: AssetDownloadListener?
    ) {

        if (downloadRequest == null) {
            return
        }

        transitioning.add(downloadRequest)

        downloadExecutor.execute(object : PriorityRunnable() {
            override fun run() {
                launchRequest(downloadRequest, downloadListener)
            }

            override val priority: Int
                get() = downloadRequest.getPriority()
        }) {
            deliverError(
                downloadRequest, downloadListener, AssetDownloadListener.DownloadError(
                    DEFAULT_SERVER_CODE,
                    InternalError(VungleError.OUT_OF_MEMORY),
                    INTERNAL_ERROR
                )
            )
        }
    }

    private fun deliverError(
        downloadRequest: DownloadRequest,
        downloadListener: AssetDownloadListener?,
        downloadError: AssetDownloadListener.DownloadError?
    ) {
        downloadListener?.onError(downloadError, downloadRequest)
    }

    override fun cancel(request: DownloadRequest?) {
        if (request == null || request.isCancelled()) return

        request.cancel()
    }

    override fun cancelAll() {
        transitioning.forEach {
            cancel(it)
        }
        transitioning.clear()
    }

    private fun launchRequest(
        downloadRequest: DownloadRequest,
        downloadListener: AssetDownloadListener?
    ) {
        Log.d(
            TAG,
            "launch request in thread:" + Thread.currentThread().id + " request: ${downloadRequest.url}"
        )

        if (downloadRequest.isCancelled()) {
            Log.d(
                TAG,
                "Request " + downloadRequest.url.toString() + " is cancelled before starting"
            )
            val progress = AssetDownloadListener.Progress()
            progress.status = AssetDownloadListener.Progress.ProgressStatus.CANCELLED
            deliverProgress(progress, downloadRequest, downloadListener)
            return
        }

        var done = false
        val progress = AssetDownloadListener.Progress()
        progress.timestampDownloadStart = System.currentTimeMillis()
        var downloadError: AssetDownloadListener.DownloadError? = null

        val url: String? = downloadRequest.url
        val path: String? = downloadRequest.path

        if (url.isNullOrEmpty() || !isValidUrl(url)) {
            deliverError(
                downloadRequest, downloadListener, AssetDownloadListener.DownloadError(
                    DEFAULT_SERVER_CODE,
                    AssetDownloadError(),
                    INTERNAL_ERROR
                )
            )
            return
        }
        if (path.isNullOrEmpty()) {
            deliverError(
                downloadRequest, downloadListener, AssetDownloadListener.DownloadError(
                    DEFAULT_SERVER_CODE,
                    AssetDownloadError(),
                    FILE_NOT_FOUND_ERROR
                )
            )
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && !checkSpaceAvailable()) {
            deliverError(
                downloadRequest, downloadListener, AssetDownloadListener.DownloadError(
                    DEFAULT_SERVER_CODE,
                    InternalError(NO_SPACE_TO_DOWNLOAD_ASSETS),
                    DISK_ERROR
                )
            )
            return
        }
        val file = File(path)

        while (!done) {
            done = true
            var sink: BufferedSink? = null
            var source: BufferedSource? = null
            var totalRead: Long = 0
            var call: Call? = null
            var downloaded: Long
            var code: Int
            var response: Response? = null
            try {
                val parentFile = file.parentFile
                if (parentFile != null && !parentFile.exists()) {
                    parentFile.mkdirs()
                }
                downloaded = if (file.exists()) file.length() else 0

                val requestBuilder = Request.Builder().url(url)

                call = okHttpClient.newCall(requestBuilder.build())
                response = call.execute()

                var contentLength: Long = getContentLength(response)
                code = response.code
                if (!response.isSuccessful) {
                    AssetFailedStatusCodeError(url, code = code, placementId = downloadRequest.placementId).logErrorNoReturnValue()
                    throw Downloader.RequestException("Code: $code")
                }

                response.cacheResponse?.let {
                    AnalyticsClient.logMetric(
                        SingleValueMetric(Sdk.SDKMetric.SDKMetricType.CACHED_ASSETS_USED),
                        downloadRequest.placementId,
                        downloadRequest.creativeId,
                        downloadRequest.eventId,
                        url
                    )
                }

                val headers = response.headers
                val contentEncoding = headers[CONTENT_ENCODING]
                if (contentEncoding != null &&
                    !GZIP.equals(contentEncoding, ignoreCase = true) &&
                    !IDENTITY.equals(contentEncoding, ignoreCase = true)
                ) {
                    Log.w(TAG, "loadAd: Unknown $CONTENT_ENCODING $contentEncoding")
                    throw IOException("Unknown $CONTENT_ENCODING $contentEncoding")
                }

                val body = decodeGzipIfNeeded(response)

                source = body?.source()

                Log.d(TAG, "Start download from bytes:$downloaded, url: $url")

                val offset = downloaded
                contentLength += offset
                Log.d(TAG, "final offset = $offset")

                sink = (if (offset == 0L) file.sink() else file.appendingSink()).buffer()

                var read: Long
                progress.status = AssetDownloadListener.Progress.ProgressStatus.STARTED
                progress.sizeBytes = body?.contentLength() ?: 0
                progress.startBytes = offset

                deliverProgress(progress, downloadRequest, downloadListener)
                var current = 0

                while ((source?.read(sink.buffer, DOWNLOAD_CHUNK_SIZE.toLong())
                        ?: -1L).apply { read = this } > 0
                ) {
                    if (!file.exists()) {
                        AnalyticsClient.logError(
                            VungleError.ASSET_WRITE_ERROR,
                            "Asset save error $url"
                        )
                        throw Downloader.RequestException("File is not existing")
                    }

                    if (downloadRequest.isCancelled()) {
                        progress.status = AssetDownloadListener.Progress.ProgressStatus.CANCELLED
                        break
                    }

                    sink.emit()

                    totalRead += read

                    downloaded = offset + totalRead

                    if (contentLength > 0) {
                        current = (downloaded * MAX_PERCENT / contentLength).toInt()
                    }
                    while (progress.progressPercent + progressStep <= current
                        && progress.progressPercent + progressStep <= MAX_PERCENT
                    ) {
                        progress.status =
                            AssetDownloadListener.Progress.ProgressStatus.IN_PROGRESS
                        progress.progressPercent += progressStep
                        deliverProgress(progress, downloadRequest, downloadListener)
                    }
                }

                sink.flush()

                if (progress.status == AssetDownloadListener.Progress.ProgressStatus.IN_PROGRESS) {
                    progress.status = AssetDownloadListener.Progress.ProgressStatus.DONE
                }

            } catch (ex: Exception) {
                Log.e("AssetDownloader", "$ex")
                // https://vungle.atlassian.net/browse/AND-4521
                // If asset returns response code 100, ProtocolException will happen when calling
                // call.execute(). That's why to add below check.
                if (ex is ProtocolException) {
                    AnalyticsClient.logError(
                        VungleError.ASSET_REQUEST_ERROR,
                        "Failed to load asset: ${downloadRequest.url}"
                    )
                } else if (ex is UnknownHostException || ex is IOException) {
                    AssetFailedStatusCodeError(url, placementId = downloadRequest.placementId).logErrorNoReturnValue()
                }

                progress.status = AssetDownloadListener.Progress.ProgressStatus.ERROR
                downloadError =
                    AssetDownloadListener.DownloadError(DEFAULT_SERVER_CODE, ex, REQUEST_ERROR)
            } finally {
                if (response?.body != null) {
                    response.body?.close()
                }

                call?.cancel()

                FileUtility.closeQuietly(sink)
                FileUtility.closeQuietly(source)

                when (progress.status) {
                    AssetDownloadListener.Progress.ProgressStatus.CANCELLED -> {
                        deliverProgress(progress, downloadRequest, downloadListener)
                    }

                    AssetDownloadListener.Progress.ProgressStatus.DONE -> {
                        deliverSuccess(file, downloadRequest, downloadListener)
                    }

                    AssetDownloadListener.Progress.ProgressStatus.STARTED -> {
                        if (downloadError != null) {
                            deliverError(downloadRequest, downloadListener, downloadError)
                        }
                    }

                    AssetDownloadListener.Progress.ProgressStatus.ERROR -> {
                        deliverError(downloadRequest, downloadListener, downloadError)
                    }

                    else -> {
                        Log.w(TAG, "status:${progress.status}")
                        deliverError(downloadRequest, downloadListener, downloadError)
                    }
                }

            }

        }
    }

    private fun decodeGzipIfNeeded(networkResponse: Response): ResponseBody? {
        val resp = networkResponse.body
        if (GZIP.equals(
                networkResponse.header(CONTENT_ENCODING),
                ignoreCase = true
            ) && resp != null
        ) {
            val responseBody = GzipSource(resp.source())
            val contentType = networkResponse.header(CONTENT_TYPE)
            return RealResponseBody(contentType, -1L, responseBody.buffer())
        }
        return resp
    }

    private fun getContentLength(response: Response): Long {
        var header = response.headers["Content-Length"]

        // OkHttp3 has removed Content-Length header but the original response header is available.
        // https://github.com/square/okhttp/issues/259#issuecomment-561586273
        if (header.isNullOrEmpty()) {
            header = response.networkResponse?.header("Content-Length")
        }

        return if (header.isNullOrEmpty()) -1 else try {
            header.toLong()
        } catch (t: Throwable) {
            -1
        }
    }

    private fun deliverSuccess(
        file: File,
        downloadRequest: DownloadRequest, listener: AssetDownloadListener?
    ) {
        Log.d(TAG, "On success $downloadRequest")
        listener?.onSuccess(file, downloadRequest)
    }

    private fun deliverProgress(
        copy: AssetDownloadListener.Progress,
        downloadRequest: DownloadRequest,
        listener: AssetDownloadListener?
    ) {
        Log.d(TAG, "On progress $downloadRequest")
        listener?.onProgress(copy, downloadRequest)
    }

    private fun checkSpaceAvailable(): Boolean {
        val availableBytes =
            pathProvider.getAvailableBytes(pathProvider.getVungleDir().absolutePath)
        if (availableBytes < MINIMUM_SPACE_REQUIRED_MB) {
            AnalyticsClient.logError(
                VungleError.ASSET_FAILED_INSUFFICIENT_SPACE,
                "Insufficient space $availableBytes"
            )
            return false
        }
        return true
    }

    private fun isValidUrl(httpUrl: String?): Boolean {
        return !httpUrl.isNullOrEmpty() && httpUrl.toHttpUrlOrNull() != null
    }
}
