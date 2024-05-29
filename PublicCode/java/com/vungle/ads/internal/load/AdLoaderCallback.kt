package com.vungle.ads.internal.load

import com.vungle.ads.VungleError
import com.vungle.ads.internal.model.AdPayload

interface AdLoaderCallback {

    fun onSuccess(advertisement: AdPayload)

    fun onFailure(error: VungleError)
}