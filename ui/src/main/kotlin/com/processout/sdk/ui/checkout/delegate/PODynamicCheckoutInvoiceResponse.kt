package com.processout.sdk.ui.checkout.delegate

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import java.util.UUID

/** @suppress */
@ProcessOutInternalApi
data class PODynamicCheckoutInvoiceResponse internal constructor(
    override val uuid: UUID,
    val invoiceRequest: POInvoiceRequest?,
    val invalidationReason: PODynamicCheckoutInvoiceInvalidationReason
) : POEventDispatcher.Response

/** @suppress */
@ProcessOutInternalApi
fun PODynamicCheckoutInvoiceRequest.toResponse(
    invoiceRequest: POInvoiceRequest?
) = PODynamicCheckoutInvoiceResponse(uuid, invoiceRequest, invalidationReason)
