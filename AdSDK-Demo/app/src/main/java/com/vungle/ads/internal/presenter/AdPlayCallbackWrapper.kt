package com.vungle.ads.internal.presenter

import com.vungle.ads.VungleError


interface AdPlayCallback {

    fun onAdStart(id: String?)

    fun onAdImpression(id: String?)

    fun onAdEnd(id: String?)

    fun onAdClick(id: String?)

    fun onAdRewarded(id: String?)

    fun onAdLeftApplication(id: String?)

    fun onFailure(error: VungleError)

}

open class AdPlayCallbackWrapper(private val adPlayCallback: AdPlayCallback) : AdPlayCallback by adPlayCallback
