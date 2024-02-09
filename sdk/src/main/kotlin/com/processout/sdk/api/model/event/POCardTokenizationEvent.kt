package com.processout.sdk.api.model.event

import com.processout.sdk.core.annotation.ProcessOutInternalApi

/**
 * Defines card tokenization lifecycle events.
 */
/** @suppress */
@ProcessOutInternalApi
sealed class POCardTokenizationEvent {

    /**
     * Event indicates that initialization is complete.
     * Currently waiting for user input.
     *
     * @param[restored] Indicates whether it's a fresh start or if the flow has been restored.
     */
    data class DidStart(val restored: Boolean) : POCardTokenizationEvent()

    /**
     * Event is sent when user changes any editable value.
     */
    data object ParametersChanged : POCardTokenizationEvent()

    /**
     * Event is sent just before submitting user input.
     * This is usually a result of a user action, e.g. button press.
     */
    data object WillTokenizeCard : POCardTokenizationEvent()

    /**
     * Event is sent when card is tokenized. This is a final event.
     */
    data object DidComplete : POCardTokenizationEvent()
}
