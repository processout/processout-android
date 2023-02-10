package com.processout.sdk.di

import com.processout.sdk.api.dispatcher.NativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.dispatcher.NativeAlternativePaymentMethodEventDispatcherImpl

internal interface DispatcherGraph {
    val nativeAlternativePaymentMethodEventDispatcher: NativeAlternativePaymentMethodEventDispatcher
}

internal class DispatcherGraphImpl : DispatcherGraph {
    override val nativeAlternativePaymentMethodEventDispatcher =
        NativeAlternativePaymentMethodEventDispatcherImpl
}
