package com.vungle.ads.internal.downloader

import java.io.File

interface AssetDownloadListener {

    fun onError(error: DownloadError?, downloadRequest: DownloadRequest?)

    fun onProgress(progress: Progress, downloadRequest: DownloadRequest)

    fun onSuccess(file: File, downloadRequest: DownloadRequest)

    class DownloadError(
        val serverCode: Int,
        val cause: Throwable,
        @field:ErrorReason val reason: Int
    ) {
        annotation class ErrorReason {
            companion object {
                var CONNECTION_ERROR = 0
                var REQUEST_ERROR = 1
                var DISK_ERROR = 2
                var FILE_NOT_FOUND_ERROR = 3
                var INTERNAL_ERROR = 4
            }
        }

        companion object {
            const val DEFAULT_SERVER_CODE = -1
        }
    }

    class Progress {
        @ProgressStatus
        var status = 0
        var progressPercent = 0
        var timestampDownloadStart: Long = 0
        var startBytes: Long = 0
        var sizeBytes: Long = 0

        annotation class ProgressStatus {
            companion object {
                var STARTED = 0
                var IN_PROGRESS = 1
                var PAUSED = 2
                var CANCELLED = 3
                var DONE = 4
                var LOST_CONNECTION = 5
                var STATE_CHANGED = 6
                var ERROR = 7
            }
        }

        companion object {
            fun copy(progress: Progress): Progress {
                val copy = Progress()
                copy.status = progress.status
                copy.progressPercent = progress.progressPercent
                copy.timestampDownloadStart = progress.timestampDownloadStart
                copy.sizeBytes = progress.sizeBytes
                copy.startBytes = progress.startBytes
                return copy
            }
        }
    }
}