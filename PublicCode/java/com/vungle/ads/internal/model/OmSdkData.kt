package com.vungle.ads.internal.model

import kotlinx.serialization.Serializable

@Serializable
data class OmSdkData(
    val params: String? = null,
    val vendorKey: String? = null,
    val vendorURL: String? = null
)
