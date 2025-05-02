package com.processout.sdk.ui.checkout.delegate

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.api.model.response.POInvoice
import java.util.UUID

internal data class DynamicCheckoutInvoiceRequest(
    override val uuid: UUID = UUID.randomUUID(),
    val currentInvoice: POInvoice,
    val invalidationReason: PODynamicCheckoutInvoiceInvalidationReason
) : POEventDispatcher.Request

internal data class DynamicCheckoutInvoiceResponse(
    override val uuid: UUID,
    val invoiceRequest: POInvoiceRequest?,
    val invalidationReason: PODynamicCheckoutInvoiceInvalidationReason
) : POEventDispatcher.Response

internal fun DynamicCheckoutInvoiceRequest.toResponse(
    invoiceRequest: POInvoiceRequest?
) = DynamicCheckoutInvoiceResponse(uuid, invoiceRequest, invalidationReason)
