package com.vungle.ads.internal.downloader

interface Downloader {
    fun download(downloadRequest: DownloadRequest?, downloadListener: AssetDownloadListener?)
    fun cancel(request: DownloadRequest?)
    fun cancelAll()

    annotation class NetworkType {
        companion object {
            var CELLULAR = 1
            var WIFI = 1 shl 1
            var ANY = CELLULAR or WIFI
        }
    }

    class RequestException internal constructor(message: String?) : Exception(message)
}