package com.processout.sdk.ui.checkout.delegate

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentElement
import com.processout.sdk.ui.napm.delegate.v2.NativeAlternativePaymentDefaultValuesResponse
import com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentParameterValue
import java.util.UUID

internal data class DynamicCheckoutAlternativePaymentDefaultValuesRequest(
    override val uuid: UUID,
    val paymentMethod: PODynamicCheckoutPaymentMethod.AlternativePayment,
    val parameters: List<PONativeAlternativePaymentElement.Form.Parameter>
) : POEventDispatcher.Request

internal fun DynamicCheckoutAlternativePaymentDefaultValuesRequest.toResponse(
    defaultValues: Map<String, PONativeAlternativePaymentParameterValue>
) = NativeAlternativePaymentDefaultValuesResponse(uuid, defaultValues)
