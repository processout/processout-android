package com.processout.sdk.api.model.request

import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod
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
