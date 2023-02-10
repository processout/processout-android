package com.processout.sdk.api.dispatcher

import kotlinx.coroutines.flow.SharedFlow

abstract class NativeAlternativePaymentMethodEventDispatcher {
    abstract val events: SharedFlow<PONativeAlternativePaymentMethodEvent>
    internal abstract suspend fun send(event: PONativeAlternativePaymentMethodEvent)
}
