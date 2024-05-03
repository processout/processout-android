package com.processout.sdk.api.dispatcher.napm

import com.processout.sdk.api.dispatcher.PONativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodDefaultValuesRequest
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodDefaultValuesResponse
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** @suppress */
@ProcessOutInternalApi
object PODefaultNativeAlternativePaymentMethodEventDispatcher : PONativeAlternativePaymentMethodEventDispatcher {

    private val _events = MutableSharedFlow<PONativeAlternativePaymentMethodEvent>()
    override val events = _events.asSharedFlow()

    private val _defaultValuesRequest = MutableSharedFlow<PONativeAlternativePaymentMethodDefaultValuesRequest>()
    override val defaultValuesRequest = _defaultValuesRequest.asSharedFlow()

    private val _defaultValuesResponse = MutableSharedFlow<PONativeAlternativePaymentMethodDefaultValuesResponse>()
    val defaultValuesResponse = _defaultValuesResponse.asSharedFlow()

    suspend fun send(event: PONativeAlternativePaymentMethodEvent) {
        _events.emit(event)
    }

    suspend fun send(request: PONativeAlternativePaymentMethodDefaultValuesRequest) {
        _defaultValuesRequest.emit(request)
    }

    override suspend fun provideDefaultValues(response: PONativeAlternativePaymentMethodDefaultValuesResponse) {
        _defaultValuesResponse.emit(response)
    }

    fun subscribedForDefaultValuesRequest() = _defaultValuesRequest.subscriptionCount.value > 0
}
