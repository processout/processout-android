package com.processout.sdk.api.model.response

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.processout.sdk.core.util.findBy
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class InvoiceAuthorizationResponse(
    @Json(name = "outcome")
    val rawOutcome: String,
    @Json(name = "customer_action")
    val customerAction: CustomerAction?,
    @Json(name = "customer_token_id")
    val customerTokenId: String?
) {

    val outcome: POInvoiceAuthorizationOutcome
        get() = POInvoiceAuthorizationOutcome::rawValue.findBy(rawOutcome) ?: POInvoiceAuthorizationOutcome.UNKNOWN
}

/**
 * Invoice authorization response.
 *
 * @param[outcome] Invoice authorization outcome.
 * @param[customerTokenId] Optional customer token ID.
 * Available if invoice was authorized with `saveSource` flag set to `true` and implementation was able to tokenize the source.
 */
data class POInvoiceAuthorizationResponse(
    val outcome: POInvoiceAuthorizationOutcome,
    val customerTokenId: String?
)

/**
 * Invoice authorization outcome determined by the request and the current transaction status.
 */
@JsonClass(generateAdapter = false)
enum class POInvoiceAuthorizationOutcome(val rawValue: String) {
    /** Pending operation. */
    PENDING("pending"),

    /** Successful operation. */
    SUCCESS("success"),

    /**
     * Placeholder that allows adding additional cases while staying backward compatible.
     * __Warning:__ Do not match this case directly, use _when-else_ instead.
     */
    @ProcessOutInternalApi
    UNKNOWN(String())
}
