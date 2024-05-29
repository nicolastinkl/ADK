package com.vungle.ads

import android.content.Context
import com.vungle.ads.internal.model.Placement

class InterstitialAd(context: Context, placementId: String, adConfig: AdConfig = AdConfig()) :
    BaseFullscreenAd(context, placementId, adConfig) {

    override fun constructAdInternal(context: Context): InterstitialAdInternal = InterstitialAdInternal(context)
}

internal class InterstitialAdInternal(context: Context) : FullscreenAdInternal(context) {

    override fun isValidAdTypeForPlacement(placement: Placement): Boolean {
        return placement.isInterstitial()
    }

}
