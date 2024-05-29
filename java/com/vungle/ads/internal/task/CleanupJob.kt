package com.vungle.ads.internal.task

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.vungle.ads.BuildConfig
import com.vungle.ads.ServiceLocator
import com.vungle.ads.internal.persistence.FilePreferences
import com.vungle.ads.internal.util.FileUtility.delete
import com.vungle.ads.internal.util.FileUtility.deleteContents
import com.vungle.ads.internal.util.FileUtility.printDirectoryTree
import com.vungle.ads.internal.util.PathProvider
import java.io.File
import java.io.IOException

private const val VERSION_CODE_KEY = "VERSION_CODE"

private const val NO_VALUE = -1

/**
 * Job that will remove any stale or expired data.
 */
class CleanupJob
internal constructor(val context: Context, val pathProvider: PathProvider) : Job {

    override fun onRunJob(bundle: Bundle, jobRunner: JobRunner): Int {
        val downloadsDir = pathProvider.getDownloadDir()
        var dirToDelete = downloadsDir
        bundle.getString(AD_ID_KEY)?.also { adId ->
            pathProvider.getDownloadsDirForAd(adId)?.also {
                dirToDelete = it
            }
        }
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "CleanupJob: Current directory snapshot")
            printDirectoryTree(dirToDelete)
        }
        try {
            if (dirToDelete == downloadsDir) {
                checkIfSdkUpgraded()
                deleteContents(dirToDelete)
            } else {
                delete(dirToDelete)
            }
        } catch (e: IOException) {
            return Job.Result.FAILURE
        }
        return Job.Result.SUCCESS
    }

    private fun dropV6Data() {
        Log.d(TAG, "CleanupJob: drop old files data")

        // DB
        var oldDb: File? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            oldDb = File(context.noBackupFilesDir, "vungle_db")
        }
        if (oldDb != null && oldDb.exists()) {
            delete(oldDb)
            delete(File(oldDb.path + "-journal"))
        } else {
            context.deleteDatabase("vungle_db")
        }

        // SP
        val sp = context.getSharedPreferences("com.vungle.sdk", Context.MODE_PRIVATE)
        val vungleCachePath = sp.getString("cache_path", null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.deleteSharedPreferences("com.vungle.sdk")
        } else {
            sp.edit().clear().apply()
        }

        // vungleSetting
        val dir: File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.noBackupFilesDir
        } else {
            context.filesDir
        }
        val vungleSettings = File(dir, "vungle_settings")
        delete(vungleSettings)

        // vungle_cache
        vungleCachePath?.let {
            delete(File(it))
        }
    }

    private fun dropV700Data() {
        val vungleDirV700 = File(
            context.applicationInfo.dataDir,
            "vungle"
        )
        delete(vungleDirV700)
    }

    private fun checkIfSdkUpgraded() {
        val filePreferences: FilePreferences by ServiceLocator.inject(context)
        val lastInstalledVersion = filePreferences.getInt(VERSION_CODE_KEY, NO_VALUE)
        if (lastInstalledVersion < BuildConfig.VERSION_CODE) {
            if (lastInstalledVersion < 70000) {
                dropV6Data()
            }
            if (lastInstalledVersion < 70100) {
                dropV700Data()
            }
            filePreferences.put(VERSION_CODE_KEY, BuildConfig.VERSION_CODE).apply()
        }
    }

    companion object {
        const val TAG: String = "CleanupJob"
        private const val AD_ID_KEY = "AD_ID_KEY"

        fun makeJobInfo(adId: String? = null): JobInfo {
            return JobInfo(TAG)
                .setPriority(JobInfo.Priority.LOWEST)
                .setExtras(Bundle().apply {
                    adId?.let { putString(AD_ID_KEY, it) }
                })
                .setUpdateCurrent(adId == null) /*only update "erase everything" type of job*/
        }
    }
}
