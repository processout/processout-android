package com.processout.sdk.api.model.response.napm.v2

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class NativeAlternativePaymentAuthorizationResponseBody(
    val state: State
) {

    @JsonClass(generateAdapter = false)
    enum class State {
        NEXT_STEP_REQUIRED,
        PENDING_CAPTURE,
        CAPTURED,
        FAILED
    }
}
