package com.processout.sdk.ui.napm.delegate.v2

import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAvailableAction
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentElement
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentState
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult

/**
 * Delegate that allows to handle events during native alternative payments.
 */
interface PONativeAlternativePaymentDelegate {

    /**
     * Invoked on native alternative payment lifecycle events.
     */
    fun onEvent(event: PONativeAlternativePaymentEvent) {}

    /**
     * Allows to prefill default values for the given [parameters] during native alternative payment.
     * Return a map of parameter keys to their custom default values.
     * It's not mandatory to provide default values for all parameters.
     */
    suspend fun defaultValues(
        gatewayConfigurationId: String,
        parameters: List<PONativeAlternativePaymentElement.Form.Parameter>
    ): Map<String, PONativeAlternativePaymentParameterValue> = emptyMap()

    /**
     * Asks delegate to finalize the payment with the given [availableActions].
     */
    suspend fun finalize(
        paymentState: PONativeAlternativePaymentState,
        availableActions: List<PONativeAlternativePaymentAvailableAction>
    ): ProcessOutResult<Unit> =
        ProcessOutResult.Failure(
            code = POFailure.Code.Generic(),
            message = "Manual finalization is not implemented by the delegate."
        )
}
