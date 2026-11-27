package com.processout.sdk.api.model.request.napm.v2

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.JsonClass

/**
 * Specifies native alternative payment redirect confirmation.
 *
 * @param[success] Indicates whether the redirection was successful.
 */
@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentRedirectConfirmation(
    val success: Boolean,
    @property:ProcessOutInternalApi
    val result: Result? = null
) {

    /**
     * Redirect result.
     *
     * @param[url] Return URL that customer was redirected to.
     */
    @ProcessOutInternalApi
    @JsonClass(generateAdapter = true)
    data class Result(
        val url: String
    )
}
