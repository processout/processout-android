package com.processout.sdk.ui.checkout.delegate

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodDefaultValuesResponse
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter
import java.util.UUID

internal data class DynamicCheckoutAlternativePaymentDefaultValuesRequest(
    override val uuid: UUID,
    val paymentMethod: PODynamicCheckoutPaymentMethod.AlternativePayment,
    val parameters: List<PONativeAlternativePaymentMethodParameter>
) : POEventDispatcher.Request

internal fun DynamicCheckoutAlternativePaymentDefaultValuesRequest.toResponse(
    defaultValues: Map<String, String>
) = PONativeAlternativePaymentMethodDefaultValuesResponse(uuid, defaultValues)
