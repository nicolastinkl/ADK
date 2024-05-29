package com.vungle.ads

import android.content.Context
import com.vungle.ads.internal.model.AdPayload
import com.vungle.ads.internal.model.Placement
import com.vungle.ads.internal.presenter.AdPlayCallback
import com.vungle.ads.internal.presenter.PresenterDelegate
import com.vungle.ads.internal.ui.AdActivity

class RewardedAd(context: Context, placementId: String, adConfig: AdConfig = AdConfig()) :
    BaseFullscreenAd(context, placementId, adConfig) {

    override fun constructAdInternal(context: Context): RewardedAdInternal = RewardedAdInternal(context)

    private val rewardedAdInternal: RewardedAdInternal
        get() = adInternal as RewardedAdInternal

    fun setUserId(userId: String) {
        rewardedAdInternal.userId = userId
    }

    fun setAlertTitleText(titleText: String) {
        rewardedAdInternal.alertTitleText = titleText
    }

    fun setAlertBodyText(bodyText: String) {
        rewardedAdInternal.alertBodyText = bodyText
    }

    fun setAlertCloseButtonText(closeButtonText: String) {
        rewardedAdInternal.alertCloseButtonText = closeButtonText
    }

    fun setAlertContinueButtonText(continueButtonText: String) {
        rewardedAdInternal.alertContinueButtonText = continueButtonText
    }

}

internal class RewardedAdInternal(context: Context) : FullscreenAdInternal(context), PresenterDelegate {

    internal var alertTitleText: String? = null
    internal var alertBodyText: String? = null
    internal var alertCloseButtonText: String? = null
    internal var alertContinueButtonText: String? = null
    internal var userId: String? = null

    override fun getUserId(): String? {
        return userId
    }

    override fun getAlertTitleText(): String? {
        return alertTitleText
    }

    override fun getAlertBodyText(): String? {
        return alertBodyText
    }

    override fun getAlertCloseButtonText(): String? {
        return alertCloseButtonText
    }

    override fun getAlertContinueButtonText(): String? {
        return alertContinueButtonText
    }

    override fun isValidAdTypeForPlacement(placement: Placement): Boolean {
        return placement.isRewardedVideo()
    }

    override fun renderAd(
        listener: AdPlayCallback?,
        placement: Placement,
        advertisement: AdPayload
    ) {
        AdActivity.presenterDelegate = this@RewardedAdInternal
        super.renderAd(listener, placement, advertisement)
    }
}
