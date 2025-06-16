package com.processout.sdk.ui.napm.delegate.v2

import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentElement
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
     * Currently waiting for user input.
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
     * Event is sent after all information is collected and implementation is waiting for a PSP to confirm capture.
     * Inspect the associated value [additionalActionExpected] to check whether user needs
     * to do an additional action(s) outside the application,
     * for example to confirm the operation in their banking app to make capture happen.
     */
    data class WillWaitForCaptureConfirmation(
        val additionalActionExpected: Boolean
    ) : PONativeAlternativePaymentEvent()

    /**
     * Event is sent during the capture stage when the user confirms that they have completed required external action.
     * Implementation proceeds with the actual capture process.
     */
    data object DidConfirmPayment : PONativeAlternativePaymentEvent()

    /**
     * Event is sent when user asked to confirm cancellation, e.g. via dialog.
     */
    data object DidRequestCancelConfirmation : PONativeAlternativePaymentEvent()

    /**
     * Event is sent after payment was confirmed to be captured. This is a final event.
     */
    data object DidCompletePayment : PONativeAlternativePaymentEvent()

    /**
     * Event is sent when unretryable error occurs. This is a final event.
     */
    data class DidFail(
        val failure: ProcessOutResult.Failure
    ) : PONativeAlternativePaymentEvent()
}
