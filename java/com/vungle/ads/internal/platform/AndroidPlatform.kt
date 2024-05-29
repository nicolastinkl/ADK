package com.vungle.ads.internal.platform

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.Log
import androidx.core.util.Consumer
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.appset.AppSet
import com.google.android.gms.appset.AppSetIdClient
import com.google.android.gms.appset.AppSetIdInfo
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.tasks.Task
import com.vungle.ads.internal.executor.VungleThreadPoolExecutor
import com.vungle.ads.internal.model.AdvertisingInfo
import com.vungle.ads.internal.platform.Platform.Companion.MANUFACTURER_AMAZON
import com.vungle.ads.internal.privacy.PrivacyManager


class AndroidPlatform(
    private val context: Context,
    private val uaExecutor: VungleThreadPoolExecutor
) : Platform {
    init {
        updateAppSetID()
    }

    companion object {
        private const val TAG = "AndroidPlatform"
    }

    private var appSetId: String? = null
    private var advertisingInfo: AdvertisingInfo? = null
    private val powerManager: PowerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    override val isAtLeastMinimumSDK: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    override val isBatterySaverEnabled: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            powerManager.isPowerSaveMode
        } else false
    override val isSideLoaded: Boolean = false // Do not retrieve canRequestPackageInstalls

    override val volumeLevel: Float
        get() = try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
            val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
            current / max
        } catch (_: Exception) {
            0f
        }
    override val isSoundEnabled: Boolean
        get() = try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            current > 0
        } catch (_: Exception) {
            true
        }
    override val isSdCardPresent: Boolean
        get() = try {
            Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        } catch (e: Exception) {
            Log.e(TAG, "Acquiring external storage state failed", e)
            false
        }

    override fun getUserAgentLazy(consumer: Consumer<String?>) {
        uaExecutor.execute {
            WebViewUtil(context).getUserAgent(consumer)
        }
    }//Generally not possible but for safer side

    override fun getAdvertisingInfo(): AdvertisingInfo {
        val cachedAdvInfo = advertisingInfo
        if (cachedAdvInfo != null && !cachedAdvInfo.advertisingId.isNullOrEmpty()) {
            return cachedAdvInfo
        }
        val advertisingInfo = AdvertisingInfo()
        try {
            val idInfo: AdvertisingIdClient.Info
            if (MANUFACTURER_AMAZON == Build.MANUFACTURER) {
                try {
                    val cr = context.contentResolver
                    // load user's tracking preference
                    advertisingInfo.limitAdTracking =
                        Settings.Secure.getInt(cr, "limit_ad_tracking") == 1
                    // load advertising
                    advertisingInfo.advertisingId = Settings.Secure.getString(cr, "advertising_id")
                } catch (ex: SettingNotFoundException) {
                    // not supported
                    Log.w(TAG, "Error getting Amazon advertising info", ex)
                }
            } else {
                try {
                    idInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
                    if (idInfo != null) {
                        advertisingInfo.advertisingId = idInfo.id
                        advertisingInfo.limitAdTracking = idInfo.isLimitAdTrackingEnabled
                    } else {
                        /// Advertising ID was not available through the Google Play Services. Panic!
                        //                    advertisingID = "PANIC!";
                    }
                } catch (ex: NoClassDefFoundError) {
                    Log.e(TAG, "Play services Not available: " + ex.localizedMessage)
                    val cr = context.contentResolver
                    advertisingInfo.advertisingId = Settings.Secure.getString(cr, "advertising_id")
                } catch (exception: GooglePlayServicesNotAvailableException) {
                    Log.e(TAG, "Play services Not available: " + exception.localizedMessage)
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Cannot load Advertising ID")
        }

        this.advertisingInfo = advertisingInfo
        return advertisingInfo
    }

    override fun getAppSetId(): String? {
        return appSetId
    }

    @SuppressLint("HardwareIds")
    override fun getAndroidId(): String? {
        return if (PrivacyManager.getPublishAndroidId()) {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } else {
            ""
        }
    }

    // AppSetIdInfo#getId might potentially return null causing a NPE. Possibly an issue with
    // Google's implementation.
    private fun updateAppSetID() {
        //query value for AppSetId
        try {
            val client: AppSetIdClient = AppSet.getClient(context)
            val task: Task<AppSetIdInfo> = client.appSetIdInfo
            task.addOnSuccessListener { info ->
                if (info != null) {
                    // Read app set ID value, which uses version 4 of the
                    // universally unique identifier (UUID) format.
                    appSetId = info.id
                }
            }
        } catch (ex: NoClassDefFoundError) {
            Log.e(TAG, "Required libs to get AppSetID Not available: " + ex.localizedMessage)
        }
    }

    //Very first time or after clearing app data
    override var userAgent: String? = null
        get() {
            return field ?: System.getProperty("http.agent")
        }

}