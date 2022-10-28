package com.processout.sdk.api.repository

import androidx.annotation.RestrictTo
import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.POInvoice
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethod
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.ProcessOutResult

interface InvoicesRepository {

    suspend fun initiatePayment(
        request: PONativeAlternativePaymentMethodRequest
    ): ProcessOutResult<PONativeAlternativePaymentMethod>

    fun initiatePayment(
        request: PONativeAlternativePaymentMethodRequest,
        callback: ProcessOutCallback<PONativeAlternativePaymentMethod>
    )

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    suspend fun createInvoice(request: POCreateInvoiceRequest): ProcessOutResult<POInvoice>
}
