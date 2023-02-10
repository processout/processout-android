package com.processout.sdk.api.dispatcher

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal object NativeAlternativePaymentMethodEventDispatcherImpl
    : NativeAlternativePaymentMethodEventDispatcher() {

    private val emitter = MutableSharedFlow<PONativeAlternativePaymentMethodEvent>()
    override val events = emitter.asSharedFlow()

    override suspend fun send(event: PONativeAlternativePaymentMethodEvent) {
        emitter.emit(event)
    }
}
