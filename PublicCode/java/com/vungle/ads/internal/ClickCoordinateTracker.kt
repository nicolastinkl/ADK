package com.vungle.ads.internal

import android.content.Context
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.WindowManager
import androidx.annotation.VisibleForTesting
import com.vungle.ads.AnalyticsClient
import com.vungle.ads.BannerAdSize
import com.vungle.ads.ServiceLocator.Companion.inject
import com.vungle.ads.VungleError
import com.vungle.ads.internal.executor.Executors
import com.vungle.ads.internal.model.AdPayload
import com.vungle.ads.internal.network.TpatSender
import com.vungle.ads.internal.network.VungleApiClient
import com.vungle.ads.internal.util.PathProvider
import com.vungle.ads.internal.util.ViewUtility
import java.util.concurrent.Executor
import java.util.regex.Pattern

class ClickCoordinateTracker(
    private val context: Context,
    private val advertisement: AdPayload,
    private val executor: Executor
) {

    private val vungleApiClient: VungleApiClient by inject(context)
    private val executors: Executors by inject(context)

    @VisibleForTesting
    internal val currentClick: ClickCoordinate = ClickCoordinate(
        Coordinate(Int.MIN_VALUE, Int.MIN_VALUE),
        Coordinate(Int.MIN_VALUE, Int.MIN_VALUE)
    )

    fun trackCoordinate(event: MotionEvent) {
        if (!advertisement.isClickCoordinatesTrackingEnabled()) {
            return
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentClick.downCoordinate = Coordinate(event.x.toInt(), event.y.toInt())
            }
            MotionEvent.ACTION_UP -> {
                currentClick.upCoordinate = Coordinate(event.x.toInt(), event.y.toInt())
                if (currentClick.ready()) {
                    sendClickCoordinates()
                }
            }
        }
    }

    private fun sendClickCoordinates() {
        val tpatUrls = advertisement.getTpatUrls(AdPayload.TPAT_CLICK_COORDINATES_URLS)
        if (tpatUrls.isNullOrEmpty()) {
            AnalyticsClient.logError(
                VungleError.EMPTY_TPAT_ERROR,
                "Empty tpat key: click_coordinate",
                placementRefId = advertisement.placementId(),
                creativeId = advertisement.getCreativeId(),
                eventId = advertisement.eventId(),
            )
            return
        }
        val reqWidth = requestedWidth
        val reqHeight = requestedHeight
        val adWidth = requestedWidth
        val adHeight = requestedHeight
        val pathProvider: PathProvider by inject(context)
        val tpatSender = TpatSender(vungleApiClient,
            advertisement.placementId(), advertisement.getCreativeId(), advertisement.eventId(),
            executors.ioExecutor, pathProvider
        )
        tpatUrls.forEach {
            val url = it.replace(MACRO_REQ_WIDTH.toRegex(), reqWidth.toString())
                .replace(MACRO_REQ_HEIGHT.toRegex(), reqHeight.toString())
                .replace(MACRO_WIDTH.toRegex(), adWidth.toString())
                .replace(MACRO_HEIGHT.toRegex(), adHeight.toString())
                .replace(
                    MACRO_DOWN_X.toRegex(), currentClick.downCoordinate.x.toString()
                )
                .replace(
                    MACRO_DOWN_Y.toRegex(), currentClick.downCoordinate.y.toString()
                )
                .replace(
                    MACRO_UP_X.toRegex(), currentClick.upCoordinate.x.toString()
                )
                .replace(
                    MACRO_UP_Y.toRegex(), currentClick.upCoordinate.y.toString()
                )
            tpatSender.sendTpat(url, executor)
        }

    }

    internal data class Coordinate(val x: Int, val y: Int)

    internal data class ClickCoordinate(
        var downCoordinate: Coordinate,
        var upCoordinate: Coordinate
    ) {
        fun ready(): Boolean {
            return downCoordinate.x != Int.MIN_VALUE && downCoordinate.y != Int.MIN_VALUE && upCoordinate.x != Int.MIN_VALUE && upCoordinate.y != Int.MIN_VALUE
        }
    }

    private val requestedWidth: Int
        get() {
            val adSize: BannerAdSize? = advertisement.adSize
            return if (adSize == null) {
                deviceWidth
            } else ViewUtility.dpToPixels(context, adSize.width)
        }

    private val requestedHeight: Int
        get() {
            val adSize: BannerAdSize? = advertisement.adSize
            return if (adSize == null) {
                deviceHeight
            } else ViewUtility.dpToPixels(context, adSize.height)
        }

    private val deviceWidth: Int
        get() = DeviceScreenInfo(context).deviceWidth

    private val deviceHeight: Int
        get() = DeviceScreenInfo(context).deviceHeight

    internal data class DeviceScreenInfo(val context: Context) {
        private val dm: DisplayMetrics = DisplayMetrics()
        val deviceWidth: Int
            get() = dm.widthPixels
        val deviceHeight: Int
            get() = dm.heightPixels

        init {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(dm)
        }
    }

    companion object {
        private const val TAG = "ClickCoordinateTracker"
        private val MACRO_REQ_WIDTH = Pattern.quote("{{{req_width}}}")
        private val MACRO_REQ_HEIGHT = Pattern.quote("{{{req_height}}}")
        private val MACRO_WIDTH = Pattern.quote("{{{width}}}")
        private val MACRO_HEIGHT = Pattern.quote("{{{height}}}")
        private val MACRO_DOWN_X = Pattern.quote("{{{down_x}}}")
        private val MACRO_DOWN_Y = Pattern.quote("{{{down_y}}}")
        private val MACRO_UP_X = Pattern.quote("{{{up_x}}}")
        private val MACRO_UP_Y = Pattern.quote("{{{up_y}}}")
    }
}
