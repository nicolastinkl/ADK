package com.vungle.ads

interface BaseAdListener {
    /**
     * Callback used to notify that the advertisement assets have been downloaded and are ready to
     * play.
     *
     * @param baseAd identifier for which the advertisement assets have been downloaded.
     */
    fun onAdLoaded(baseAd: BaseAd)

    /**
     * Called when the Vungle SDK has successfully launched the advertisement
     * and an advertisement will begin playing momentarily.
     *
     * @param baseAd identifier for which the advertisement being played.
     */
    fun onAdStart(baseAd: BaseAd)

    /**
     * Called when the Vungle SDK has shown an Ad for the play/render request
     * For Vungle adImpression should be same as adStart as we show every Ad only once,
     * hence a unique Ad can never generate multiple impressions
     * @param baseAd identifier for which the advertisement being played.
     */
    fun onAdImpression(baseAd: BaseAd)

    /**
     * Callback for an advertisement ending. The Vungle SDK has finished playing the advertisement and
     * the user has closed the advertisement.
     *
     * @param baseAd identifier for which the advertisement that ended.
     */
    fun onAdEnd(baseAd: BaseAd)

    /**
     * Callback for an advertisement tapped. Sent when the user has tapped on an ad.
     *
     * @param baseAd identifier for which the advertisement that was clicked.
     */
    fun onAdClicked(baseAd: BaseAd)

    /**
     * Callback when the user has left the app.
     *
     * @param baseAd identifier for which the advertisement that user clicked resulting in leaving app
     */
    fun onAdLeftApplication(baseAd: BaseAd)

    /**
     * Callback used to notify that an error has occurred while downloading assets. This indicates
     * an unrecoverable error within the SDK, such as lack of network or out of disk space on the
     * device.
     *
     * @param baseAd identifier for which the advertisement for which the error occurred.
     * @param adError   Error message and event suggesting the cause of failure
     */
    fun onAdFailedToLoad(baseAd: BaseAd, adError: VungleError)

    /**
     * Callback used to notify that an error has occurred while playing advertisement. This indicates
     * an unrecoverable error within the SDK, such as lack of network or out of disk space on the
     * device.
     *
     * @param baseAd identifier for which the advertisement for which the error occurred.
     * @param adError   Error message and event suggesting the cause of failure
     */
    fun onAdFailedToPlay(baseAd: BaseAd, adError: VungleError)

}
