package com.processout.sdk.ui.napm.delegate

import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter

/**
 * Delegate that allows to handle events during native alternative payments.
 */
interface PONativeAlternativePaymentDelegate {

    /**
     * Invoked on native alternative payment lifecycle events.
     */
    fun onEvent(event: PONativeAlternativePaymentEvent) {}

    /**
     * Allows to prefill default values for the given parameters during native alternative payment.
     * Return a map where key is a [PONativeAlternativePaymentMethodParameter.key] and value is a custom default value.
     * It's not mandatory to provide default values for all parameters.
     */
    suspend fun defaultValues(
        gatewayConfigurationId: String,
        parameters: List<PONativeAlternativePaymentMethodParameter>
    ): Map<String, String> = emptyMap()
}
