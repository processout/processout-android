package com.processout.sdk.api.model.response.napm.v2

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.JsonClass

/**
 * Specifies native alternative payment redirect parameters.
 *
 * @param[url] Redirect URL.
 * @param[hint] A hint or description associated with the redirect URL.
 */
/** @suppress */
@ProcessOutInternalApi
@JsonClass(generateAdapter = true)
data class PONativeAlternativePaymentRedirect(
    val url: String,
    val hint: String
)
