package com.processout.sdk.ui.card.update.delegate

import com.processout.sdk.api.model.event.POCardUpdateEvent
import com.processout.sdk.core.ProcessOutResult

/**
 * Delegate that allows to handle events during card update.
 */
interface POCardUpdateDelegate {

    /**
     * Invoked on card update lifecycle events.
     */
    fun onEvent(event: POCardUpdateEvent) {}

    /**
     * Allows to decide whether the card update should continue or complete after the failure.
     * Returns _true_ by default.
     */
    fun shouldContinue(
        failure: ProcessOutResult.Failure
    ): Boolean = true
}
