package com.processout.sdk.api.model.response

import com.squareup.moshi.JsonClass

/**
 * Defines the state of native alternative payment.
 */
@JsonClass(generateAdapter = false)
enum class PONativeAlternativePaymentMethodState {
    /** Customer input is required. */
    CUSTOMER_INPUT,

    /** Invoice is pending for capture. */
    PENDING_CAPTURE,

    /** Invoice is captured. */
    CAPTURED
}
