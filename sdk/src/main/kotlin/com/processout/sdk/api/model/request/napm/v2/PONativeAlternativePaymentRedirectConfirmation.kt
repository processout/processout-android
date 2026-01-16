package com.processout.sdk.api.model.request.napm.v2

import com.squareup.moshi.JsonClass

/**
 * Specifies native alternative payment redirect confirmation.
 *
 * @param[success] Indicates whether the redirection was successful.
 */
@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentRedirectConfirmation(
    val success: Boolean
)
