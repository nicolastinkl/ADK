package com.vungle.ads.internal.privacy

import android.content.Context
import com.vungle.ads.ServiceLocator
import com.vungle.ads.internal.model.Cookie.CCPA_CONSENT_STATUS
import com.vungle.ads.internal.model.Cookie.COPPA_DISABLE_AD_ID
import com.vungle.ads.internal.model.Cookie.COPPA_STATUS_KEY
import com.vungle.ads.internal.model.Cookie.GDPR_CONSENT_MESSAGE_VERSION
import com.vungle.ads.internal.model.Cookie.GDPR_CONSENT_SOURCE
import com.vungle.ads.internal.model.Cookie.GDPR_CONSENT_STATUS
import com.vungle.ads.internal.model.Cookie.GDPR_CONSENT_TIMESTAMP
import com.vungle.ads.internal.model.Cookie.PUBLISH_ANDROID_ID
import com.vungle.ads.internal.persistence.FilePreferences
import java.util.concurrent.atomic.AtomicReference

internal object PrivacyManager {

    /**
     * Value from server to respect
     */
    private val disableAdId = AtomicReference<Boolean?>()
    private val coppaStatus = AtomicReference<Boolean?>()

    private var gdprConsent: String? = null
    private var gdprConsentSource: String? = null
    private var gdprConsentMessageVersion: String? = null
    private var gdprConsentTimestamp: Long? = null

    private var ccpaConsent: PrivacyConsent? = null

    private var publishAndroidId = AtomicReference<Boolean?>()

    private var filePreferences: FilePreferences? = null

    fun init(context: Context) {
        val filePreferences =
            ServiceLocator.getInstance(context).getService(FilePreferences::class.java)
        this.filePreferences = filePreferences
        //Restore the DisableAdId if publisher set it before calling init
        val disableAdIdFlag = disableAdId.get()
        if (disableAdIdFlag != null) {
            saveDisableAdId(disableAdIdFlag)
        } else {
            val storedDisableAdId = filePreferences.getBoolean(COPPA_DISABLE_AD_ID)
            storedDisableAdId?.run { disableAdId.set(this) }
        }

        //Restore the consent if publisher set it before calling init
        val gdpr = gdprConsent
        if (gdpr != null) {
            saveGdprConsent(
                gdpr,
                gdprConsentSource ?: "",
                gdprConsentMessageVersion ?: "",
                gdprConsentTimestamp ?: 0L
            )
        } else {
            val storedGdpr = filePreferences.getString(GDPR_CONSENT_STATUS)
            gdprConsent = when (storedGdpr) {
                PrivacyConsent.OPT_IN.getValue() -> {
                    PrivacyConsent.OPT_IN.getValue()
                }
                PrivacyConsent.OPT_OUT.getValue() -> {
                    PrivacyConsent.OPT_OUT.getValue()
                }
                else -> {
                    storedGdpr
                }
            }
            gdprConsentSource = filePreferences.getString(GDPR_CONSENT_SOURCE)
            gdprConsentMessageVersion =
                filePreferences.getString(GDPR_CONSENT_MESSAGE_VERSION)
            gdprConsentTimestamp = filePreferences.getLong(GDPR_CONSENT_TIMESTAMP, 0)
        }

        //Restore the CCPA if publisher set it before calling init
        val ccpaStatus = ccpaConsent
        if (ccpaStatus != null) {
            saveCcpaConsent(ccpaStatus)
        } else {
            val storedCcpa = filePreferences.getString(CCPA_CONSENT_STATUS)
            ccpaConsent = if (PrivacyConsent.OPT_OUT.getValue() == storedCcpa) {
                PrivacyConsent.OPT_OUT
            } else {
                PrivacyConsent.OPT_IN
            }
        }

        //Restore the COPPA if publisher set it before calling init
        val coppa = coppaStatus.get()
        if (coppa != null) {
            saveCoppaConsent(coppa)
        } else {
            val storedCoppa = filePreferences.getBoolean(COPPA_STATUS_KEY)
            storedCoppa?.run { coppaStatus.set(this) }
        }

        //Restore the publishAndroidId if publisher set it before calling init
        val publishAndroidIdFlag = publishAndroidId.get()
        if (publishAndroidIdFlag != null) {
            savePublishAndroidId(publishAndroidIdFlag)
        } else {
            val storedPublishAndroidId = filePreferences.getBoolean(PUBLISH_ANDROID_ID)
            storedPublishAndroidId?.run { publishAndroidId.set(this) }
        }

    }

    fun updateGdprConsent(consent: String, source: String, consentMessageVersion: String?) {
        gdprConsent = consent
        gdprConsentSource = source
        gdprConsentMessageVersion = consentMessageVersion
        val gdprConsentTimestamp = System.currentTimeMillis() / 1000 // Server requires seconds
        this.gdprConsentTimestamp = gdprConsentTimestamp
        saveGdprConsent(consent, source, gdprConsentMessageVersion ?: "", gdprConsentTimestamp)
    }

    private fun saveGdprConsent(
        consent: String,
        source: String,
        consentMessageVersion: String,
        consentTimestamp: Long
    ) {
        filePreferences?.put(GDPR_CONSENT_STATUS, consent)?.put(GDPR_CONSENT_SOURCE, source)
            ?.put(GDPR_CONSENT_MESSAGE_VERSION, consentMessageVersion)
            ?.put(GDPR_CONSENT_TIMESTAMP, consentTimestamp)?.apply()
    }

    fun updateCcpaConsent(consent: PrivacyConsent) {
        ccpaConsent = consent

        saveCcpaConsent(consent)
    }

    fun getCcpaStatus(): String {
        return ccpaConsent?.getValue() ?: PrivacyConsent.OPT_IN.getValue()
    }

    private fun saveCcpaConsent(consent: PrivacyConsent) {
        filePreferences?.put(CCPA_CONSENT_STATUS, consent.getValue())?.apply()
    }

    fun updateCoppaConsent(newValue: Boolean) {
        coppaStatus.set(newValue)

        saveCoppaConsent(newValue)
    }

    private fun saveCoppaConsent(value: Boolean) {
        filePreferences?.put(COPPA_STATUS_KEY, value)?.apply()
    }

    fun updateDisableAdId(newValue: Boolean) {
        disableAdId.set(newValue)

        saveDisableAdId(newValue)
    }

    private fun saveDisableAdId(value: Boolean) {
        filePreferences?.put(COPPA_DISABLE_AD_ID, value)?.apply()
    }

    fun getConsentStatus(): String {
        return gdprConsent ?: "unknown"
    }

    fun getConsentSource(): String {
        return gdprConsentSource ?: "no_interaction"
    }

    fun getConsentMessageVersion(): String {
        return gdprConsentMessageVersion ?: ""
    }

    fun getConsentTimestamp(): Long {
        return gdprConsentTimestamp ?: 0L
    }

    fun getCoppaStatus(): COPPA {
        if (coppaStatus.get() == null) {
            return COPPA.COPPA_NOTSET
        } else if (coppaStatus.get() == true) {
            return COPPA.COPPA_ENABLED
        } else if (coppaStatus.get() == false) {
            return COPPA.COPPA_DISABLED
        }
        return COPPA.COPPA_NOTSET
    }

    fun shouldSendAdIds(): Boolean {
        val value = disableAdId.get()
        return if (value == null) false else !value
    }

    fun setPublishAndroidId(publish: Boolean) {
        publishAndroidId.set(publish)

        savePublishAndroidId(publish)
    }

    private fun savePublishAndroidId(publish: Boolean) {
        filePreferences?.put(PUBLISH_ANDROID_ID, publish)?.apply()
    }

    fun getPublishAndroidId(): Boolean {
        return publishAndroidId.get() ?: true
    }

}
