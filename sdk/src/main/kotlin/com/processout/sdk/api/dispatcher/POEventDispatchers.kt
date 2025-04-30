package com.processout.sdk.api.dispatcher

import com.processout.sdk.api.dispatcher.card.tokenization.POCardTokenizationEventDispatcher
import com.processout.sdk.api.dispatcher.card.update.POCardUpdateEventDispatcher

/**
 * Dispatchers that allows to handle events during various payment flows.
 */
interface POEventDispatchers {

    /** Dispatcher that allows to handle events during native alternative payments. */
    val nativeAlternativePaymentMethod: PONativeAlternativePaymentMethodEventDispatcher

    /** Dispatcher that allows to handle events during card tokenization. */
    @Deprecated(message = "Use API with POCardTokenizationDelegate instead.")
    val cardTokenization: POCardTokenizationEventDispatcher

    /** Dispatcher that allows to handle events during card updates. */
    @Deprecated(message = "Use API with POCardUpdateDelegate instead.")
    val cardUpdate: POCardUpdateEventDispatcher
}
