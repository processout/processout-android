package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.request.POInvoiceAuthorizationRequest
import com.processout.sdk.api.model.request.POInvoiceAuthorizationSuccess
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.POCaptureSuccess
import com.processout.sdk.api.model.response.POInvoice
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethod
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodTransactionDetails
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi

interface InvoicesRepository {

    suspend fun authorize(
        invoiceId: String,
        request: POInvoiceAuthorizationRequest
    ): ProcessOutResult<POInvoiceAuthorizationSuccess>

    fun authorize(
        invoiceId: String,
        request: POInvoiceAuthorizationRequest,
        callback: ProcessOutCallback<POInvoiceAuthorizationSuccess>
    )

    suspend fun capture(
        invoiceId: String,
        gatewayConfigurationId: String
    ): ProcessOutResult<POCaptureSuccess>

    fun capture(
        invoiceId: String,
        gatewayConfigurationId: String,
        callback: ProcessOutCallback<POCaptureSuccess>
    )

    suspend fun initiatePayment(
        request: PONativeAlternativePaymentMethodRequest
    ): ProcessOutResult<PONativeAlternativePaymentMethod>

    fun initiatePayment(
        request: PONativeAlternativePaymentMethodRequest,
        callback: ProcessOutCallback<PONativeAlternativePaymentMethod>
    )

    suspend fun fetchNativeAlternativePaymentMethodTransactionDetails(
        invoiceId: String,
        gatewayConfigurationId: String
    ): ProcessOutResult<PONativeAlternativePaymentMethodTransactionDetails>

    fun fetchNativeAlternativePaymentMethodTransactionDetails(
        invoiceId: String,
        gatewayConfigurationId: String,
        callback: ProcessOutCallback<PONativeAlternativePaymentMethodTransactionDetails>
    )

    @ProcessOutInternalApi
    suspend fun createInvoice(request: POCreateInvoiceRequest): ProcessOutResult<POInvoice>
}
