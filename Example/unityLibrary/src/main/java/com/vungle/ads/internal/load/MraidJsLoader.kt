package com.vungle.ads.internal.load

import android.util.Log
import com.vungle.ads.AnalyticsClient
import com.vungle.ads.MraidJsError
import com.vungle.ads.VungleError
import com.vungle.ads.internal.ConfigManager
import com.vungle.ads.internal.Constants
import com.vungle.ads.internal.downloader.AssetDownloadListener
import com.vungle.ads.internal.downloader.DownloadRequest
import com.vungle.ads.internal.downloader.Downloader
import com.vungle.ads.internal.util.FileUtility
import com.vungle.ads.internal.util.PathProvider
import java.io.File

object MraidJsLoader {

    private const val TAG = "MraidJsLoader"

    const val MRAID_DOWNLOADED = 10
    const val MRAID_INVALID_ENDPOINT = 11
    const val MRAID_DOWNLOAD_FAILED = 12
    const val MRAID_AVAILABLE = 13

    fun downloadJs(
        pathProvider: PathProvider,
        downloader: Downloader,
        downloadListener: (downloadState: Int) -> Unit
    ) {
        val mraidEndpoint = ConfigManager.getMraidEndpoint()
        if (mraidEndpoint.isNullOrEmpty()) {
            downloadListener(MRAID_INVALID_ENDPOINT)
            return
        }

        val mraidJsPath = pathProvider.getJsAssetDir(ConfigManager.getMraidJsVersion())
        val mraidJsFile = File(mraidJsPath, Constants.MRAID_JS_FILE_NAME)
        if (mraidJsFile.exists()) {
            downloadListener(MRAID_AVAILABLE)
            return
        }

        // delete invalid js first
        val jsPath = pathProvider.getJsDir()
        FileUtility.deleteContents(jsPath)

        // download latest js
        val mraidDownloadRequest =
            DownloadRequest(
                DownloadRequest.Priority.HIGH,
                mraidEndpoint + "/" + Constants.MRAID_JS_FILE_NAME,
                mraidJsFile.absolutePath,
                cookieString = null,
                isTemplate = false,
                isMainVideo = false
            )
        downloader.download(mraidDownloadRequest, object : AssetDownloadListener {
            override fun onError(
                error: AssetDownloadListener.DownloadError?,
                downloadRequest: DownloadRequest?
            ) {
                val errorMessage = "download mraid js error: ${error?.serverCode}:${error?.cause}"
                Log.d(TAG, errorMessage)
                MraidJsError(errorMessage).logErrorNoReturnValue()

                FileUtility.deleteContents(jsPath)
                downloadListener(MRAID_DOWNLOAD_FAILED)
            }

            override fun onProgress(
                progress: AssetDownloadListener.Progress,
                downloadRequest: DownloadRequest
            ) {
                //no-op
            }

            override fun onSuccess(file: File, downloadRequest: DownloadRequest) {
                if (mraidJsFile.exists() && mraidJsFile.length() > 0) {
                    downloadListener(MRAID_DOWNLOADED)
                } else {
                    AnalyticsClient.logError(
                        VungleError.MRAID_JS_WRITE_FAILED,
                        "Mraid js downloaded but write failure: ${mraidJsFile.absolutePath}"
                    )

                    FileUtility.deleteContents(jsPath)
                    downloadListener(MRAID_DOWNLOAD_FAILED)
                }
            }
        })
    }
}
