package com.processout.sdk.api.dispatcher

import com.processout.sdk.api.dispatcher.card.update.POCardUpdateEventDispatcher
import com.processout.sdk.api.dispatcher.card.update.PODefaultCardUpdateEventDispatcher
import com.processout.sdk.api.dispatcher.nativeapm.DefaultNativeAlternativePaymentMethodEventDispatcher

internal object DefaultEventDispatchers : POEventDispatchers {

    override val cardUpdate: POCardUpdateEventDispatcher by lazy {
        PODefaultCardUpdateEventDispatcher
    }

    override val nativeAlternativePaymentMethod: PONativeAlternativePaymentMethodEventDispatcher by lazy {
        DefaultNativeAlternativePaymentMethodEventDispatcher
    }
}
