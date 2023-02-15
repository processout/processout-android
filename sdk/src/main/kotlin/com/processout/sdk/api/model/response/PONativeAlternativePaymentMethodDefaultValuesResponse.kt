package com.processout.sdk.api.model.response

import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodDefaultValuesRequest
import java.util.*

data class PONativeAlternativePaymentMethodDefaultValuesResponse internal constructor(
    val uuid: UUID,
    val defaultValues: Map<String, String>
)

fun PONativeAlternativePaymentMethodDefaultValuesRequest.toResponse(
    defaultValues: Map<String, String>
) = PONativeAlternativePaymentMethodDefaultValuesResponse(uuid, defaultValues)
