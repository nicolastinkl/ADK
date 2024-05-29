package com.vungle.ads.internal.ui

import com.vungle.ads.internal.presenter.AdEventListener
import com.vungle.ads.internal.util.ActivityManager

class PresenterAppLeftCallback(
    private val bus: AdEventListener?,
    private val placementRefId: String?
) : ActivityManager.LeftApplicationCallback {
    override fun onLeftApplication() {
        bus?.onNext("open", "adLeftApplication", placementRefId)
    }
}
