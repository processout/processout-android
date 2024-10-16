package com.processout.sdk.api.model.request

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import java.util.UUID

/** @suppress */
@ProcessOutInternalApi
data class PODynamicCheckoutInvoiceAuthorizationRequest(
    override val uuid: UUID = UUID.randomUUID(),
    val request: POInvoiceAuthorizationRequest,
    val paymentMethod: PODynamicCheckoutPaymentMethod
) : POEventDispatcher.Request
