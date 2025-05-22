package com.processout.sdk.api.model.response.napm.v2

import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse.State
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Specifies details of native alternative payment.
 *
 * @param[state] State of native alternative payment.
 * @param[nextStep] Next required step in the payment flow.
 * @param[customerInstructions] Instructions for the customer that provide additional information and/or describe required actions.
 */
/** @suppress */
@ProcessOutInternalApi
data class PONativeAlternativePaymentAuthorizationResponse(
    val state: State,
    val nextStep: PONativeAlternativePaymentNextStep?,
    val customerInstructions: List<PONativeAlternativePaymentCustomerInstruction>?
) {

    /**
     * State of native alternative payment.
     */
    @JsonClass(generateAdapter = false)
    enum class State {
        /** Next step is required to proceed. */
        NEXT_STEP_REQUIRED,

        /** Payment is ready to be captured. */
        PENDING_CAPTURE,

        /** Payment is captured. */
        CAPTURED,

        /**
         * Placeholder that allows adding additional cases while staying backward compatible.
         * __Warning:__ Do not match this case directly, use _when-else_ instead.
         */
        @ProcessOutInternalApi
        UNKNOWN
    }
}

@JsonClass(generateAdapter = true)
internal data class NativeAlternativePaymentAuthorizationResponseBody(
    val state: State,
    @Json(name = "next_step")
    val nextStep: NativeAlternativePaymentNextStep?,
    @Json(name = "customer_instructions")
    val customerInstructions: List<PONativeAlternativePaymentCustomerInstruction>?
)
