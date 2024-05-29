package com.vungle.ads.internal.privacy

enum class COPPA(private val value: Boolean?) {
    COPPA_ENABLED(true), COPPA_DISABLED(false), COPPA_NOTSET(null);

    fun getValue(): Boolean? {
        return value
    }
}