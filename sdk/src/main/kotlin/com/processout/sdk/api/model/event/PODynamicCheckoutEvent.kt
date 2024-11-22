package com.processout.sdk.api.model.event

import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod
import com.processout.sdk.core.ProcessOutResult

/**
 * Defines dynamic checkout lifecycle events.
 */
sealed class PODynamicCheckoutEvent {

    /**
     * Initial event that is sent prior any other event.
     */
    data object WillStart : PODynamicCheckoutEvent()

    /**
     * Event indicates that initialization is complete.
     * Currently waiting for user input.
     */
    data object DidStart : PODynamicCheckoutEvent()

    /**
     * Event is sent when user asked to confirm cancellation, e.g. via dialog.
     */
    data object DidRequestCancelConfirmation : PODynamicCheckoutEvent()

    /**
     * Event is sent when payment method is selected by the user.
     */
    data class DidSelectPaymentMethod(
        val paymentMethod: PODynamicCheckoutPaymentMethod
    ) : PODynamicCheckoutEvent()

    /**
     * Event is sent when certain payment method has failed with retryable error.
     * User can provide different payment details or try another payment method.
     */
    data class DidFailPayment(
        val failure: ProcessOutResult.Failure,
        val paymentMethod: PODynamicCheckoutPaymentMethod
    ) : PODynamicCheckoutEvent()

    /**
     * Event is sent after payment was confirmed to be captured. This is a final event.
     */
    data object DidCompletePayment : PODynamicCheckoutEvent()

    /**
     * Event is sent when unretryable error occurs. This is a final event.
     */
    data class DidFail(
        val failure: ProcessOutResult.Failure
    ) : PODynamicCheckoutEvent()
}
