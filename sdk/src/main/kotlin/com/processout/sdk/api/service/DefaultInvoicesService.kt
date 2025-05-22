@file:Suppress("OVERRIDE_DEPRECATION")

package com.processout.sdk.api.service

import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.request.POInvoiceAuthorizationRequest
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodRequest
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentAuthorizationRequest
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentRequest
import com.processout.sdk.api.model.response.POInvoice
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethod
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodCapture
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodTransactionDetails
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse
import com.processout.sdk.api.repository.InvoicesRepository
import com.processout.sdk.core.*
import com.processout.sdk.core.POFailure.Code.Cancelled
import com.processout.sdk.core.logger.POLogAttribute
import com.processout.sdk.core.logger.POLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class DefaultInvoicesService(
    private val scope: CoroutineScope,
    private val repository: InvoicesRepository,
    private val customerActionsService: CustomerActionsService
) : POInvoicesService {

    private val _authorizeInvoiceResult = MutableSharedFlow<ProcessOutResult<String>>()
    override val authorizeInvoiceResult = _authorizeInvoiceResult.asSharedFlow()

    override fun authorizeInvoice(
        request: POInvoiceAuthorizationRequest,
        threeDSService: PO3DSService
    ): Job = scope.launch {
        val logAttributes = mapOf(POLogAttribute.INVOICE_ID to request.invoiceId)
        repository.authorizeInvoice(request)
            .onSuccess { response ->
                if (response.customerAction == null) {
                    threeDSService.cleanup()
                    _authorizeInvoiceResult.emit(
                        ProcessOutResult.Success(request.invoiceId)
                    )
                    return@onSuccess
                }
                customerActionsService.handle(response.customerAction, threeDSService)
                    .onSuccess { newSource ->
                        authorizeInvoice(
                            request.copy(source = newSource),
                            threeDSService
                        )
                    }.onFailure { failure ->
                        POLogger.warn(
                            message = "Failed to authorize invoice: %s", failure,
                            attributes = logAttributes
                        )
                        threeDSService.cleanup()
                        _authorizeInvoiceResult.emit(failure)
                    }
            }.onFailure { failure ->
                POLogger.warn(
                    message = "Failed to authorize invoice: %s", failure,
                    attributes = logAttributes
                )
                threeDSService.cleanup()
                _authorizeInvoiceResult.emit(failure)
            }
    }

    override fun authorizeInvoice(
        request: POInvoiceAuthorizationRequest,
        threeDSService: PO3DSService,
        callback: (ProcessOutResult<Unit>) -> Unit
    ): Job = scope.launch {
        val logAttributes = mapOf(POLogAttribute.INVOICE_ID to request.invoiceId)
        repository.authorizeInvoice(request)
            .onSuccess { response ->
                if (response.customerAction == null) {
                    threeDSService.cleanup()
                    callback(ProcessOutResult.Success(Unit))
                    return@onSuccess
                }
                customerActionsService.handle(response.customerAction, threeDSService)
                    .onSuccess { newSource ->
                        authorizeInvoice(
                            request.copy(source = newSource),
                            threeDSService,
                            callback
                        )
                    }.onFailure { failure ->
                        POLogger.warn(
                            message = "Failed to authorize invoice: %s", failure,
                            attributes = logAttributes
                        )
                        threeDSService.cleanup()
                        callback(failure)
                    }
            }.onFailure { failure ->
                POLogger.warn(
                    message = "Failed to authorize invoice: %s", failure,
                    attributes = logAttributes
                )
                threeDSService.cleanup()
                callback(failure)
            }
    }

    override suspend fun authorize(
        request: POInvoiceAuthorizationRequest,
        threeDSService: PO3DSService
    ): ProcessOutResult<Unit> {
        val logAttributes = mapOf(POLogAttribute.INVOICE_ID to request.invoiceId)
        return try {
            repository.authorizeInvoice(request)
                .fold(
                    onSuccess = { response ->
                        if (response.customerAction == null) {
                            threeDSService.cleanup()
                            return@fold ProcessOutResult.Success(Unit)
                        }
                        customerActionsService.handle(response.customerAction, threeDSService)
                            .fold(
                                onSuccess = { newSource ->
                                    authorize(
                                        request.copy(source = newSource),
                                        threeDSService
                                    )
                                },
                                onFailure = { failure ->
                                    POLogger.warn(
                                        message = "Failed to authorize invoice: %s", failure,
                                        attributes = logAttributes
                                    )
                                    threeDSService.cleanup()
                                    failure
                                }
                            )
                    },
                    onFailure = { failure ->
                        POLogger.warn(
                            message = "Failed to authorize invoice: %s", failure,
                            attributes = logAttributes
                        )
                        threeDSService.cleanup()
                        failure
                    }
                )
        } catch (e: CancellationException) {
            coroutineScope {
                val failure = ProcessOutResult.Failure(
                    code = Cancelled,
                    message = e.message,
                    cause = e
                )
                POLogger.info(
                    message = "Invoice authorization has been cancelled: %s", failure,
                    attributes = logAttributes
                )
                threeDSService.cleanup()
                ensureActive()
                failure
            }
        }
    }

    override suspend fun authorize(
        request: PONativeAlternativePaymentAuthorizationRequest
    ): ProcessOutResult<PONativeAlternativePaymentAuthorizationResponse> =
        repository.authorizeInvoice(request)

    override suspend fun nativeAlternativePayment(
        request: PONativeAlternativePaymentRequest
    ): ProcessOutResult<PONativeAlternativePaymentAuthorizationResponse> =
        repository.nativeAlternativePayment(request)

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

    override suspend fun captureNativeAlternativePayment(
        invoiceId: String,
        gatewayConfigurationId: String
    ): ProcessOutResult<PONativeAlternativePaymentMethodCapture> =
        repository.captureNativeAlternativePayment(invoiceId, gatewayConfigurationId)

    override fun captureNativeAlternativePayment(
        invoiceId: String,
        gatewayConfigurationId: String,
        callback: ProcessOutCallback<PONativeAlternativePaymentMethodCapture>
    ) {
        repository.captureNativeAlternativePayment(invoiceId, gatewayConfigurationId, callback)
    }

    override suspend fun invoice(
        request: POInvoiceRequest
    ): ProcessOutResult<POInvoice> =
        repository.invoice(request)

    override fun invoice(
        request: POInvoiceRequest,
        callback: ProcessOutCallback<POInvoice>
    ) {
        repository.invoice(request, callback)
    }

    override suspend fun invoice(
        invoiceId: String
    ): ProcessOutResult<POInvoice> =
        repository.invoice(
            request = POInvoiceRequest(
                invoiceId = invoiceId,
                clientSecret = null
            )
        )

    override fun invoice(
        invoiceId: String,
        callback: ProcessOutCallback<POInvoice>
    ) {
        repository.invoice(
            request = POInvoiceRequest(
                invoiceId = invoiceId,
                clientSecret = null
            ),
            callback = callback
        )
    }

    override suspend fun createInvoice(
        request: POCreateInvoiceRequest
    ): ProcessOutResult<POInvoice> =
        repository.createInvoice(request)
}
