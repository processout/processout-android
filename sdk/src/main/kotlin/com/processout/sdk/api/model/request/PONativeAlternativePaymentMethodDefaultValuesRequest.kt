package com.processout.sdk.api.model.request

import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter
import java.util.*

data class PONativeAlternativePaymentMethodDefaultValuesRequest internal constructor(
    val gatewayConfigurationId: String,
    val invoiceId: String,
    val parameters: List<PONativeAlternativePaymentMethodParameter>,
    val uuid: UUID = UUID.randomUUID()
)
