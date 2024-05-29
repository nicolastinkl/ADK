@file:JvmName("BaseAdLoader")

package com.vungle.ads.internal.load

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.webkit.URLUtil
import androidx.annotation.WorkerThread
import com.vungle.ads.*
import com.vungle.ads.internal.ConfigManager
import com.vungle.ads.internal.downloader.AssetDownloadListener
import com.vungle.ads.internal.downloader.AssetDownloadListener.DownloadError.Companion.DEFAULT_SERVER_CODE
import com.vungle.ads.internal.downloader.AssetDownloadListener.DownloadError.ErrorReason.Companion.INTERNAL_ERROR
import com.vungle.ads.internal.downloader.AssetDownloadListener.DownloadError.ErrorReason.Companion.REQUEST_ERROR
import com.vungle.ads.internal.downloader.DownloadRequest
import com.vungle.ads.internal.downloader.Downloader
import com.vungle.ads.internal.executor.Executors
import com.vungle.ads.internal.model.AdAsset
import com.vungle.ads.internal.model.AdPayload
import com.vungle.ads.internal.model.AdPayload.Companion.KEY_TEMPLATE
import com.vungle.ads.internal.network.TpatSender
import com.vungle.ads.internal.network.VungleApiClient
import com.vungle.ads.internal.omsdk.OMInjector
import com.vungle.ads.internal.protos.Sdk
import com.vungle.ads.internal.ui.HackMraid
import com.vungle.ads.internal.util.FileUtility
import com.vungle.ads.internal.util.PathProvider
import com.vungle.ads.internal.util.UnzipUtility
import java.io.*
import java.util.*
import java.util.concurrent.atomic.AtomicLong


abstract class BaseAdLoader(
    val context: Context,
    val vungleApiClient: VungleApiClient,
    val sdkExecutors: Executors,
    private val omInjector: OMInjector,
    private val downloader: Downloader,
    val pathProvider: PathProvider,
    val adRequest: AdRequest
) {

    companion object {
        private const val TAG = "BaseAdLoader"
        private const val DOWNLOADED_FILE_NOT_FOUND = "Downloaded file not found!"
    }

    private val downloadCount: AtomicLong = AtomicLong(0)

    private var adLoaderCallback: AdLoaderCallback? = null

    private var notifySuccess: Boolean = false

    private val adLoadOptimizationEnabled: Boolean = ConfigManager.adLoadOptimizationEnabled()

    private val adAssets: MutableList<AdAsset> = mutableListOf()

    internal var advertisement: AdPayload? = null
    private val errors =
        Collections.synchronizedList(ArrayList<AssetDownloadListener.DownloadError>())

    private var mainVideoSizeMetric =
        SingleValueMetric(Sdk.SDKMetric.SDKMetricType.ASSET_FILE_SIZE)

    private var templateSizeMetric =
        SingleValueMetric(Sdk.SDKMetric.SDKMetricType.TEMPLATE_ZIP_SIZE)

    private var assetDownloadDurationMetric =
        TimeIntervalMetric(Sdk.SDKMetric.SDKMetricType.ASSET_DOWNLOAD_DURATION_MS)

    // check if all the assets downloaded successfully.
    private val assetDownloadListener: AssetDownloadListener
        get() {
            return object : AssetDownloadListener {
                override fun onError(
                    error: AssetDownloadListener.DownloadError?,
                    downloadRequest: DownloadRequest?
                ) {
                    Log.d(TAG, "onError called! ${error?.reason}")
                    sdkExecutors.backgroundExecutor.execute {
                        if (downloadRequest != null) {
                            val id = downloadRequest.cookieString
                            var adAsset: AdAsset? = null
                            for (tmpAdAsset: AdAsset in adAssets) {
                                if (TextUtils.equals(tmpAdAsset.identifier, id)) {
                                    adAsset = tmpAdAsset
                                }
                            }
                            if (adAsset != null) {
                                errors.add(error)
                            } else {
                                errors.add(
                                    AssetDownloadListener.DownloadError(
                                        DEFAULT_SERVER_CODE,
                                        IOException(DOWNLOADED_FILE_NOT_FOUND),
                                        REQUEST_ERROR
                                    )
                                )
                            }
                        } else {
                            errors.add(
                                AssetDownloadListener.DownloadError(
                                    DEFAULT_SERVER_CODE,
                                    RuntimeException("error in request"),
                                    INTERNAL_ERROR
                                )
                            )
                        }
                        if (downloadCount.decrementAndGet() <= 0) {
                            onAdLoadFailed(AssetDownloadError())
                        }
                    }
                }

                override fun onProgress(
                    progress: AssetDownloadListener.Progress,
                    downloadRequest: DownloadRequest
                ) {
                    Log.d(
                        TAG,
                        "progress: ${progress.progressPercent}, download url: ${downloadRequest.url}"
                    )
                }

                override fun onSuccess(file: File, downloadRequest: DownloadRequest) {
                    sdkExecutors.backgroundExecutor.execute {
                        if (!file.exists()) {
                            onError(
                                AssetDownloadListener.DownloadError(
                                    DEFAULT_SERVER_CODE,
                                    IOException(DOWNLOADED_FILE_NOT_FOUND),
                                    AssetDownloadListener.DownloadError.ErrorReason.FILE_NOT_FOUND_ERROR
                                ),
                                downloadRequest
                            ) //AdAsset table will be updated in onError callback
                            return@execute
                        }
                        if (downloadRequest.isTemplate) {
                            downloadRequest.stopRecord()
                            templateSizeMetric.value = file.length()
                            AnalyticsClient.logMetric(
                                templateSizeMetric,
                                placementId = adRequest.placement.referenceId,
                                creativeId = advertisement?.getCreativeId(),
                                eventId = advertisement?.eventId(),
                                metaData = downloadRequest.url
                            )
                        } else if (downloadRequest.isMainVideo) {
                            mainVideoSizeMetric.value = file.length()
                            AnalyticsClient.logMetric(
                                mainVideoSizeMetric,
                                placementId = adRequest.placement.referenceId,
                                creativeId = advertisement?.getCreativeId(),
                                eventId = advertisement?.eventId(),
                                metaData = downloadRequest.url
                            )
                        }

                        val id = downloadRequest.cookieString
                        var adAsset: AdAsset? = null
                        for (tmpAdAsset: AdAsset in adAssets) {
                            if (TextUtils.equals(tmpAdAsset.identifier, id)) {
                                adAsset = tmpAdAsset
                                break
                            }
                        }

                        if (adAsset == null) {
                            onError(
                                AssetDownloadListener.DownloadError(
                                    DEFAULT_SERVER_CODE,
                                    IOException(DOWNLOADED_FILE_NOT_FOUND),
                                    REQUEST_ERROR
                                ),
                                downloadRequest
                            ) //AdAsset table will be updated in onError callback
                            return@execute
                        }

                        adAsset?.let {
                            it.fileType =
                                if (isZip(file)) AdAsset.FileType.ZIP else AdAsset.FileType.ASSET
                            it.fileSize = file.length()
                            it.status = AdAsset.Status.DOWNLOAD_SUCCESS

                            if (isZip(file)) {
                                // Inject OMSDK
                                injectOMIfNeeded(advertisement)

                                // onAdLoaded callback will be triggered when template is downloaded.
                                if (!processTemplate(it, advertisement)) {
                                    errors.add(
                                        AssetDownloadListener.DownloadError(
                                            -1,
                                            AssetDownloadError(),
                                            INTERNAL_ERROR
                                        )
                                    )
                                }
                            }
                        }

                        // check if all the assets downloaded successfully.
                        if (downloadCount.decrementAndGet() <= 0) {
                            // set advertisement state to READY
                            if (errors.isEmpty()) {
                                onDownloadCompleted(adRequest, advertisement?.eventId())
                            } else {
                                onAdLoadFailed(AssetDownloadError())
                            }

                        }
                    }
                }
            }
        }

    fun loadAd(adLoaderCallback: AdLoaderCallback) {
        this.adLoaderCallback = adLoaderCallback

        sdkExecutors.backgroundExecutor.execute {
            MraidJsLoader.downloadJs(pathProvider, downloader) {
                if (it == MraidJsLoader.MRAID_AVAILABLE || it == MraidJsLoader.MRAID_DOWNLOADED) {
                    if (it == MraidJsLoader.MRAID_DOWNLOADED) {
                        AnalyticsClient.logMetric(
                            Sdk.SDKMetric.SDKMetricType.MRAID_DOWNLOAD_JS_RETRY_SUCCESS,
                            placementId = adRequest.placement.referenceId
                        )
                    }
                    requestAd()
                } else {
                    adLoaderCallback.onFailure(MraidJsError())
                }
            }
        }
    }

    protected abstract fun requestAd()

    abstract fun onAdLoadReady()

    fun cancel() {
        downloader.cancelAll()
    }

    private fun downloadAssets(advertisement: AdPayload) {
        assetDownloadDurationMetric.markStart()
        downloadCount.set(adAssets.size.toLong())
        for (asset in adAssets) {
            val downloadRequest =
                DownloadRequest(
                    getAssetPriority(asset),
                    asset.serverPath,
                    asset.localPath,
                    asset.identifier,
                    isTemplateUrl(asset),
                    isMainVideo(asset),
                    adRequest.placement.referenceId,
                    advertisement.getCreativeId(),
                    advertisement.eventId()
                )
            if (downloadRequest.isTemplate) {
                downloadRequest.startRecord()
            }
            downloader.download(downloadRequest, assetDownloadListener)
        }
    }

    fun onAdLoadFailed(error: VungleError) {
        adLoaderCallback?.onFailure(error)
    }

    private fun onAdReady() {
        advertisement?.let {
            val destinationDir = getDestinationDir(it)
            destinationDir?.let { assetDir ->
                val downloadedFile = mutableListOf<String>()
                adAssets.forEach { asset ->
                    if (asset.status == AdAsset.Status.DOWNLOAD_SUCCESS) {
                        asset.localPath?.let { localFile ->
                            downloadedFile.add(localFile)
                        }
                    }
                }
                it.setMraidAssetDir(assetDir, downloadedFile)
            }
            // onSuccess can only be called once. For ADO case, it will be called right after
            // template is downloaded. After all assets are downloaded, onSuccess will not be called.
            if (!notifySuccess) {
                // After real time ad loaded, will send win notifications.
                // Use this abstract method to notify sub ad loader doing some clean up job.
                onAdLoadReady()
                adLoaderCallback?.onSuccess(it)
                notifySuccess = true
            }
        }
    }

    private fun isUrlValid(url: String?): Boolean {
        return !url.isNullOrEmpty() && (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url))
    }

    private fun fileIsValid(file: File, adAsset: AdAsset): Boolean {
        return file.exists() && file.length() == adAsset.fileSize
    }

    open fun isZip(downloadedFile: File): Boolean {
        return downloadedFile.name == KEY_TEMPLATE
    }

    private fun isTemplateUrl(adAsset: AdAsset): Boolean {
        return (adAsset.fileType == AdAsset.FileType.ZIP)
    }

    private fun isMainVideo(asset: AdAsset): Boolean {
        return advertisement?.getMainVideoUrl() == asset.serverPath
    }

    private fun getAsset(
        advertisement: AdPayload,
        destinationDir: File,
        key: String,
        url: String?
    ): AdAsset? {
        val path = destinationDir.path + File.separator + key
        val type =
            if (path.endsWith(KEY_TEMPLATE)) AdAsset.FileType.ZIP else AdAsset.FileType.ASSET
        val eventId = advertisement.eventId()
        if (eventId.isNullOrEmpty()) {
            return null
        }

        val adAsset = AdAsset(eventId, url, path)
        adAsset.status = AdAsset.Status.NEW
        adAsset.fileType = type
        return adAsset
    }

    private fun unzipFile(
        advertisement: AdPayload,
        downloadedFile: File
    ): Boolean {
        val existingPaths: MutableList<String> = ArrayList()
        for (asset in adAssets) {
            if (asset.fileType == AdAsset.FileType.ASSET && asset.localPath != null) {
                existingPaths.add(asset.localPath)
            }
        }
        val destinationDir = getDestinationDir(advertisement)
        if (destinationDir == null || !destinationDir.isDirectory) {
            throw IOException("Unable to access Destination Directory")
        }
        try {
            UnzipUtility.unzip(downloadedFile.path, destinationDir.path,
                object : UnzipUtility.Filter {
                    override fun matches(extractPath: String?): Boolean {
                        if (extractPath.isNullOrEmpty()) {
                            return true
                        }
                        val toExtract = File(extractPath)
                        for (existing in existingPaths) {
                            val existingFile = File(existing)
                            if (existingFile == toExtract)
                                return false
                            if (toExtract.path.startsWith(existingFile.path + File.separator))
                                return false
                        }
                        return true
                    }
                })

            val file = File(destinationDir.path + File.separator + "index.html")
            if (!file.exists()) {
                AnalyticsClient.logError(
                    VungleError.INVALID_INDEX_URL,
                    "Failed to retrieve indexFileUrl from the Ad.",
                    adRequest.placement.referenceId,
                    advertisement.getCreativeId(),
                    advertisement.eventId(),
                )
                return false
            }

        } catch (ex: Exception) {

            AnalyticsClient.logError(
                VungleError.TEMPLATE_UNZIP_ERROR,
                "Unzip failed: ${ex.message}",
                adRequest.placement.referenceId,
                advertisement.getCreativeId(),
                advertisement.eventId(),
            )
            return false
        }

        if (downloadedFile.name == KEY_TEMPLATE) {
            //  Updating mraid.js
            // Find the mraid.js file and append the MRAID code. Why this is not done by the server is
            // confusing to me but I assume there must be some historical context to it.
            val mraidJS = File(destinationDir.path + File.separator + "mraid.js")
            if (!mraidJS.exists()) {
                val created = mraidJS.createNewFile()
                if (!created) {
                    throw IOException("mraid.js can not be created")
                }
            }
            val out = PrintWriter(BufferedWriter(FileWriter(mraidJS, true)))
            HackMraid.apply(pathProvider, out)
            out.close()
        }

        FileUtility.printDirectoryTree(destinationDir)
        FileUtility.delete(downloadedFile)
        return true
    }

    private fun getDestinationDir(advertisement: AdPayload): File? {
        return pathProvider.getDownloadsDirForAd(advertisement.eventId())
    }

    private fun injectOMIfNeeded(advertisement: AdPayload?): Boolean {
        if (advertisement == null) {
            return false
        }
        if (advertisement.omEnabled()) {
            try {
                val destinationDir = getDestinationDir(advertisement)
                if (destinationDir == null || !destinationDir.isDirectory) {
                    onAdLoadFailed(AssetDownloadError())
                    return false
                }
                omInjector.injectJsFiles(destinationDir)
            } catch (e: IOException) {
                onAdLoadFailed(AssetDownloadError())
                return false
            }
        }
        return true
    }

    private fun processTemplate(
        asset: AdAsset,
        advertisement: AdPayload?
    ): Boolean {
        if (advertisement == null) {
            return false
        }
        if (asset.status != AdAsset.Status.DOWNLOAD_SUCCESS) {
            return false
        }
        if (asset.localPath.isNullOrEmpty()) {
            return false
        }
        val f = File(asset.localPath)
        if (!fileIsValid(f, asset)) {
            return false
        }
        if (asset.fileType == AdAsset.FileType.ZIP && !unzipFile(advertisement, f)) {
            return false
        }

        if (isAdLoadOptimizationEnabled(advertisement)) {
            onAdReady()
        }
        return true
    }

    private fun isAdLoadOptimizationEnabled(advertisement: AdPayload?): Boolean {
        return adLoadOptimizationEnabled && advertisement != null && advertisement.getAdType() == AdPayload.TYPE_VUNGLE_MRAID
    }

    @WorkerThread
    open fun onDownloadCompleted(request: AdRequest, advertisementId: String?) {
        Log.d(TAG, "download completed $request")
        advertisement?.setAssetFullyDownloaded()
        onAdReady()

        assetDownloadDurationMetric.markEnd()
        val placementId = advertisement?.placementId()
        val creativeId = advertisement?.getCreativeId()
        val eventId = advertisement?.eventId()
        AnalyticsClient.logMetric(assetDownloadDurationMetric, placementId, creativeId, eventId)
    }

    fun handleAdMetaData(advertisement: AdPayload) {
        this.advertisement = advertisement

        val error = validateAdMetadata(advertisement)
        if (error != null) {
            AnalyticsClient.logError(
                error.reason,
                error.description,
                adRequest.placement.referenceId,
                advertisement.getCreativeId(),
                advertisement.eventId(),
            )
            onAdLoadFailed(InternalError(error.reason, error.descriptionExternal))
            return
        }

        val entries: Set<Map.Entry<String, String>> =
            advertisement.getDownloadableUrls().entries
        val destinationDir: File? = getDestinationDir(advertisement)
        if (destinationDir == null || !destinationDir.isDirectory || entries.isEmpty()) {
            onAdLoadFailed(AssetDownloadError())
            return
        }

        // URLs for start to load ad notification when load ad after get the adm and
        // before assets start to download.
        advertisement.adUnit()?.loadAdUrls?.also { loadAdUrls ->
            val tpatSender = TpatSender(
                vungleApiClient,
                advertisement.placementId(),
                advertisement.getCreativeId(),
                advertisement.eventId(),
                sdkExecutors.ioExecutor,
                pathProvider
            )
            loadAdUrls.forEach {
                tpatSender.pingUrl(it, sdkExecutors.jobExecutor)
            }
        }

        for ((key, value) in entries) {
            val asset: AdAsset? = getAsset(advertisement, destinationDir, key, value)
            if (asset != null) {
                adAssets.add(asset)
            }
        }

        downloadAssets(advertisement)
    }

    private fun getAssetPriority(adAsset: AdAsset): DownloadRequest.Priority {
        return if (adLoadOptimizationEnabled) {
            if (!adAsset.localPath.isNullOrEmpty() && adAsset.localPath.endsWith(KEY_TEMPLATE)) {
                DownloadRequest.Priority.CRITICAL
            } else {
                DownloadRequest.Priority.HIGHEST
            }
        } else {
            DownloadRequest.Priority.CRITICAL
        }

    }

    private fun validateAdMetadata(adPayload: AdPayload): ErrorInfo? {

        var reason: Int
        var description: String
        if (adPayload.adUnit()?.sleep != null) {
            return getErrorInfo(adPayload)
        }

        if (adRequest.placement.referenceId != advertisement?.placementId()) {
            reason = VungleError.AD_RESPONSE_EMPTY
            description = "The ad response is missing placement reference id."
            return ErrorInfo(reason, description)
        }

        if (!adRequest.placement.supportedTemplateTypes.contains(advertisement?.templateType())) {
            reason = VungleError.AD_RESPONSE_INVALID_TEMPLATE_TYPE
            description = "The ad response has an unexpected template type."
            return ErrorInfo(reason, description)
        }

        val templateSettings = adPayload.adUnit()?.templateSettings
        if (templateSettings == null) {
            reason = VungleError.ASSET_RESPONSE_DATA_ERROR
            description = "Missing assets URLs"
            return ErrorInfo(reason, description)
        }
        val cacheableReplacements = templateSettings.cacheableReplacements
        if (adPayload.isNativeTemplateType()) {
            cacheableReplacements?.let {
                if (it[NativeAdInternal.TOKEN_MAIN_IMAGE]?.url == null) {
                    reason = VungleError.NATIVE_ASSET_ERROR
                    description = "Unable to load main image."
                    return ErrorInfo(reason, description)
                }
                if (it[NativeAdInternal.TOKEN_VUNGLE_PRIVACY_ICON_URL]?.url == null) {
                    reason = VungleError.NATIVE_ASSET_ERROR
                    description = "Unable to load privacy image."
                    return ErrorInfo(reason, description)
                }
            }
        } else {
            val templateUrl = adPayload.adUnit()?.templateURL
            if (templateUrl.isNullOrEmpty()) {
                reason = VungleError.INVALID_TEMPLATE_URL
                description = "Failed to prepare URL for template download."
                return ErrorInfo(reason, description)
            }

            if (!isUrlValid(templateUrl)) {
                reason = VungleError.ASSET_REQUEST_ERROR
                description = "Failed to load template asset."
                return ErrorInfo(reason, description)
            }
        }

        if (adPayload.hasExpired()) {
            reason = VungleError.AD_EXPIRED
            description = "The ad markup has expired for playback."
            return ErrorInfo(reason, description)
        }

        if (adPayload.eventId().isNullOrEmpty()) {
            reason = VungleError.INVALID_EVENT_ID_ERROR
            description = "Event id is invalid."
            return ErrorInfo(reason, description)
        }

        cacheableReplacements?.forEach {
            val httpUrl = it.value.url
            if (httpUrl.isNullOrEmpty()) {
                reason = VungleError.INVALID_ASSET_URL
                description = "Invalid asset URL $httpUrl"
                return ErrorInfo(reason, description)
            }
            if (!isUrlValid(httpUrl)) {
                reason = VungleError.ASSET_REQUEST_ERROR
                description = "Invalid asset URL $httpUrl"
                return ErrorInfo(reason, description)
            }
        }

        return null
    }

    private fun getErrorInfo(adPayload: AdPayload): ErrorInfo {

        val errorCode: Int = adPayload.adUnit()?.errorCode ?: VungleError.PLACEMENT_SLEEP
        val sleep = adPayload.adUnit()?.sleep
        val info = adPayload.adUnit()?.info
        when (errorCode) {
            Sdk.SDKError.Reason.AD_NO_FILL_VALUE,
            Sdk.SDKError.Reason.AD_LOAD_TOO_FREQUENTLY_VALUE,
            Sdk.SDKError.Reason.AD_SERVER_ERROR_VALUE,
            Sdk.SDKError.Reason.AD_PUBLISHER_MISMATCH_VALUE,
            Sdk.SDKError.Reason.AD_INTERNAL_INTEGRATION_ERROR_VALUE,
            -> {
                return ErrorInfo(
                    errorCode, "Response error: $sleep",
                    "Request failed with error: $errorCode, $info"
                )
            }

            else -> {
                return ErrorInfo(
                    VungleError.PLACEMENT_SLEEP, "Response error: $sleep",
                    "Request failed with error: ${VungleError.PLACEMENT_SLEEP}, $info"
                )
            }
        }

    }

    class ErrorInfo(
        val reason: Int,
        val description: String,
        val descriptionExternal: String = description,
        val errorIsTerminal: Boolean = false,
    )

}
