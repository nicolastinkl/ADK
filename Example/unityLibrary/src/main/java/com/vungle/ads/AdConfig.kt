package com.vungle.ads

import androidx.annotation.IntDef

open class AdConfig {

    private var extras = mutableMapOf<String, String>()

    private var settings: Int = 0

    @Orientation
    var adOrientation = AUTO_ROTATE

    @IntDef(PORTRAIT, LANDSCAPE, AUTO_ROTATE)
    annotation class Orientation

    companion object {
        const val PORTRAIT = 0
        const val LANDSCAPE = 1
        const val AUTO_ROTATE = 2

        const val IMMEDIATE_BACK = 1 shl 1

        private const val WATERMARK = "WATERMARK"
    }

    @IntDef(IMMEDIATE_BACK)
    annotation class Settings

    /**
     *
     * Sets whether the Android back button will be immediately enabled during the
     * video ad, or it will be inactive until the on screen close button appears
     * *(the default)*.
     *
     *
     *
     * Once enabled, the Android back button allows the user to skip the video
     * ad and proceed to the post-roll if one exists; if the ad does not have a
     * post-roll the ad simply ends.
     *
     * @param isBackButtonImmediatelyEnabled `true` if back button should
     * be enabled before ad close button appears; otherwise, `false`
     */
    fun setBackButtonImmediatelyEnabled(isBackButtonImmediatelyEnabled: Boolean) {
        settings = if (isBackButtonImmediatelyEnabled) {
            settings or IMMEDIATE_BACK
        } else {
            settings and IMMEDIATE_BACK.inv()
        }
    }

    fun getSettings(): Int {
        return settings
    }

    fun setWatermark(watermark: String) {
        this.extras[WATERMARK] = watermark
    }

    internal fun getWatermark(): String? {
        return this.extras[WATERMARK]
    }
}
