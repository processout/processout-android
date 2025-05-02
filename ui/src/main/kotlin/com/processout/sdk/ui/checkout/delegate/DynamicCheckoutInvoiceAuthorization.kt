package com.processout.sdk.ui.checkout.delegate

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.request.POInvoiceAuthorizationRequest
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod
import java.util.UUID

internal data class DynamicCheckoutInvoiceAuthorizationRequest(
    override val uuid: UUID = UUID.randomUUID(),
    val paymentMethod: PODynamicCheckoutPaymentMethod,
    val request: POInvoiceAuthorizationRequest
) : POEventDispatcher.Request

internal data class DynamicCheckoutInvoiceAuthorizationResponse(
    override val uuid: UUID,
    val request: POInvoiceAuthorizationRequest
) : POEventDispatcher.Response

internal fun DynamicCheckoutInvoiceAuthorizationRequest.toResponse(
    request: POInvoiceAuthorizationRequest
) = DynamicCheckoutInvoiceAuthorizationResponse(uuid, request)
