package com.processout.sdk.ui.card.tokenization

import com.processout.sdk.api.model.event.POCardTokenizationEvent

/**
 * Delegate that allows to handle events during card tokenization.
 */
interface POCardTokenizationDelegate {

    /**
     * Invoked on card tokenization lifecycle events.
     */
    fun onEvent(event: POCardTokenizationEvent) {}
}
