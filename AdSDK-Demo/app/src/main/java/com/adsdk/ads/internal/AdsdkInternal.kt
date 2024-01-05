package com.adsdk.ads.internal

import android.content.Context

internal class AdsdkInternal {


    /*
    *    Will return an encoded string of advertisement bid tokens.
    * */
    fun getAvailableBidTokens(context: Context): String? {
        return "Token"
    }
    fun getSdkVersion() = BuildConfig.VERSION_NAME
}