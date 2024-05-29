package com.vungle.ads.internal.presenter

interface NativePresenterDelegate {
    fun getPlacementRefId(): String?
    fun getImpressionUrls(): List<String>?
}
