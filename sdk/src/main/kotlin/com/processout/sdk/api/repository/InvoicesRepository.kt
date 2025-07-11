package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.request.POInvoiceAuthorizationRequest
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodRequest
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentAuthorizationRequest
import com.processout.sdk.api.model.response.*
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi

internal interface InvoicesRepository {

    suspend fun authorizeInvoice(
        request: POInvoiceAuthorizationRequest
    ): ProcessOutResult<InvoiceAuthorizationResponse>

    suspend fun authorizeInvoice(
        request: PONativeAlternativePaymentAuthorizationRequest
    ): ProcessOutResult<PONativeAlternativePaymentAuthorizationResponse>

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

    suspend fun captureNativeAlternativePayment(
        invoiceId: String,
        gatewayConfigurationId: String
    ): ProcessOutResult<PONativeAlternativePaymentMethodCapture>

    fun captureNativeAlternativePayment(
        invoiceId: String,
        gatewayConfigurationId: String,
        callback: ProcessOutCallback<PONativeAlternativePaymentMethodCapture>
    )

    suspend fun invoice(request: POInvoiceRequest): ProcessOutResult<POInvoice>

    fun invoice(
        request: POInvoiceRequest,
        callback: ProcessOutCallback<POInvoice>
    )

    /** @suppress */
    @ProcessOutInternalApi
    suspend fun createInvoice(request: POCreateInvoiceRequest): ProcessOutResult<POInvoice>
}
