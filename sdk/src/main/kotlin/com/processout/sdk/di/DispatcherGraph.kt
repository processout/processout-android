package com.processout.sdk.di

import com.processout.sdk.api.dispatcher.PONativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.dispatcher.NativeAlternativePaymentMethodEventDispatcherImpl

internal interface DispatcherGraph {
    val nativeAlternativePaymentMethodEventDispatcher: PONativeAlternativePaymentMethodEventDispatcher
}

internal class DispatcherGraphImpl : DispatcherGraph {
    override val nativeAlternativePaymentMethodEventDispatcher =
        NativeAlternativePaymentMethodEventDispatcherImpl
}
