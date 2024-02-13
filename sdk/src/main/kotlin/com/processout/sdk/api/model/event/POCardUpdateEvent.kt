package com.processout.sdk.api.model.event

/**
 * Defines card update lifecycle events.
 */
sealed class POCardUpdateEvent {

    /**
     * Event indicates that initialization is complete.
     * Currently waiting for user input.
     */
    data object DidStart : POCardUpdateEvent()

    /**
     * Event is sent when user changes any editable value.
     */
    data object ParametersChanged : POCardUpdateEvent()

    /**
     * Event is sent just before submitting user input.
     * This is usually a result of a user action, e.g. button press.
     */
    data object WillUpdateCard : POCardUpdateEvent()

    /**
     * Event is sent when card is updated. This is a final event.
     */
    data object DidComplete : POCardUpdateEvent()
}
