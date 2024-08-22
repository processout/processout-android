package com.processout.sdk.api.model.request

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.response.POInvoice
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import java.util.UUID

/** @suppress */
@ProcessOutInternalApi
data class PODynamicCheckoutInvoiceRequest(
    override val uuid: UUID = UUID.randomUUID(),
    val currentInvoice: POInvoice,
    val invalidationReason: PODynamicCheckoutInvoiceInvalidationReason
) : POEventDispatcher.Request
