package com.vungle.ads.internal.ui

import androidx.annotation.VisibleForTesting


class VungleActivity : AdActivity() {
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    override fun canRotate(): Boolean {
        return true
    }
}