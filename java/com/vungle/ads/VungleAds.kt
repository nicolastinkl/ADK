package com.vungle.ads

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import com.vungle.ads.internal.VungleInitializer
import com.vungle.ads.internal.VungleInternal
import com.vungle.ads.internal.network.VungleApiClient

class VungleAds {

    companion object {

        const val TAG: String = "VungleAds"
        private var vungleInternal: VungleInternal = VungleInternal()
        private var initializer: VungleInitializer = VungleInitializer()

        @JvmStatic
        fun init(
            context: Context,
            appId: String,
            callback: InitializationListener
        ) {
            val appContext = if (context !is Application) context.applicationContext else context

            initializer.init(appId, appContext, callback)
        }

        /**
         * @return whether or not VungleAds is initialized. If "true" it can be used normally,
         * if "false" - it needs to be initialized first, otherwise errors and exceptions will
         * be thrown
         */
        @JvmStatic
        fun isInitialized() = initializer.isInitialized()

        /**
         * Will return an encoded string of advertisement bidding token.
         * This method might be called from adapter side.
         *
         * @return an encoded string contains available bid tokens digest. In rare cases, this can return empty string
         */
        @JvmStatic
        fun getBiddingToken(context: Context): String? {
            return vungleInternal.getAvailableBidTokens(context)
        }

        /**
         * @return version of Vungle SDK currently used, like "7.0.0"
         * */
        @JvmStatic
        fun getSdkVersion() : String = vungleInternal.getSdkVersion()

        /**
         * Override the info passed from Vungle's Plugins and Adapters
         * This should NOT be used by any Publishers
         * This should be called before calling Vungle.init()
         */
        @JvmStatic
        fun setIntegrationName(
            wrapperFramework: WrapperFramework,
            wrapperFrameworkVersion: String
        ) {
            //TODO move to VungleInitializer
            if (wrapperFramework != WrapperFramework.none) {
                VungleApiClient.WRAPPER_FRAMEWORK_SELECTED = wrapperFramework

                val originalHeader: String = VungleApiClient.headerUa
                val wrapperVersion =
                    if (wrapperFrameworkVersion.isNotEmpty()) "/$wrapperFrameworkVersion" else ""
                val newMediation: String = wrapperFramework.toString() + wrapperVersion
                val headers = originalHeader.split(";")
                val set = HashSet(headers)
                if (set.add(newMediation)) {
                    VungleApiClient.headerUa = "$originalHeader;$newMediation"
                }
            } else {
                Log.e(TAG, "Wrapper is null or is none")
            }
            if (isInitialized()) {
                Log.w(
                    TAG,
                    "VUNGLE WARNING: SDK already initialized, you should've set wrapper info before"
                )
            }
        }

        internal fun deInit() {
            initializer.deInit()
        }
    }

    @Keep
    enum class WrapperFramework {
        admob,
        air,
        cocos2dx,
        corona,
        dfp,
        heyzap,
        marmalade,
        mopub,
        unity,
        fyber,
        ironsource,
        upsight,
        appodeal,
        aerserv,
        adtoapp,
        tapdaq,
        vunglehbs,
        max,
        none
    }

}
