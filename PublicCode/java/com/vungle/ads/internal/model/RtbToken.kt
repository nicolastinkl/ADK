package com.vungle.ads.internal.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RtbToken(
    val device: DeviceNode,
    val user: CommonRequestBody.User? = null,
    val ext: CommonRequestBody.RequestExt? = null,
    val request: RtbRequest? = null,
    @SerialName("ordinal_view")
    val ordinalView: Int
)

@Serializable
data class RtbRequest(
    @SerialName("sdk_user_agent")
    val sdkUserAgent: String? = null
)