package com.processout.sdk.api.model.event

import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.core.annotation.ProcessOutInternalApi

/**
 * Defines card tokenization lifecycle events.
 */
/** @suppress */
@ProcessOutInternalApi
sealed class POCardTokenizationEvent {

    /**
     * Initial event that is sent prior any other event.
     */
    data object WillStart : POCardTokenizationEvent()

    /**
     * Event indicates that initialization is complete.
     * Currently waiting for user input.
     */
    data object DidStart : POCardTokenizationEvent()

    /**
     * Event is sent when user changes any editable value.
     */
    data object ParametersChanged : POCardTokenizationEvent()

    /**
     * Event is sent just before submitting user input.
     * This is usually a result of a user action, e.g. button press.
     */
    data object WillTokenize : POCardTokenizationEvent()

    /**
     * Event is sent when card is tokenized.
     */
    data class DidTokenize(val card: POCard) : POCardTokenizationEvent()

    /**
     * Event is sent when tokenized card has been processed, e.g. authorized.
     * This is a final event.
     */
    data object DidComplete : POCardTokenizationEvent()
}
