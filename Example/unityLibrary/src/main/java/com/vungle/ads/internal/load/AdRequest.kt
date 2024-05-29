package com.vungle.ads.internal.load

import com.vungle.ads.internal.model.BidPayload
import com.vungle.ads.internal.model.Placement
import java.io.Serializable

class AdRequest(val placement: Placement, val adMarkup: BidPayload?, val requestAdSize: String) : Serializable {
    override fun toString(): String {
        return "AdRequest{" +
                "placementId='" + placement.referenceId + '\'' +
                ", adMarkup=" + adMarkup +
                ", requestAdSize=" + requestAdSize +
                '}'
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val request = other as AdRequest
        if (placement.referenceId != request.placement.referenceId) return false
        if (requestAdSize != request.requestAdSize) return false
        return if (adMarkup != null) adMarkup == request.adMarkup else request.adMarkup == null
    }

    override fun hashCode(): Int {
        var result = placement.referenceId.hashCode()
        result = 31 * result + requestAdSize.hashCode()
        result = 31 * result + (adMarkup?.hashCode() ?: 0)
        return result
    }
}
