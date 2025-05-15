package com.processout.sdk.api.model.response.napm.v2

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.JsonClass

/** @suppress */
@ProcessOutInternalApi
@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentAuthorizationResponse(
    val state: State
) {

    @JsonClass(generateAdapter = false)
    enum class State {
        NEXT_STEP_REQUIRED,
        PENDING_CAPTURE,
        CAPTURED
    }
}
