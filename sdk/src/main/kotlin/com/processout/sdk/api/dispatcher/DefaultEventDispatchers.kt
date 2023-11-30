package com.processout.sdk.api.dispatcher

import com.processout.sdk.api.dispatcher.card.update.DefaultCardUpdateEventDispatcher
import com.processout.sdk.api.dispatcher.card.update.POCardUpdateEventDispatcher
import com.processout.sdk.api.dispatcher.nativeapm.DefaultNativeAlternativePaymentMethodEventDispatcher

internal object DefaultEventDispatchers : POEventDispatchers {

    override val cardUpdate: POCardUpdateEventDispatcher = DefaultCardUpdateEventDispatcher

    override val nativeAlternativePaymentMethod: PONativeAlternativePaymentMethodEventDispatcher =
        DefaultNativeAlternativePaymentMethodEventDispatcher
}
