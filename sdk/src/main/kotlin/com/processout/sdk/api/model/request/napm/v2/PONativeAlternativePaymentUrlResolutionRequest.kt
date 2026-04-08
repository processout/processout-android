package com.processout.sdk.api.model.request.napm.v2

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.JsonClass

/**
 * Request parameters for redirect URL resolution during native alternative payment.
 *
 * @param[redirect] Redirect information.
 */
@ProcessOutInternalApi
@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentUrlResolutionRequest(
    val redirect: Redirect
) {

    /**
     * Redirect information.
     *
     * @param[result] Redirect result.
     */
    @JsonClass(generateAdapter = true)
    data class Redirect(
        val result: Result
    ) {

        /**
         * Redirect result.
         *
         * @param[url] Result URL.
         */
        @JsonClass(generateAdapter = true)
        data class Result(
            val url: String
        )
    }
}
