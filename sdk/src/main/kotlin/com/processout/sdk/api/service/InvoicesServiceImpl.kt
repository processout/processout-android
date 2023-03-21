package com.processout.sdk.api.service

import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.request.POInvoiceAuthorizationRequest
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.POInvoice
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethod
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodCapture
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodTransactionDetails
import com.processout.sdk.api.repository.InvoicesRepository
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi

internal class InvoicesServiceImpl(
    private val repository: InvoicesRepository,
    private val threeDSService: ThreeDSService
) : InvoicesService {

    override fun authorizeInvoice(
        request: POInvoiceAuthorizationRequest,
        threeDSHandler: PO3DSHandler,
        callback: (PO3DSResult<Unit>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun capture(
        invoiceId: String,
        gatewayConfigurationId: String
    ): ProcessOutResult<PONativeAlternativePaymentMethodCapture> =
        repository.capture(invoiceId, gatewayConfigurationId)

    override fun capture(
        invoiceId: String,
        gatewayConfigurationId: String,
        callback: ProcessOutCallback<PONativeAlternativePaymentMethodCapture>
    ) {
        repository.capture(invoiceId, gatewayConfigurationId, callback)
    }

    override suspend fun initiatePayment(
        request: PONativeAlternativePaymentMethodRequest
    ): ProcessOutResult<PONativeAlternativePaymentMethod> =
        repository.initiatePayment(request)

    override fun initiatePayment(
        request: PONativeAlternativePaymentMethodRequest,
        callback: ProcessOutCallback<PONativeAlternativePaymentMethod>
    ) {
        repository.initiatePayment(request, callback)
    }

    override suspend fun fetchNativeAlternativePaymentMethodTransactionDetails(
        invoiceId: String,
        gatewayConfigurationId: String
    ): ProcessOutResult<PONativeAlternativePaymentMethodTransactionDetails> =
        repository.fetchNativeAlternativePaymentMethodTransactionDetails(
            invoiceId, gatewayConfigurationId
        )

    override fun fetchNativeAlternativePaymentMethodTransactionDetails(
        invoiceId: String,
        gatewayConfigurationId: String,
        callback: ProcessOutCallback<PONativeAlternativePaymentMethodTransactionDetails>
    ) {
        repository.fetchNativeAlternativePaymentMethodTransactionDetails(
            invoiceId, gatewayConfigurationId, callback
        )
    }

    @ProcessOutInternalApi
    override suspend fun createInvoice(
        request: POCreateInvoiceRequest
    ): ProcessOutResult<POInvoice> =
        repository.createInvoice(request)
}
