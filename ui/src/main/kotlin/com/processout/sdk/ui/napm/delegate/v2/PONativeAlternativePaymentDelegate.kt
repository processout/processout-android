package com.processout.sdk.ui.napm.delegate.v2

import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentNextStep
import com.processout.sdk.core.annotation.ProcessOutInternalApi

/**
 * Delegate that allows to handle events during native alternative payments.
 */
/** @suppress */
@ProcessOutInternalApi
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
        parameters: List<PONativeAlternativePaymentNextStep.SubmitData.Parameter>
    ): Map<String, PONativeAlternativePaymentParameterValue> = emptyMap()
}
