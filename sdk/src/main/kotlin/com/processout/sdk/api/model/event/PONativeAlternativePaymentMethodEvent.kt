package com.processout.sdk.api.model.event

import com.processout.sdk.core.ProcessOutResult

/**
 * Defines native alternative payment lifecycle events.
 */
sealed class PONativeAlternativePaymentMethodEvent {

    /**
     * Initial event that is sent prior any other event.
     */
    data object WillStart : PONativeAlternativePaymentMethodEvent()

    /**
     * Event indicates that initial data has been loaded successfully.
     * Currently waiting for user input.
     */
    data object DidStart : PONativeAlternativePaymentMethodEvent()

    /**
     * Event is sent when user changes any editable value.
     */
    data object ParametersChanged : PONativeAlternativePaymentMethodEvent()

    /**
     * Event is sent just before submitting user input.
     * This is usually a result of a user action, e.g. button press.
     */
    data object WillSubmitParameters : PONativeAlternativePaymentMethodEvent()

    /**
     * Event is sent when parameters were submitted successfully.
     * Inspect the associated value [additionalParametersExpected] to check whether additional user input is required.
     */
    data class DidSubmitParameters(
        val additionalParametersExpected: Boolean
    ) : PONativeAlternativePaymentMethodEvent()

    /**
     * Event is sent when parameters submission failed and the error is retriable, otherwise expect [DidFail] event.
     */
    data class DidFailToSubmitParameters(
        val failure: ProcessOutResult.Failure
    ) : PONativeAlternativePaymentMethodEvent()

    /**
     * Event is sent after all information is collected and implementation is waiting for a PSP to confirm capture.
     * Inspect the associated value [additionalActionExpected] to check whether user needs
     * to do an additional action(s) outside the application,
     * for example to confirm the operation in their banking app to make capture happen.
     */
    data class WillWaitForCaptureConfirmation(
        val additionalActionExpected: Boolean
    ) : PONativeAlternativePaymentMethodEvent()

    /**
     * Event is sent when user asked to confirm cancellation, e.g. via dialog.
     */
    data object DidRequestCancelConfirmation : PONativeAlternativePaymentMethodEvent()

    /**
     * Event is sent after payment was confirmed to be captured. This is a final event.
     */
    data object DidCompletePayment : PONativeAlternativePaymentMethodEvent()

    /**
     * Event is sent when unretryable error occurs. This is a final event.
     */
    data class DidFail(
        val failure: ProcessOutResult.Failure
    ) : PONativeAlternativePaymentMethodEvent()
}
