package com.vungle.ads.internal.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.VisibleForTesting
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.vungle.ads.*
import com.vungle.ads.internal.ConfigManager
import com.vungle.ads.internal.executor.Executors
import com.vungle.ads.internal.model.AdPayload
import com.vungle.ads.internal.model.BidPayload
import com.vungle.ads.internal.model.Placement
import com.vungle.ads.internal.omsdk.OMTracker
import com.vungle.ads.internal.presenter.AdEventListener
import com.vungle.ads.internal.presenter.MRAIDPresenter
import com.vungle.ads.internal.presenter.PresenterDelegate
import com.vungle.ads.internal.ui.view.MRAIDAdWidget
import java.util.concurrent.ExecutorService

abstract class AdActivity : Activity() {

    @VisibleForTesting internal var placementRefId: String = ""
    @VisibleForTesting internal var eventRefId: String = ""

    @VisibleForTesting internal var mraidPresenter: MRAIDPresenter? = null

    @VisibleForTesting internal var mraidAdWidget: MRAIDAdWidget? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )

        placementRefId = getPlacement(intent).let { it.toString() }
        eventRefId = getEventId(intent).let { it.toString() }
        var adv = advertisement
        var placement = ConfigManager.getPlacement(placementRefId)
        if (placement == null || adv == null) {
            eventListener?.onError(
                AdNotLoadedCantPlay(),
                placementRefId
            )
            placement = Placement(placementRefId,placementRefId);
            placement.adPosision = eventRefId
            adv = AdPayload(null)

//            finish()
//            return
        }

        window.decorView.setBackgroundColor(Color.BLACK)

        val mraidAdWidget: MRAIDAdWidget?
        try {
            mraidAdWidget = MRAIDAdWidget(this)
        } catch (ex: InstantiationException) {
            eventListener?.onError(
                AdCantPlayWithoutWebView().apply {
                    setPlacementId(placementRefId)
                    setEventId(advertisement?.eventId())
                    setCreativeId(advertisement?.getCreativeId())
                }.logError(),
                placementRefId
            )
            finish()
            return
        }

        mraidAdWidget.run {
            setCloseDelegate(object : MRAIDAdWidget.CloseDelegate {
                override fun close() {
                   // finish()
                }
            })

            setOnViewTouchListener(object : MRAIDAdWidget.OnViewTouchListener {
                override fun onTouch(event: MotionEvent?): Boolean {
                    mraidPresenter?.onViewTouched(event)
                    return false
                }
            })

            setOrientationDelegate(object : MRAIDAdWidget.OrientationDelegate {
                override fun setOrientation(orientation: Int) {
                    requestedOrientation = orientation
                }
            })
        }

        val executors = ServiceLocator.getInstance(this).getService(Executors::class.java)
        val offloadExecutor: ExecutorService = executors.offloadExecutor
        val webClient = VungleWebClient(adv, placement, offloadExecutor)
        val omTrackerFactory = ServiceLocator.getInstance(this).getService(OMTracker.Factory::class.java)
        val omTracker = omTrackerFactory.make(ConfigManager.omEnabled() && adv.omEnabled())
        val jobExecutor: ExecutorService = executors.jobExecutor

        // Add webview observer for OMSDK
        webClient.setWebViewObserver(omTracker)

        val mraidPresenter =
            MRAIDPresenter(mraidAdWidget, adv, placement, webClient, jobExecutor, omTracker, bidPayload)
        mraidPresenter.setEventListener(eventListener)
        mraidPresenter.setPresenterDelegate(presenterDelegate)

        mraidPresenter.prepare()

        setContentView(mraidAdWidget, mraidAdWidget.layoutParams)
        //eventRefId
        mraidAdWidget.showWebsite(placementRefId)

        adv.adConfig?.getWatermark()?.let {
            val imageView = WatermarkView(this, it)
            window.decorView.findViewById<FrameLayout>(android.R.id.content).addView(imageView)
            imageView.bringToFront()
        }

        this.mraidAdWidget = mraidAdWidget
        this.mraidPresenter = mraidPresenter
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val oldPlacement = getPlacement(getIntent())
        val newPlacement = getPlacement(intent)

        val oldEventId = getEventId(getIntent())
        val newEventId = getEventId(intent)

        // Check playing fullscreen ads with different placement or different advertisement that already display.
        if (oldPlacement != null && newPlacement != null && oldPlacement != newPlacement ||
            oldEventId != null && newEventId != null && oldEventId != newEventId
        ) {
            Log.d(
                TAG,
                "Tried to play another placement $newPlacement while playing $oldPlacement"
            )
            onConcurrentPlaybackError(newPlacement)
        }
    }

    private fun onConcurrentPlaybackError(placement: String?) {
        val exception = ConcurrentPlaybackUnsupported()
        eventListener?.onError(exception, placement)
        with(exception) {
            placementId = placementRefId
            creativeId = advertisement?.getCreativeId()
            eventId = advertisement?.eventId()
            logErrorNoReturnValue()
        }
        Log.e(TAG, "onConcurrentPlaybackError: ${exception.localizedMessage}")
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        mraidPresenter?.start()
    }

    override fun onPause() {
        super.onPause()
        mraidPresenter?.stop()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(TAG, "landscape")
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(TAG, "portrait")
        }
        mraidPresenter?.onViewConfigurationChanged()
    }
//
//    override fun onBackPressed() {
//        mraidPresenter?.handleExit()
//    }


    private var mExitTime: Long = 0
    override fun onBackPressed() {
        //super.onBackPressed();
        mraidPresenter?.handleExit()
        this.mraidAdWidget?.goback()
        exit()
    }

    open fun exit() {
        if (System.currentTimeMillis() - mExitTime > 500) {
            // Toast.makeText(this, "Please press back again to exit.", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis()
        } else {
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(homeIntent)
        }
    }

    override fun setRequestedOrientation(requestedOrientation: Int) {
        if (canRotate()) {
            super.setRequestedOrientation(requestedOrientation)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    internal open fun canRotate(): Boolean {
        return false
    }

    override fun onDestroy() {
        mraidPresenter?.detach(
            (if (isChangingConfigurations) MRAIDAdWidget.AdStopReason.IS_CHANGING_CONFIGURATION else 0)
                    or MRAIDAdWidget.AdStopReason.IS_AD_FINISHING
        )
        super.onDestroy()
    }

    private fun hideSystemUi() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    companion object {
        @VisibleForTesting internal const val REQUEST_KEY_EXTRA = "request"
        @VisibleForTesting internal const val REQUEST_KEY_EVENT_ID_EXTRA = "request_eventId"
        private const val TAG = "AdActivity"

        @get:VisibleForTesting
        internal var eventListener: AdEventListener? = null
        internal var presenterDelegate: PresenterDelegate? = null

        internal var advertisement: AdPayload? = null
        internal var bidPayload: BidPayload? = null

        fun createIntent(context: Context?, placement: String, eventId: String?): Intent {
            val intent = Intent(context, VungleActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val bundle = Bundle()
            bundle.putString(REQUEST_KEY_EXTRA, placement)
            bundle.putString(REQUEST_KEY_EVENT_ID_EXTRA, eventId)
            intent.putExtras(bundle)
            return intent
        }

        private fun getPlacement(intent: Intent): String? {
            return try {
                intent.extras?.getString(REQUEST_KEY_EXTRA)
            } catch (_: Exception) {
                null
            }
        }

        private fun getEventId(intent: Intent): String? {
            return try {
                intent.extras?.getString(REQUEST_KEY_EVENT_ID_EXTRA)
            } catch (_: Exception) {
                null
            }
        }

    }
}
