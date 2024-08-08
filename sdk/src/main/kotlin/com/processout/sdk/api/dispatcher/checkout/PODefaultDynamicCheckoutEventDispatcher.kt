package com.processout.sdk.api.dispatcher.checkout

import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent
import com.processout.sdk.api.model.request.PODynamicCheckoutInvoiceRequest
import com.processout.sdk.api.model.response.PODynamicCheckoutInvoiceResponse
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** @suppress */
@ProcessOutInternalApi
object PODefaultDynamicCheckoutEventDispatcher : PODynamicCheckoutEventDispatcher {

    private val _cardTokenizationEvents = MutableSharedFlow<POCardTokenizationEvent>()
    override val cardTokenizationEvents = _cardTokenizationEvents.asSharedFlow()

    private val _nativeAlternativePaymentEvents = MutableSharedFlow<PONativeAlternativePaymentMethodEvent>()
    override val nativeAlternativePaymentEvents = _nativeAlternativePaymentEvents.asSharedFlow()

    private val _invoiceRequest = MutableSharedFlow<PODynamicCheckoutInvoiceRequest>()
    override val invoiceRequest = _invoiceRequest.asSharedFlow()

    private val _invoiceResponse = MutableSharedFlow<PODynamicCheckoutInvoiceResponse>()
    val invoiceResponse = _invoiceResponse.asSharedFlow()

    // Events

    suspend fun send(event: POCardTokenizationEvent) {
        _cardTokenizationEvents.emit(event)
    }

    suspend fun send(event: PONativeAlternativePaymentMethodEvent) {
        _nativeAlternativePaymentEvents.emit(event)
    }

    // Invoice

    suspend fun send(request: PODynamicCheckoutInvoiceRequest) {
        _invoiceRequest.emit(request)
    }

    override suspend fun replaceInvoice(response: PODynamicCheckoutInvoiceResponse) {
        _invoiceResponse.emit(response)
    }

    fun subscribedForInvoiceRequest() = _invoiceRequest.subscriptionCount.value > 0
}
