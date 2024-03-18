package com.vungle.ads.internal.omsdk

import android.util.Base64
import android.util.Log
import android.view.View
import com.iab.omid.library.vungle.Omid
import com.iab.omid.library.vungle.adsession.AdEvents
import com.iab.omid.library.vungle.adsession.AdSession
import com.iab.omid.library.vungle.adsession.AdSessionConfiguration
import com.iab.omid.library.vungle.adsession.AdSessionContext
import com.iab.omid.library.vungle.adsession.CreativeType
import com.iab.omid.library.vungle.adsession.ImpressionType
import com.iab.omid.library.vungle.adsession.Owner
import com.iab.omid.library.vungle.adsession.Partner
import com.iab.omid.library.vungle.adsession.VerificationScriptResource
import com.vungle.ads.BuildConfig
import com.vungle.ads.internal.model.OmSdkData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URL

class NativeOMTracker(omSdkData: String) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
    }

    private var adSession: AdSession? = null
    private var adEvents: AdEvents? = null

    init {
        try {
            // getNativeAdSession
            val adSessionConfiguration = AdSessionConfiguration.createAdSessionConfiguration(
                CreativeType.NATIVE_DISPLAY,
                ImpressionType.BEGIN_TO_RENDER,
                Owner.NATIVE,
                Owner.NONE,
                false
            )
            val partner =
                Partner.createPartner(BuildConfig.OMSDK_PARTNER_NAME, BuildConfig.VERSION_NAME)

            // getVerificationScriptResources
            val decoded = Base64.decode(omSdkData, Base64.DEFAULT)
            val omSdkDataEntry = decoded?.let {
                val jsonStr = String(it)
                json.decodeFromString<OmSdkData>(jsonStr)
            }
            val verificationScriptResource =
                VerificationScriptResource.createVerificationScriptResourceWithParameters(
                    omSdkDataEntry?.vendorKey,
                    URL(omSdkDataEntry?.vendorURL),
                    omSdkDataEntry?.params
                )
            val verificationScripts = listOf<VerificationScriptResource>(verificationScriptResource)

            // createAdSession
            val adSessionContext = AdSessionContext.createNativeAdSessionContext(
                partner,
                Res.OM_JS,
                verificationScripts,
                null,
                null
            )
            adSession = AdSession.createAdSession(adSessionConfiguration, adSessionContext)
        } catch (e: Exception) {
            Log.e("NativeOMTracker", "error occured when create omsdk adSession:", e)
        }
    }

    fun start(view: View) {
        if (Omid.isActive()) {
            adSession?.let {
                it.registerAdView(view)
                it.start()

                // Trigger Native ad load finish event
                adEvents = AdEvents.createAdEvents(it)
                adEvents?.loaded()
            }
        }
    }

    fun stop() {
        adSession?.finish()
        adSession = null
    }

    fun impressionOccurred() {
        adEvents?.impressionOccurred()
    }

}
