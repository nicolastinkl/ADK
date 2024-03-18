package com.vungle.ads.internal.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.VisibleForTesting
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.vungle.ads.*
import com.vungle.ads.ServiceLocator
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

        val closeButton = ImageView(this) 

        val encodedImage = "iVBORw0KGgoAAAANSUhEUgAAAMgAAADICAMAAACahl6sAAABqlBMVEUAAAD///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAARERETExNiYWFjYmJlZGRmZWVoZ2dpaGhqampsbGxvbm5wb29ycXFycXHMzMzOzs7Pzs7Pzs7Pz8/Qz8/Q0NDT0tLT0tLU09PV1NTV1NTW1dXX1tbW1dXX1tbY19f49/f49/f5+Pj5+Pj6+fn6+fn7+vr7+vr9/Pz+/f1/wVT5AAAAjHRSTlMAAAECAwQFBwgLDA0ODxAREhMVFhcYGRobHB0eICEiIyQsLS4wMTI1Njg5Ozw9Pj9AQURFRkdISUpLTk9QUVJUVVZXWFtcXl9gYWJjZWZoaWprbG5vcHFyc3R2d3h5ent8fX5/gISFnp+foKChoqKjpKSl19fX2NnZ2drc3Nzd3d3e3t75+vr7+/z8/SUGFOQAAAftSURBVHja7d35VxNXFAfwaSFiE1posOxLGixFkIALEUQjIhqSUgKIRA2yzTV2E7uv7i0Va+Z/buBwzACZZO579755RL6/w7zPOZDMzLvvXuO9ColxBDmCHEHKQwzyBFrDA9HY5PTNdCazBmuZTPrm9GQsOhBuDdBfjAdSG+qPzayCc1ZnYv2hWq0hVU19E2lwl/REX1OVlpDanqtLgMvS1Z5azSDBoRSIJTUU1AZSHxFV7Foi9RpAfOG4CbIx42Gft5C64QzQJDNc5x2keXwd6LI+3uwNpCsB1El0qYe0J4EjyXa1kMYbwJV4kzpIYMwEvphjATWQ6sgy8GY5Uq0A0jYH/Jlr44bUjJigIuZIDSukMw2qku7kg/jOm6Au5nkfEyQ4A2ozE2SBhJdBdVbC9JDqKHiRaDUxxJ8Ab5Lwk0IaboFXudVACOnIgHdZ6iSDnFwDL7N2kghyygRvY54igUTA+0QIIGdBh5yVhujhKC1xA4mALhmUgpwCfXJKAtJtagQxu4UhHWugU9Y6BCEnlkCvLJ0QgnywCLol7ReAVCdAvySq8ZAo6JgoGvIp6JkwEhJc0RSyEkRBfCnQNaljGMgF0DcjCEiXqTHE7HINqUmDzknXuIWMgt4ZdQlpMzWHmG2uIL450D1zPjeQ06B/TruA+O8dAsg9f3nIJTgMuVQW0mQeCojZVA5yAw5H4mUg7fhfmf31geyqNn66j/6Z9tIQ/NNU9qX1j6RkYzP3F1qSLAkJ4R3PLUtSknfkBCShUpCEkENOsuPI5R5jJYkSkBa045llSUp2HXlJFvmTLc6QcVGHuOStAy8Zd4TUrws7RCU2B1qyXu8EQb44yT6xLEnJHkcu9xQniTpAfHdlHCKSfQ6s5K6vOCSMWsT9/Q7L2nwg6cjlnqEk4eKQuKQDKyniQEriRSH1pqwDJynqwEnM+mKQQYzjsWVJShwcudwLhGSwGGQeAfndsiQljo5c7jf365gtAvkE8+f943+SkhKOzQ3EQoIHIWdAoYTKAUMHIci3vVISMgekDkA+xH6ZSUjoHACB/ZDPQZmE0gE9+yHXQJWE1LFzC2yHVIns4JaSfKXGAUtVeyGNQg8TAhJiB0DjXkgvqJGQO6B3L+SK4IPqDzgJvQOu7IUIFwegJAwOWNwDCYi/BEFIOBzb3yQ2SAgUSHgcELJD+oFfwuSAfjvkMrBLHjI5IGaHSNYHuJCwOfL3jTaIbMFGWQmfA1ZsEL/0dkUZCaMj/7FVgLQAr4TVAa0FSBhYJVuvOB0QLkAGgFWSY3XAQAFCUy3nJGF2QLQAuQw0ktdeOCBWgFwHPgm7A64XINPAJuF3wHQBchO4JAocsFCA3AEmiQoHLBYglLXjdokSBywVIJQ9KGwSNQ47BIBDosgB61yQXYkqBwAbZEeizrHO9D+yI3nj7HhF7LD/j5CfeHm45QzZ+poPcofa4fz8kbOsf4kltu+RBZUOcskCw72WKwe15Ev6u1+XDmLJdfLnEdcOWkmM+gkR4SCVRImf2VEOSskA7VsUpCMv+Yboora3KK0eOOgkrZRvGgUcZJIA4btfIQeRZOV9urfxgg4aSYpuf6ScY+uNxSiJke1YlXNsbnz/mlHST7WHWN4BwCkJEe3qunGwSgI0++zuHHnJFpMkTVP54NbBJ7lCUovi3sEm6aWoDsI4uCSNBPVaOEdpybeibx6q5CvosA4WyVX5mka8g0PSI11lKuJgkARk637FHOSSlGwltqiDWnJGsjZe3EEsaZA8rSDhIJXMyp4fAak6GTpJ0fMjH5uKHHSS4id6jClVDoBHNJIp6VNv0nVLpSTfuX+j5XAO0X3H0j+k99VKSH5xuwqnc4iIk6HZl9L7g46SJ64Pi0UJzupmX0jvczpI3B/fcz6rizk9XVSC268tKkEcQxynOc+efSa971xEgjlO2ULUYeCABL9/fkDyHOFIkvV82CcRqQPYJ8E4Svd8MFBTRbJPpesZ9khQjiRlXxSbRLQuwyZ5iToCXqYvCu4seEEiXl/yVoJzxIl7B2UfS9fJ7EpwjvK9g7DdnHYkcvU+j17lHX/j2j1cctFfaxktka1bykuQjmU/Q8ez+z9L1y09+hPZEuW0qx5086B75l31oDPate8K2OGyT+NFzSEX3TacPK5558zjFdLLNIToLjuiMQTTXdY4pm+/39ljFdKBuQHZEzusKaT7ne1Srmff+KRA33jDr9+3STrwTs9WqJxpF5Uzf8Qw+jSC9EnN6BnUxiE3o8cwzmniOCc9x+qc9o7DNFlssEJmvfVVyPS9z8jmIXZ6+R1POA/R0wmVi5QTKj2cGZqknRmav6v3ZkjMBeoprtvPjOqffjnm6lbQpGPD8A0rnT09zDV7unKmgW/PIRpVNJ99lHc++/YEHxWTb+bbMEsSgxi+CPdY8OWIz1AAMYzAGOfflzkWQK5HGGIYzXE2R7wFvRoJiGF0JFkYyQ6BtUhBDCNET/kiJLQSSUj+D2ycsuvF+niL4DqkIYZRF6Wa3J6J1gmvggCS/zDunpL/CDOnun0SayCBbJdCDs7K7UEN1sstgAqyfWM8JLpXlxoKSl+dEJLPR73XsE/2S9d6P6K4NC0kn6rmvgm3t8fpib7mKqLrkkN2Uhvqj6VWSxBWU7H+UC3lJXkgu7djreGBaGxyeuF2JmOCmcncXpiejEUHwq0B+osVIJWQI8gRhCn/Ay0yO3nFetQyAAAAAElFTkSuQmCC"
        val decodedString: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.count())
        closeButton.setImageBitmap(decodedByte)

        closeButton.setOnClickListener {
            finish()
        }

        //Left TOP
        val layoutParams = RelativeLayout.LayoutParams(80, 80)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        layoutParams.setMargins(10,10,0,0)

        //Riught Top
//        val layoutParams = RelativeLayout.LayoutParams(100, 100)
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
//        layoutParams.setMargins(0,10,10,0)


        Handler(Looper.getMainLooper()).postDelayed({

            mraidAdWidget.addView(closeButton,layoutParams)

        }, 5000)

        Handler(Looper.getMainLooper()).postDelayed({
            mraidAdWidget.removeView(closeButton)

        }, 60000)

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
