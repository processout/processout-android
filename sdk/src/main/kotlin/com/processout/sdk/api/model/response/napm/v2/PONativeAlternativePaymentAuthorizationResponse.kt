package com.processout.sdk.api.model.response.napm.v2

import com.processout.sdk.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
data class PONativeAlternativePaymentAuthorizationResponse(
    val state: State
) {

    enum class State {
        NEXT_STEP_REQUIRED,
        PENDING_CAPTURE,
        CAPTURED
    }
}
