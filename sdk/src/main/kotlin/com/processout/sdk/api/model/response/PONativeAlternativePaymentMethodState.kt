package com.processout.sdk.api.model.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class PONativeAlternativePaymentMethodState {
    CUSTOMER_INPUT,
    PENDING_CAPTURE,
    CAPTURED
}
