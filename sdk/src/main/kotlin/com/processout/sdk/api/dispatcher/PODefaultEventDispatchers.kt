package com.processout.sdk.api.dispatcher

import com.processout.sdk.api.dispatcher.card.tokenization.POCardTokenizationEventDispatcher
import com.processout.sdk.api.dispatcher.card.tokenization.PODefaultCardTokenizationEventDispatcher
import com.processout.sdk.api.dispatcher.card.update.POCardUpdateEventDispatcher
import com.processout.sdk.api.dispatcher.card.update.PODefaultCardUpdateEventDispatcher
import com.processout.sdk.api.dispatcher.napm.PODefaultNativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.core.annotation.ProcessOutInternalApi

/** @suppress */
@ProcessOutInternalApi
object PODefaultEventDispatchers : POEventDispatchers {

    val defaultNativeAlternativePaymentMethod by lazy {
        PODefaultNativeAlternativePaymentMethodEventDispatcher()
    }
    override val nativeAlternativePaymentMethod: PONativeAlternativePaymentMethodEventDispatcher =
        defaultNativeAlternativePaymentMethod

    override val cardTokenization: POCardTokenizationEventDispatcher by lazy {
        PODefaultCardTokenizationEventDispatcher
    }

    override val cardUpdate: POCardUpdateEventDispatcher by lazy {
        PODefaultCardUpdateEventDispatcher
    }
}
