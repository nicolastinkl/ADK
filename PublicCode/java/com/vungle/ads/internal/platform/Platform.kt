package com.vungle.ads.internal.platform

import androidx.core.util.Consumer
import com.vungle.ads.internal.model.AdvertisingInfo

interface Platform {
    companion object {
        const val MANUFACTURER_AMAZON = "Amazon"
    }

    val isAtLeastMinimumSDK: Boolean
    val isBatterySaverEnabled: Boolean
    val isSideLoaded: Boolean
    val volumeLevel: Float
    val isSoundEnabled: Boolean
    val isSdCardPresent: Boolean
    val userAgent: String?
    fun getUserAgentLazy(consumer: Consumer<String?>)
    fun getAdvertisingInfo(): AdvertisingInfo?

    fun getAppSetId(): String?

    fun getAndroidId(): String?

}
