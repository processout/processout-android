package com.processout.sdk.api.model.request.napm.v2

import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentRequestConfiguration.ReturnRedirectType.AUTOMATIC
import com.squareup.moshi.JsonClass

/**
 * Payment configuration.
 *
 * @param[returnRedirectType] Return redirect type. By default [AUTOMATIC].
 */
data class PONativeAlternativePaymentRequestConfiguration(
    val returnRedirectType: ReturnRedirectType = AUTOMATIC
) {

    /**
     * Return redirect type.
     */
    @JsonClass(generateAdapter = false)
    enum class ReturnRedirectType(val rawValue: String) {
        /** Redirect result is handled automatically. */
        AUTOMATIC("automatic"),

        /** Redirect result is not processed automatically and should be resolved explicitly. */
        MANUAL("manual")
    }
}
