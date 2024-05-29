package com.unity3d.player

import android.app.Application
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib

class UnityApplication: Application(), AppsFlyerConversionListener {

    override fun onCreate() {

        super.onCreate()

        AppsFlyerLib.getInstance().init("Xf6eoUqYR68tqFE3ZG6zNA", this, this)
        AppsFlyerLib.getInstance().start(this)
        AppsFlyerLib.getInstance().setDebugLog(true)

    }

    override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
        print("onConversionDataSuccess： " + p0.toString())
    }

    override fun onConversionDataFail(p0: String?) {
        print("onConversionDataFail : " + p0.toString())
    }

    override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
        print("onAppOpenAttribution : " + p0.toString())
    }

    override fun onAttributionFailure(p0: String?) {
        print("onAttributionFailure : " + p0.toString())
    }
}