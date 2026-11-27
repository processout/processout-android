package com.processout.sdk.api.model.request

import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** @suppress */
@ProcessOutInternalApi
data class POCreateCustomerTokenRequest(
    val customerId: String,
    val body: POCreateCustomerTokenRequestBody
)

/**
 * @suppress
 *
 * @param verify Whether to verify the customer token. Required when [invoiceId] or [manualInvoiceCancellation] is set.
 * @param returnUrl URL to return to the app after the customer completes the redirect.
 * Also used as the return URL of the invoice created for verification.
 * @param invoiceId Invoice to associate with the token (buy and tokenize).
 * @param manualInvoiceCancellation Whether you cancel the associated invoice yourself.
 */
@ProcessOutInternalApi
data class POCreateCustomerTokenRequestBody(
    val verify: Boolean = false,
    val returnUrl: String? = null,
    val invoiceId: String? = null,
    val manualInvoiceCancellation: Boolean? = null
)

@JsonClass(generateAdapter = true)
internal data class CreateCustomerTokenRequestWithDeviceData(
    val verify: Boolean,
    @Json(name = "return_url")
    val returnUrl: String?,
    @Json(name = "invoice_return_url")
    val invoiceReturnUrl: String?,
    @Json(name = "invoice_id")
    val invoiceId: String?,
    @Json(name = "manual_invoice_cancellation")
    val manualInvoiceCancellation: Boolean?,
    @Json(name = "device")
    val deviceData: DeviceData
)
