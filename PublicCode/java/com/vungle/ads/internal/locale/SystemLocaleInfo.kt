package com.vungle.ads.internal.locale

import java.util.*

class SystemLocaleInfo : LocaleInfo {
    override val timeZoneId: String
        get() = TimeZone.getDefault().id
    override val language: String
        get() = Locale.getDefault().language
}