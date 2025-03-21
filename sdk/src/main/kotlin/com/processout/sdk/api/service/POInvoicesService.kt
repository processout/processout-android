package com.processout.sdk.api.service

import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.request.POInvoiceAuthorizationRequest
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.POInvoice
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethod
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodCapture
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodTransactionDetails
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow

/**
 * Provides functionality related to invoices.
 */
interface POInvoicesService {

    /**
     * Subscribe to this flow to collect result from [authorizeInvoice] invocation.
     * Result contains _invoiceId_ that was used for authorization.
     */
    val authorizeInvoiceResult: SharedFlow<ProcessOutResult<String>>

    /**
     * Authorize invoice with the given request and 3DS service implementation.
     * Collect result by subscribing to [authorizeInvoiceResult] flow before invoking invoice authorization.
     */
    fun authorizeInvoice(
        request: POInvoiceAuthorizationRequest,
        threeDSService: PO3DSService
    )

    @Deprecated(
        message = "Use function authorizeInvoice(request, threeDSService)",
        replaceWith = ReplaceWith("authorizeInvoice(request, threeDSService)")
    )
    fun authorizeInvoice(
        request: POInvoiceAuthorizationRequest,
        threeDSService: PO3DSService,
        callback: (ProcessOutResult<Unit>) -> Unit
    ): Job

    /**
     * Initiates native alternative payment with the given request.
     */
    suspend fun initiatePayment(
        request: PONativeAlternativePaymentMethodRequest
    ): ProcessOutResult<PONativeAlternativePaymentMethod>

    /**
     * Initiates native alternative payment with the given request.
     */
    fun initiatePayment(
        request: PONativeAlternativePaymentMethodRequest,
        callback: ProcessOutCallback<PONativeAlternativePaymentMethod>
    )

    /**
     * Fetch information to start or continue payment.
     */
    suspend fun fetchNativeAlternativePaymentMethodTransactionDetails(
        invoiceId: String,
        gatewayConfigurationId: String
    ): ProcessOutResult<PONativeAlternativePaymentMethodTransactionDetails>

    /**
     * Fetch information to start or continue payment.
     */
    fun fetchNativeAlternativePaymentMethodTransactionDetails(
        invoiceId: String,
        gatewayConfigurationId: String,
        callback: ProcessOutCallback<PONativeAlternativePaymentMethodTransactionDetails>
    )

    /**
     * Captures native alternative payment.
     */
    suspend fun captureNativeAlternativePayment(
        invoiceId: String,
        gatewayConfigurationId: String
    ): ProcessOutResult<PONativeAlternativePaymentMethodCapture>

    /**
     * Captures native alternative payment.
     */
    fun captureNativeAlternativePayment(
        invoiceId: String,
        gatewayConfigurationId: String,
        callback: ProcessOutCallback<PONativeAlternativePaymentMethodCapture>
    )

    /**
     * Fetch invoice details.
     */
    suspend fun invoice(request: POInvoiceRequest): ProcessOutResult<POInvoice>

    /**
     * Fetch invoice details.
     */
    fun invoice(
        request: POInvoiceRequest,
        callback: ProcessOutCallback<POInvoice>
    )

    /**
     * Fetch invoice details.
     */
    @Deprecated(
        message = "Use function invoice(request)",
        replaceWith = ReplaceWith("invoice(request)")
    )
    suspend fun invoice(invoiceId: String): ProcessOutResult<POInvoice>

    /**
     * Fetch invoice details.
     */
    @Deprecated(
        message = "Use function invoice(request, callback)",
        replaceWith = ReplaceWith("invoice(request, callback)")
    )
    fun invoice(
        invoiceId: String,
        callback: ProcessOutCallback<POInvoice>
    )

    /** @suppress */
    @ProcessOutInternalApi
    suspend fun createInvoice(request: POCreateInvoiceRequest): ProcessOutResult<POInvoice>
}
