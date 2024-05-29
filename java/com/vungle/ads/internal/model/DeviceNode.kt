package com.vungle.ads.internal.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceNode(
    val make: String,
    val model: String,
    val osv: String,
    val carrier: String? = null,
    val os: String,
    val w: Int,
    val h: Int,
    var ua: String? = null,
    var ifa: String? = null,
    var lmt: Int? = null,
    var ext: DeviceExt? = null
) {

    @Serializable
    data class DeviceExt(val vungle: VungleExt)

    @Serializable
    data class VungleExt(
        val android: AndroidAmazonExt? = null,
        val amazon: AndroidAmazonExt? = null
    )

    @Serializable
    open class CommonVungleExt {
        @SerialName("android_id")
        var androidId: String? = null

        @SerialName("is_google_play_services_available")
        var isGooglePlayServicesAvailable: Boolean = false

        @SerialName("app_set_id")
        var appSetId: String? = null

        @SerialName("battery_level")
        var batteryLevel: Float = 0.0f

        @SerialName("battery_state")
        var batteryState: String? = null

        @SerialName("battery_saver_enabled")
        var batterySaverEnabled: Int = 0

        @SerialName("connection_type")
        var connectionType: String? = null

        @SerialName("connection_type_detail")
        var connectionTypeDetail: String? = null
        var locale: String? = null
        var language: String? = null

        @SerialName("time_zone")
        var timeZone: String? = null

        @SerialName("volume_level")
        var volumeLevel: Float = 0f

        @SerialName("sound_enabled")
        var soundEnabled: Int = 1

        @SerialName("is_tv")
        var isTv: Boolean = false

        @SerialName("sd_card_available")
        var sdCardAvailable: Int = 1

        @SerialName("is_sideload_enabled")
        var isSideloadEnabled: Boolean = false

        @SerialName("os_name")
        var osName: String? = null
    }

    @Serializable
    data class AndroidAmazonExt(var gaid: String? = null, var amazonAdvertisingId: String? = null) :
        CommonVungleExt()
}
