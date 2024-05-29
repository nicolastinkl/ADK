package com.vungle.ads.internal.presenter

import android.util.Log
import com.vungle.ads.VungleError
import com.vungle.ads.internal.model.Placement

open class AdEventListener(
    private val playAdCallback: AdPlayCallback?,
    private var placement: Placement?
) {
    companion object {
        private const val TAG = "AdEventListener"
    }

    /**
     * The percentage of the ad that was viewed. We initialize to -1 in order to differentiate
     * between the ad never starting.
     */
    private var adRewarded = false
    fun onNext(s: String, value: String?, id: String?) {
        Log.d(TAG, "s=$s, value=$value, id=$id")
        when (s) {
            "start" -> {
                playAdCallback?.onAdStart(id)
            }
            "adViewed" -> {
                playAdCallback?.onAdImpression(id)
            }
            "successfulView" -> {
                if (placement?.isIncentivized == true && !adRewarded) {
                    adRewarded = true
                    playAdCallback?.onAdRewarded(id)
                }
            }
            "end" -> {
                playAdCallback?.onAdEnd(id)
            }
            "open" -> {
                when (value) {
                    "adClick" -> {
                        playAdCallback?.onAdClick(id)
                    }
                    "adLeftApplication" -> {
                        playAdCallback?.onAdLeftApplication(id)
                    }
                }
            }
        }
    }

    fun onError(error: VungleError, placementId: String?) {
        if (playAdCallback != null) {
            playAdCallback.onFailure(error)
            Log.e(TAG, "AdEventListener#PlayAdCallback $placementId", error)
        }
    }

}
