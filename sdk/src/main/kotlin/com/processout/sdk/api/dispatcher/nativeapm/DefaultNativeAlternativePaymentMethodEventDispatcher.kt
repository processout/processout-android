package com.processout.sdk.api.dispatcher.nativeapm

import com.processout.sdk.api.dispatcher.PONativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodDefaultValuesRequest
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodDefaultValuesResponse
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal object DefaultNativeAlternativePaymentMethodEventDispatcher
    : PONativeAlternativePaymentMethodEventDispatcher() {

    private val _events = MutableSharedFlow<PONativeAlternativePaymentMethodEvent>()
    override val events = _events.asSharedFlow()

    private val _defaultValuesRequest = MutableSharedFlow<PONativeAlternativePaymentMethodDefaultValuesRequest>()
    override val defaultValuesRequest = _defaultValuesRequest.asSharedFlow()

    private val _defaultValuesResponse = MutableSharedFlow<PONativeAlternativePaymentMethodDefaultValuesResponse>()
    override val defaultValuesResponse = _defaultValuesResponse.asSharedFlow()

    override suspend fun send(event: PONativeAlternativePaymentMethodEvent) {
        _events.emit(event)
    }

    override suspend fun send(request: PONativeAlternativePaymentMethodDefaultValuesRequest) {
        _defaultValuesRequest.emit(request)
    }

    override suspend fun provideDefaultValues(response: PONativeAlternativePaymentMethodDefaultValuesResponse) {
        _defaultValuesResponse.emit(response)
    }

    override fun subscribedForDefaultValuesRequest() = _defaultValuesRequest.subscriptionCount.value > 0
}
