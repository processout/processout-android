package com.processout.sdk.api.dispatcher

import com.processout.sdk.api.dispatcher.card.tokenization.POCardTokenizationEventDispatcher
import com.processout.sdk.api.dispatcher.card.update.POCardUpdateEventDispatcher
import com.processout.sdk.core.annotation.ProcessOutInternalApi

/**
 * Dispatchers that allows to handle events during various payment flows.
 */
interface POEventDispatchers {

    /** Dispatcher that allows to handle events during card updates. */
    val cardUpdate: POCardUpdateEventDispatcher

    /** Dispatcher that allows to handle events during card tokenization. */
    /** @suppress */
    @ProcessOutInternalApi
    val cardTokenization: POCardTokenizationEventDispatcher

    /** Dispatcher that allows to handle events during native alternative payments. */
    val nativeAlternativePaymentMethod: PONativeAlternativePaymentMethodEventDispatcher
}
