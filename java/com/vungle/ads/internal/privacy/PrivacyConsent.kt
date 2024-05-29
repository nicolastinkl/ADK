package com.vungle.ads.internal.privacy

enum class PrivacyConsent(private val value: String) {
    OPT_IN("opted_in"),
    OPT_OUT("opted_out");

    fun getValue(): String {
        return value
    }
}