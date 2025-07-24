package com.processout.sdk.ui.napm.delegate.v2

import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentElement
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentState
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi

/**
 * Defines native alternative payment lifecycle events.
 */
/** @suppress */
@ProcessOutInternalApi
sealed class PONativeAlternativePaymentEvent {

    /**
     * Initial event that is sent prior any other event.
     */
    data object WillStart : PONativeAlternativePaymentEvent()

    /**
     * Event indicates that initial data has been loaded successfully.
     * Currently waiting for the next step.
     */
    data object DidStart : PONativeAlternativePaymentEvent()

    /**
     * Event is sent when user changes any editable value.
     *
     * @param[parameter] Parameter definition.
     */
    data class ParametersChanged(
        val parameter: PONativeAlternativePaymentElement.Form.Parameter
    ) : PONativeAlternativePaymentEvent()

    /**
     * Event is sent just before submitting user input.
     * This is usually a result of a user action, e.g. button press.
     *
     * @param[parameters] Parameter definitions.
     */
    data class WillSubmitParameters(
        val parameters: List<PONativeAlternativePaymentElement.Form.Parameter>
    ) : PONativeAlternativePaymentEvent()

    /**
     * Event is sent when parameters were submitted successfully.
     * Inspect the associated value [additionalParametersExpected] to check whether additional user input is required.
     */
    data class DidSubmitParameters(
        val additionalParametersExpected: Boolean
    ) : PONativeAlternativePaymentEvent()

    /**
     * Event is sent when parameters submission failed and the error is retriable, otherwise expect [DidFail] event.
     */
    data class DidFailToSubmitParameters(
        val failure: ProcessOutResult.Failure
    ) : PONativeAlternativePaymentEvent()

    /**
     * Event is sent when user asked to confirm cancellation, e.g. via dialog.
     */
    data object DidRequestCancelConfirmation : PONativeAlternativePaymentEvent()

    /**
     * Event is sent after all information is collected and implementation is waiting for a PSP to confirm the payment.
     */
    data object WillWaitForPaymentConfirmation : PONativeAlternativePaymentEvent()

    /**
     * Event is sent during the _PENDING_ state when the user confirms that they have completed required external action.
     * Implementation proceeds with the actual payment confirmation process.
     */
    data object DidConfirmPayment : PONativeAlternativePaymentEvent()

    /**
     * Event is sent after payment was confirmed to be completed. This is a final event.
     */
    data object DidCompletePayment : PONativeAlternativePaymentEvent()

    /**
     * Event is sent when unretryable error occurs. This is a final event.
     *
     * @param[paymentState] The payment state provides additional context about where in the payment process the failure occurred.
     * For example, in the event of a user-initiated cancellation,
     * this state can be used to determine which step the user was on when they canceled.
     */
    data class DidFail(
        val failure: ProcessOutResult.Failure,
        val paymentState: PONativeAlternativePaymentState
    ) : PONativeAlternativePaymentEvent()
}
