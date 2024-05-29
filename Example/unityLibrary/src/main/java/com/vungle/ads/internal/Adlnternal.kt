package com.vungle.ads.internal

import android.content.Context
import android.util.Log
import com.vungle.ads.AdExpiredError
import com.vungle.ads.AdExpiredOnPlayError
import com.vungle.ads.AdMarkupInvalidError
import com.vungle.ads.AdNotLoadedCantPlay
import com.vungle.ads.AnalyticsClient
import com.vungle.ads.BuildConfig
import com.vungle.ads.ConcurrentPlaybackUnsupported
import com.vungle.ads.InternalError
import com.vungle.ads.InvalidAdStateError
import com.vungle.ads.InvalidWaterfallPlacementError
import com.vungle.ads.PlacementAdTypeMismatchError
import com.vungle.ads.PlacementNotFoundError
import com.vungle.ads.SdkNotInitialized
import com.vungle.ads.ServiceLocator.Companion.inject
import com.vungle.ads.TimeIntervalMetric
import com.vungle.ads.VungleAds
import com.vungle.ads.VungleError
import com.vungle.ads.VungleError.Companion.AD_EXPIRED
import com.vungle.ads.VungleError.Companion.INVALID_AD_STATE
import com.vungle.ads.VungleError.Companion.codeToLoggableReason
import com.vungle.ads.internal.downloader.Downloader
import com.vungle.ads.internal.executor.SDKExecutors
import com.vungle.ads.internal.load.AdLoaderCallback
import com.vungle.ads.internal.load.AdRequest
import com.vungle.ads.internal.load.BaseAdLoader
import com.vungle.ads.internal.load.DefaultAdLoader
import com.vungle.ads.internal.load.RealtimeAdLoader
import com.vungle.ads.internal.model.AdPayload
import com.vungle.ads.internal.model.BidPayload
import com.vungle.ads.internal.model.Placement
import com.vungle.ads.internal.network.VungleApiClient
import com.vungle.ads.internal.omsdk.OMInjector
import com.vungle.ads.internal.presenter.AdEventListener
import com.vungle.ads.internal.presenter.AdPlayCallback
import com.vungle.ads.internal.presenter.AdPlayCallbackWrapper
import com.vungle.ads.internal.protos.Sdk
import com.vungle.ads.internal.task.CleanupJob
import com.vungle.ads.internal.task.JobRunner
import com.vungle.ads.internal.ui.AdActivity
import com.vungle.ads.internal.util.ActivityManager
import com.vungle.ads.internal.util.PathProvider
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.lang.ref.WeakReference

abstract class AdInternal(val context: Context) : AdLoaderCallback {
    companion object {
        private val TAG = AdInternal::class.simpleName

        private val THROW_ON_ILLEGAL_TRANSITION = BuildConfig.DEBUG

        private val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            explicitNulls = false
        }
    }

    var adState: AdState = AdState.NEW
        set(value) {
            if (value.isTerminalState()) {
                advertisement?.eventId()?.let {
                    val jobRunner: JobRunner by inject(context)
                    val jobInfo = CleanupJob.makeJobInfo(it)
                    jobRunner.execute(jobInfo)
                }
            }

            field = adState.transitionTo(value)
        }

    var advertisement: AdPayload? = null
    var placement: Placement? = null
    var bidPayload: BidPayload? = null
    private var adLoaderCallback: AdLoaderCallback? = null
    private val vungleApiClient: VungleApiClient by inject(context)

    private var baseAdLoader: BaseAdLoader? = null

    private var requestMetric: TimeIntervalMetric? = null

    private var playContext: WeakReference<Context>? = null

    fun canPlayAd(onPlay: Boolean = false): VungleError? {
        val error: VungleError =
            when {
                advertisement == null -> AdNotLoadedCantPlay()
                advertisement?.hasExpired() == true -> if (onPlay) {
                    AdExpiredOnPlayError()
                } else {
                    AdExpiredError()
                }

                adState == AdState.PLAYING -> ConcurrentPlaybackUnsupported()
                adState != AdState.READY -> InvalidAdStateError()
                else -> return null
            }
        if (onPlay) {
            error.setPlacementId(placement?.referenceId)
                .setCreativeId(advertisement?.getCreativeId())
                .setEventId(advertisement?.eventId())
                .logErrorNoReturnValue()
        }
        return error
    }

    abstract fun isValidAdTypeForPlacement(placement: Placement): Boolean

    abstract fun isValidAdSize(adSize: String): Boolean

    abstract fun getAdSizeForAdRequest(): String

    fun loadAd(
        placementId: String,
        adMarkup: String?,
        adLoaderCallback: AdLoaderCallback
    ) {
        this.adLoaderCallback = adLoaderCallback
        if (!VungleAds.isInitialized()) {
            adLoaderCallback.onFailure(SdkNotInitialized())
            return
        }

        val pl = ConfigManager.getPlacement(placementId)
        if (pl == null) {
            adLoaderCallback.onFailure(PlacementNotFoundError(placementId).logError())
            return
        }
        this.placement = pl
        if (!isValidAdTypeForPlacement(pl)) {
            adLoaderCallback.onFailure(PlacementAdTypeMismatchError(pl.referenceId).logError())
            return
        }
        val adSize = getAdSizeForAdRequest()
        if (!isValidAdSize(adSize)) {
            adLoaderCallback.onFailure(InternalError(VungleError.INVALID_SIZE))
            return
        }

        if (pl.headerBidding && adMarkup.isNullOrEmpty() || !pl.headerBidding && !adMarkup.isNullOrEmpty()) {
            adLoaderCallback.onFailure(InvalidWaterfallPlacementError(placementId).logError())
            return
        }
        if (adState != AdState.NEW) {
            val error = when (adState) {
                AdState.NEW -> TODO() // Not possible
                AdState.LOADING -> VungleError.AD_IS_LOADING
                AdState.READY -> VungleError.AD_ALREADY_LOADED
                AdState.PLAYING -> VungleError.AD_IS_PLAYING
                AdState.FINISHED -> VungleError.AD_CONSUMED
                AdState.ERROR -> VungleError.AD_ALREADY_FAILED
            }
            adLoaderCallback.onFailure(
                InvalidAdStateError(
                    INVALID_AD_STATE,
                    codeToLoggableReason(error),
                    "$adState state is incorrect for load",
                    placementId = placementId,
                    creativeId = advertisement?.getCreativeId(),
                    eventId = advertisement?.eventId()
                )
                    .logError()
            )
            return
        }

        val type =
            if (ConfigManager.adLoadOptimizationEnabled()) Sdk.SDKMetric.SDKMetricType.AD_REQUEST_TO_CALLBACK_ADO_DURATION_MS
            else Sdk.SDKMetric.SDKMetricType.AD_REQUEST_TO_CALLBACK_DURATION_MS
        requestMetric = TimeIntervalMetric(type)
        requestMetric?.markStart()
        if (!adMarkup.isNullOrEmpty()) {
            try {
                bidPayload = json.decodeFromString<BidPayload>(adMarkup)
            } catch (e: IllegalArgumentException) {
                AnalyticsClient.logError(
                    VungleError.INVALID_ADUNIT_BID_PAYLOAD,
                    "Unable to decode payload into BidPayload object. Error: ${e.localizedMessage}",
                    placementId,
                    eventId = advertisement?.eventId(),
                    )
                adLoaderCallback.onFailure(AdMarkupInvalidError())
                return
            } catch (e: Exception) {
                AnalyticsClient.logError(
                    VungleError.INVALID_JSON_BID_PAYLOAD,
                    "Unable to decode payload into BidPayload object. Error: ${e.localizedMessage}",
                    placementId,
                    eventId = advertisement?.eventId(),
                    )
                adLoaderCallback.onFailure(AdMarkupInvalidError())
                return
            }
        }

        adState = AdState.LOADING

        val omInjector: OMInjector by inject(context)
        val sdkExecutors: SDKExecutors by inject(context)
        val pathProvider: PathProvider by inject(context)
        val downloader: Downloader by inject(context)

        if (adMarkup.isNullOrEmpty()) {
            val adRequest = AdRequest(pl, null, adSize)
            val adLoader = DefaultAdLoader(
                context,
                vungleApiClient,
                sdkExecutors,
                omInjector,
                downloader,
                pathProvider,
                adRequest
            )
            baseAdLoader = adLoader
            adLoader.loadAd(this)
        } else {
            val adRequest = AdRequest(pl, bidPayload, adSize)
            val adLoader = RealtimeAdLoader(
                context,
                vungleApiClient,
                sdkExecutors,
                omInjector,
                downloader,
                pathProvider,
                adRequest
            )
            baseAdLoader = adLoader
            adLoader.loadAd(this)
        }
    }

    internal fun cancelDownload() {
        baseAdLoader?.cancel()
    }

    fun play(context: Context?, adPlayCallback: AdPlayCallback) {
        playContext = if (context != null) {
            WeakReference(context)
        } else {
            null
        }

        val error = canPlayAd(true)
        if (error != null) {
            adPlayCallback.onFailure(error)
            if (isErrorTerminal(error.code)) {
                adState = AdState.ERROR
            }
            return
        }

        val pl = placement ?: return
        val adv = advertisement ?: return

        val callbackWrapper = object : AdPlayCallbackWrapper(adPlayCallback) {
            override fun onAdStart(id: String?) {
                adState = AdState.PLAYING
                super.onAdStart(id)
            }

            override fun onAdEnd(id: String?) {
                adState = AdState.FINISHED
                super.onAdEnd(id)
            }

            override fun onFailure(error: VungleError) {
                adState = AdState.ERROR
                super.onFailure(error)
            }
        }

        cancelDownload()

        renderAd(callbackWrapper, pl, adv)
    }

    internal open fun renderAd(
        listener: AdPlayCallback?,
        placement: Placement,
        advertisement: AdPayload
    ) {
        /// Subscribe to the event bus of the activity before starting the activity, otherwise
        /// the Publisher notices it has no subscribers and does not emit the start value.
        AdActivity.eventListener = object : AdEventListener(
            listener,
            placement
        ) {}

        AdActivity.advertisement = advertisement
        AdActivity.bidPayload = bidPayload

        // Start the activity, and if there are any extras that have been overridden by the application, apply them.
        // Special case that we allow load ad in A activity and play it in B activity.
        val ctx = playContext?.get() ?: context
        val intent = AdActivity.createIntent(ctx, placement.referenceId, advertisement.eventId())
        ActivityManager.startWhenForeground(ctx, null, intent, null)
    }

    override fun onSuccess(advertisement: AdPayload) {
        this@AdInternal.advertisement = advertisement
        adState = AdState.READY
        adLoadedAndUpdateConfigure(advertisement)
        adLoaderCallback?.onSuccess(advertisement)
        requestMetric?.let {
            it.markEnd()
            AnalyticsClient.logMetric(it,
                placement?.referenceId, advertisement.getCreativeId(), advertisement.eventId())
        }
    }

    internal open fun adLoadedAndUpdateConfigure(advertisement: AdPayload) {
    }

    override fun onFailure(error: VungleError) {
        adState = AdState.ERROR
        adLoaderCallback?.onFailure(error)
    }

    /*
      We use this method to check if we allowed to change ad state to ERROR. Because we use ERROR
    state to clean up the ad assets in the device.
      There's only one case we need to clean up assets which is pub try to play one READY but has
    expired ad.

    |InstanceState | canPlay() | expired | loadAd() | playAd() | playAd()-to-ERROR? |
    |--------------|-----------|---------|----------|----------|--------------------|
    |    NEW       |      10   |     -   |     Y    |    10    |            N       |
    |    LOADING   |      10   |     -   |     42   |    10    |            N       |
    |    READY     |      0    |     N   |     42   |     Y    |            N       |
    |    READY     |      4    |     Y   |     42   |     4    |            Y       |
    |    PLAYING   |    4,46   |    Y/N  |     42   |   4,46   |            N       |
    |    FINISHED  |    4,42   |    Y/N  |     42   |   4,42   |            N       |
    |    ERROR     |   10,4,42 |    Y/N  |     42   |  10,4,42 |            N       |

     */
    internal fun isErrorTerminal(errorCode: Int): Boolean {
        return adState == AdState.READY && errorCode == AD_EXPIRED
    }

    enum class AdState {
        NEW {
            override fun canTransitionTo(adState: AdState): Boolean {
                return adState === LOADING || adState === READY || adState === ERROR
            }
        },
        LOADING {
            override fun canTransitionTo(adState: AdState): Boolean {
                return adState === READY || adState === ERROR
            }
        },
        READY {
            override fun canTransitionTo(adState: AdState): Boolean {
                return adState === PLAYING || adState === FINISHED || adState === ERROR
            }
        },
        PLAYING {
            override fun canTransitionTo(adState: AdState): Boolean {
                return adState === FINISHED || adState === ERROR
            }
        },
        FINISHED {
            override fun canTransitionTo(adState: AdState): Boolean {
                return false
            }
        },
        ERROR {
            override fun canTransitionTo(adState: AdState): Boolean {
                return adState === FINISHED
            }
        };

        abstract fun canTransitionTo(adState: AdState): Boolean

        fun transitionTo(adState: AdState): AdState {
            if (this != adState && !canTransitionTo(adState)) {
                val msg = "Cannot transition from ${this.name} to ${adState.name}"
                if (THROW_ON_ILLEGAL_TRANSITION) {
                    throw IllegalStateException(msg)
                } else {
                    Log.e(TAG, "Illegal state transition", IllegalStateException(msg))
                }
            }
            return adState
        }

        fun isTerminalState(): Boolean {
            return this in listOf(FINISHED, ERROR)
        }
    }
}
