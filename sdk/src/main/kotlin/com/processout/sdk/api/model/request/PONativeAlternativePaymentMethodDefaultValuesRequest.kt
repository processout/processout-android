package com.processout.sdk.api.model.request

import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter
import java.util.UUID

/**
 * Defines the request to provide default values for native alternative payment method parameters.
 *
 * @param[gatewayConfigurationId] Gateway configuration identifier.
 * @param[invoiceId] Invoice identifier.
 * @param[parameters] Collection of parameters that can be inspected to decide if default values should be provided.
 * @param[uuid] Unique identifier of request.
 */
data class PONativeAlternativePaymentMethodDefaultValuesRequest internal constructor(
    val gatewayConfigurationId: String,
    val invoiceId: String,
    val parameters: List<PONativeAlternativePaymentMethodParameter>,
    val uuid: UUID = UUID.randomUUID()
)
