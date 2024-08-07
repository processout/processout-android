package com.processout.sdk.api.dispatcher.checkout

import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent
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

    // Events

    suspend fun send(event: POCardTokenizationEvent) {
        _cardTokenizationEvents.emit(event)
    }

    suspend fun send(event: PONativeAlternativePaymentMethodEvent) {
        _nativeAlternativePaymentEvents.emit(event)
    }
}
