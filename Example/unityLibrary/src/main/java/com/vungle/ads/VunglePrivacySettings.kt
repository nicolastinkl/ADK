package com.vungle.ads

import com.vungle.ads.internal.privacy.PrivacyConsent
import com.vungle.ads.internal.privacy.PrivacyManager

object VunglePrivacySettings {

    @JvmStatic
    fun setCOPPAStatus(isUserCoppa: Boolean) {
        PrivacyManager.updateCoppaConsent(isUserCoppa)
    }

    @JvmStatic
    fun getCOPPAStatus(): String {
        return PrivacyManager.getCoppaStatus().name
    }

    @JvmStatic
    fun setGDPRStatus(optIn: Boolean, consentMessageVersion: String?) {
        val consent = if (optIn) {
            PrivacyConsent.OPT_IN.getValue()
        } else {
            PrivacyConsent.OPT_OUT.getValue()
        }
        PrivacyManager.updateGdprConsent(consent, "publisher", consentMessageVersion)
    }

    @JvmStatic
    fun getGDPRStatus(): String {
        return PrivacyManager.getConsentStatus()
    }

    @JvmStatic
    fun getGDPRMessageVersion(): String {
        return PrivacyManager.getConsentMessageVersion()
    }

    @JvmStatic
    fun getGDPRSource(): String {
        return PrivacyManager.getConsentSource()
    }

    @JvmStatic
    fun getGDPRTimestamp(): Long {
        return PrivacyManager.getConsentTimestamp()
    }

    @JvmStatic
    fun setCCPAStatus(optIn: Boolean) {
        val consent = if (optIn) {
            PrivacyConsent.OPT_IN
        } else {
            PrivacyConsent.OPT_OUT
        }
        PrivacyManager.updateCcpaConsent(consent)
    }

    @JvmStatic
    fun getCCPAStatus(): String {
        return PrivacyManager.getCcpaStatus()
    }

    @JvmStatic
    fun setPublishAndroidId(publish: Boolean) {
        PrivacyManager.setPublishAndroidId(publish)
    }

}
