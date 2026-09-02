package com.processout.sdk.api.model.response.napm.v2

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.JsonClass

/**
 * Available action to advance the payment.
 */
@JsonClass(generateAdapter = false)
enum class PONativeAlternativePaymentAvailableAction {

    /** Authorization is possible. */
    AUTHORIZE,

    /** Capture is possible. */
    CAPTURE,

    /**
     * Placeholder that allows adding additional cases while staying backward compatible.
     * __Warning:__ Do not match this case directly, use _when-else_ instead.
     */
    @ProcessOutInternalApi
    UNKNOWN
}
