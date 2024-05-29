package com.vungle.ads

interface RewardedAdListener : FullscreenAdListener {
    /**
     * Callback for the user has watched the advertisement to completion. The Vungle SDK has finished playing the
     * advertisement and the user has closed the advertisement.
     *
     * @param baseAd identifier for which the advertisement got rewarded.
     */
    fun onAdRewarded(baseAd: BaseAd)
}
