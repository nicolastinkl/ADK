package com.vungle.ads.internal.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Base64
import android.widget.ImageView

open class WatermarkView(context: Context, watermark: String): ImageView(context) {
    init {
        // Render the overlay string on the ad. In this example, an ImageView is used to simulate the ad view.
        val overlayBytes = Base64.decode(watermark, Base64.DEFAULT)
        val overlayBm = BitmapFactory.decodeByteArray(overlayBytes, 0, overlayBytes.count())

        // Optional: Check opacity of pixels in bitmap.
        val overlayRepeat = BitmapDrawable(context.resources, overlayBm)
        overlayRepeat.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        val displayMetrics = context.resources.displayMetrics
        overlayRepeat.setTargetDensity(displayMetrics)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.background = overlayRepeat
        } else {
            this.setBackgroundDrawable(overlayRepeat)
        }
        this.isClickable = false
        this.isFocusable = false
    }
}
