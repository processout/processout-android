package com.processout.sdk.api.service

import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.request.POInvoiceAuthorizationRequest
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodRequest
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentAuthorizationDetailsRequest
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentAuthorizationRequest
import com.processout.sdk.api.model.response.POInvoice
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethod
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodCapture
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodTransactionDetails
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse
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
     * Subscribe to this flow to collect the result from [authorizeInvoice] invocation.
     * Result contains _invoiceId_ that was used for authorization.
     */
    @Deprecated(message = "Use function: authorize(request, threeDSService)")
    val authorizeInvoiceResult: SharedFlow<ProcessOutResult<String>>

    /**
     * Authorize invoice with the given request and 3DS service implementation.
     * Collect the result by subscribing to [authorizeInvoiceResult] flow before invoking this function.
     * Returns coroutine job.
     */
    @Deprecated(
        message = "Use replacement function.",
        replaceWith = ReplaceWith("authorize(request, threeDSService)")
    )
    fun authorizeInvoice(
        request: POInvoiceAuthorizationRequest,
        threeDSService: PO3DSService
    ): Job

    /**
     * Authorize invoice with the given request and 3DS service implementation.
     * Result provided in the callback.
     * Returns coroutine job.
     */
    @Deprecated(
        message = "Use replacement function.",
        replaceWith = ReplaceWith("authorize(request, threeDSService)")
    )
    fun authorizeInvoice(
        request: POInvoiceAuthorizationRequest,
        threeDSService: PO3DSService,
        callback: (ProcessOutResult<Unit>) -> Unit
    ): Job

    /**
     * Authorize invoice with the given request and 3DS service implementation.
     */
    suspend fun authorize(
        request: POInvoiceAuthorizationRequest,
        threeDSService: PO3DSService
    ): ProcessOutResult<Unit>

    /**
     * Authorize invoice with the given request.
     */
    /** @suppress */
    @ProcessOutInternalApi
    suspend fun authorize(
        request: PONativeAlternativePaymentAuthorizationRequest
    ): ProcessOutResult<PONativeAlternativePaymentAuthorizationResponse>

    /**
     * Fetch native alternative payment details.
     */
    /** @suppress */
    @ProcessOutInternalApi
    suspend fun nativeAlternativePayment(
        request: PONativeAlternativePaymentAuthorizationDetailsRequest
    ): ProcessOutResult<PONativeAlternativePaymentAuthorizationResponse>

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
