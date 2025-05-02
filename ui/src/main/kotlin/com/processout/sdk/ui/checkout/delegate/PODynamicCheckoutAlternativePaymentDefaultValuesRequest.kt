package com.processout.sdk.ui.checkout.delegate

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodDefaultValuesResponse
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import java.util.UUID

/** @suppress */
@ProcessOutInternalApi
data class PODynamicCheckoutAlternativePaymentDefaultValuesRequest(
    override val uuid: UUID,
    val paymentMethod: PODynamicCheckoutPaymentMethod.AlternativePayment,
    val parameters: List<PONativeAlternativePaymentMethodParameter>
) : POEventDispatcher.Request

/** @suppress */
@ProcessOutInternalApi
fun PODynamicCheckoutAlternativePaymentDefaultValuesRequest.toResponse(
    defaultValues: Map<String, String>
) = PONativeAlternativePaymentMethodDefaultValuesResponse(uuid, defaultValues)
