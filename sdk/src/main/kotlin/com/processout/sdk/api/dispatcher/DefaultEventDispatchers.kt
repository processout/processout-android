package com.processout.sdk.api.dispatcher

import com.processout.sdk.api.dispatcher.card.tokenization.POCardTokenizationEventDispatcher
import com.processout.sdk.api.dispatcher.card.tokenization.PODefaultCardTokenizationEventDispatcher
import com.processout.sdk.api.dispatcher.card.update.POCardUpdateEventDispatcher
import com.processout.sdk.api.dispatcher.card.update.PODefaultCardUpdateEventDispatcher
import com.processout.sdk.api.dispatcher.napm.PODefaultNativeAlternativePaymentMethodEventDispatcher

internal object DefaultEventDispatchers : POEventDispatchers {

    override val cardUpdate: POCardUpdateEventDispatcher by lazy {
        PODefaultCardUpdateEventDispatcher
    }

    override val cardTokenization: POCardTokenizationEventDispatcher by lazy {
        PODefaultCardTokenizationEventDispatcher
    }

    override val nativeAlternativePaymentMethod: PONativeAlternativePaymentMethodEventDispatcher by lazy {
        PODefaultNativeAlternativePaymentMethodEventDispatcher
    }
}
