package com.vungle.ads.internal.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppNode(val bundle: String, val ver: String, @SerialName("id") val appId: String)
