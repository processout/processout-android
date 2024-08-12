package com.processout.sdk.api.model.response

import com.processout.sdk.api.model.request.PODynamicCheckoutInvoiceInvalidationReason
import com.processout.sdk.api.model.request.PODynamicCheckoutInvoiceRequest
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import java.util.UUID

/** @suppress */
@ProcessOutInternalApi
data class PODynamicCheckoutInvoiceResponse internal constructor(
    val uuid: UUID = UUID.randomUUID(),
    val invoiceRequest: POInvoiceRequest?,
    val reason: PODynamicCheckoutInvoiceInvalidationReason
)

/** @suppress */
@ProcessOutInternalApi
fun PODynamicCheckoutInvoiceRequest.toResponse(
    invoiceRequest: POInvoiceRequest?
) = PODynamicCheckoutInvoiceResponse(uuid, invoiceRequest, reason)
