package com.processout.sdk.api.model.response.napm.v2

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.JsonClass

/**
 * State of native alternative payment.
 */
@JsonClass(generateAdapter = false)
enum class PONativeAlternativePaymentState {
    /** Next step is required to proceed. */
    NEXT_STEP_REQUIRED,

    /** Pending payment processing. */
    PENDING,

    /** Successful payment processing. */
    SUCCESS,

    /**
     * Placeholder that allows adding additional cases while staying backward compatible.
     * __Warning:__ Do not match this case directly, use _when-else_ instead.
     */
    @ProcessOutInternalApi
    UNKNOWN
}
