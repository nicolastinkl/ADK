package com.vungle.ads.internal.util

import android.content.Context
import android.os.Build
import android.os.StatFs
import android.util.Log
import java.io.File

class PathProvider(val context: Context) {
    /** root directory for Vungle */
    private val vungleDir: File = File(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.noBackupFilesDir
        } else {
            context.filesDir
        }, VUNGLE_FOLDER
    )

    private val downloadsDir: File = File(
        vungleDir,
        DOWNLOADS_FOLDER
    )
    private val jsDir: File = File(
        vungleDir,
        JS_FOLDER
    )
    private val cleverCacheDir: File = File(
        vungleDir,
        CLEVER_CACHE_FOLDER
    )

    companion object {
        private const val VUNGLE_FOLDER = "vungle_cache"
        private const val DOWNLOADS_FOLDER = "downloads"
        private const val JS_FOLDER = "js"
        private const val CLEVER_CACHE_FOLDER = "clever_cache"
        private const val UNKNOWN_SIZE = -1L

    }

    init {
        listOf(vungleDir, downloadsDir, jsDir, cleverCacheDir).forEach {
            if (!it.exists()) {
                it.mkdirs()
            }
        }
    }

    /** @return root directory for vungle data */
    fun getVungleDir(): File {
        if (!vungleDir.exists()) {
            vungleDir.mkdirs()
        }
        return vungleDir
    }

    /** @return root directory for clever cache */
    fun getCleverCacheDir(): File {
        if (!cleverCacheDir.exists()) {
            cleverCacheDir.mkdirs()
        }
        return cleverCacheDir
    }

    /** @return root directory for js assets */
    fun getJsDir(): File {
        if (!jsDir.exists()) {
            jsDir.mkdirs()
        }
        return jsDir
    }

    /** @return root directory for ad assets */
    fun getDownloadDir(): File {
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        return downloadsDir
    }

    /** @return directory for assets of specific js */
    fun getJsAssetDir(jsVersion: String): File {
        val jsAssetDir = File(getJsDir(), jsVersion)
        if (!jsAssetDir.exists()) {
            jsAssetDir.mkdirs()
        }
        return jsAssetDir
    }

    /** @return directory for assets of specific ad(determined by adId(eventId)) */
    fun getDownloadsDirForAd(adId: String?): File? {
        if (adId.isNullOrEmpty()) {
            return null
        }
        val downloadDirForAd = getDownloadDir()
        val adDir = File(downloadDirForAd.path + File.separator + adId)
        if (!adDir.exists()) {
            adDir.mkdirs()
        }
        return adDir
    }

    fun getSharedPrefsDir(): File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        context.noBackupFilesDir
    } else {
        context.filesDir
    }

    fun getAvailableBytes(path: String): Long {
        var bytesAvailable: Long = UNKNOWN_SIZE
        val stats: StatFs?
        try {
            stats = StatFs(path)

            bytesAvailable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                stats.availableBytes
            } else {
                stats.availableBlocks * stats.blockSize.toLong()
            }
        } catch (e: IllegalArgumentException) {
            Log.w("PathProvider", "Failed to get available bytes ${e.message}")
        }

        return bytesAvailable
    }
}
