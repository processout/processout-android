package com.processout.sdk.api.repository

import androidx.annotation.RestrictTo
import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.response.*
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.ProcessOutResult

interface InvoicesRepository {

    suspend fun authorize(
        invoiceId: String,
        request: POInvoiceAuthorizationRequest
    ): ProcessOutResult<POInvoiceAuthorizeSuccess>

    fun authorize(
        invoiceId: String,
        request: POInvoiceAuthorizationRequest,
        callback: ProcessOutCallback<POInvoiceAuthorizeSuccess>
    )

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
