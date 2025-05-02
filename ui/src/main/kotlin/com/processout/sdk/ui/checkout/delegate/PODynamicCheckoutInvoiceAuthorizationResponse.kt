package com.processout.sdk.ui.checkout.delegate

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.request.POInvoiceAuthorizationRequest
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import java.util.UUID

/** @suppress */
@ProcessOutInternalApi
data class PODynamicCheckoutInvoiceAuthorizationResponse internal constructor(
    override val uuid: UUID,
    val request: POInvoiceAuthorizationRequest
) : POEventDispatcher.Response

/** @suppress */
@ProcessOutInternalApi
fun PODynamicCheckoutInvoiceAuthorizationRequest.toResponse(
    request: POInvoiceAuthorizationRequest
) = PODynamicCheckoutInvoiceAuthorizationResponse(uuid, request)
