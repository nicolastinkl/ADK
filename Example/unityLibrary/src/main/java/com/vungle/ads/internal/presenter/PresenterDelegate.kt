package com.vungle.ads.internal.presenter

internal interface PresenterDelegate {

    fun getUserId(): String?

    // String used for the title of the alert dialog that displays
    // when a user prematurely closes a rewarded ad experience
    fun getAlertTitleText(): String?

    // String used for the body text of the alert dialog that displays
    // when a user prematurely closes a rewarded ad experience
    fun getAlertBodyText(): String?

    // String title for the close button text of the alert dialog that displays
    // when a user prematurely closes a rewarded ad experience
    fun getAlertCloseButtonText(): String?

    // String title for the continue button text of the alert dialog that displays
    // when a user prematurely closes a rewarded ad experience
    fun getAlertContinueButtonText(): String?
}
