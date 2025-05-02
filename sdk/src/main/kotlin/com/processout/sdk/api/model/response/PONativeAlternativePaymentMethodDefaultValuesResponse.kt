package com.processout.sdk.api.model.response

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.request.PODynamicCheckoutAlternativePaymentDefaultValuesRequest
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodDefaultValuesRequest
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import java.util.UUID

/**
 * Defines the response with default values for native alternative payment method parameters.
 * This response can only be created from [PONativeAlternativePaymentMethodDefaultValuesRequest.toResponse] function.
 *
 * @param[uuid] Unique identifier of response that must be equal to UUID of request.
 * @param[defaultValues] Map where key is [PONativeAlternativePaymentMethodParameter.key] and value is a default value for this parameter.
 */
data class PONativeAlternativePaymentMethodDefaultValuesResponse internal constructor(
    override val uuid: UUID,
    val defaultValues: Map<String, String>
) : POEventDispatcher.Response

/**
 * Creates response with default values from request to use the same UUID.
 *
 * @param[defaultValues] Map where key is [PONativeAlternativePaymentMethodParameter.key] and value is a default value for this parameter.
 */
fun PONativeAlternativePaymentMethodDefaultValuesRequest.toResponse(
    defaultValues: Map<String, String>
) = PONativeAlternativePaymentMethodDefaultValuesResponse(uuid, defaultValues)

/** @suppress */
@ProcessOutInternalApi
fun PODynamicCheckoutAlternativePaymentDefaultValuesRequest.toResponse(
    defaultValues: Map<String, String>
) = PONativeAlternativePaymentMethodDefaultValuesResponse(uuid, defaultValues)
