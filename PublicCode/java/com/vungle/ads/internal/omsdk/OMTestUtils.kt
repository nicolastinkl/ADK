package com.vungle.ads.internal.omsdk


import androidx.annotation.VisibleForTesting
import com.iab.omid.library.vungle.Omid

internal object OMTestUtils {
    @get:VisibleForTesting
    val isOmidActive: Boolean
        get() = Omid.isActive()
}