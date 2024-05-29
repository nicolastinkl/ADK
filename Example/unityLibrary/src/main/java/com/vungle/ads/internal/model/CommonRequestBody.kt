package com.vungle.ads.internal.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CommonRequestBody(
    val device: DeviceNode,
    val app: AppNode? = null,
    val user: User? = null,
    var ext: RequestExt? = null,
    var request: RequestParam? = null
) {

    @Serializable
    data class User(var gdpr: GDPR? = null, var ccpa: CCPA? = null, var coppa: COPPA? = null)

    @Serializable
    data class GDPR(
        @SerialName("consent_status") val consentStatus: String,
        @SerialName("consent_source") val consentSource: String,
        @SerialName("consent_timestamp") val consentTimestamp: Long,
        @SerialName("consent_message_version") val consentMessageVersion: String
    )

    @Serializable
    data class CCPA(val status: String)

    @Serializable
    data class COPPA(@SerialName("is_coppa") val isCoppa: Boolean?)

    @Serializable
    data class RequestExt(
        @SerialName("config_extension") val configExtension: String? = null,
        val adExt: String? = null
    )

    @Serializable
    data class RequestParam(
        //ads
        val placements: List<String>? = null,
        @SerialName("header_bidding") val isHeaderBidding: Boolean? = null,
        @SerialName("ad_size") var adSize: String? = null,

        //ri
        val adStartTime: Long? = null,
        @SerialName("app_id") val appId: String? = null,
        @SerialName("placement_reference_id") val placementReferenceId: String? = null,
        val user: String? = null
    )
}
