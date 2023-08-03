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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class InvoicesServiceImpl(
    private val scope: CoroutineScope,
    private val repository: InvoicesRepository,
    private val threeDSService: ThreeDSService
) : POInvoicesService {

    private val _authorizeInvoiceResult = MutableSharedFlow<ProcessOutResult<String>>()
    override val authorizeInvoiceResult = _authorizeInvoiceResult.asSharedFlow()

    override fun authorizeInvoice(
        request: POInvoiceAuthorizationRequest,
        threeDSService: PO3DSService
    ) {
        scope.launch {
            when (val result = repository.authorizeInvoice(request)) {
                is ProcessOutResult.Success ->
                    result.value.customerAction?.let { action ->
                        this@InvoicesServiceImpl.threeDSService
                            .handle(action, threeDSService) { serviceResult ->
                                when (serviceResult) {
                                    is ProcessOutResult.Success ->
                                        authorizeInvoice(
                                            request.copy(source = serviceResult.value),
                                            threeDSService
                                        )
                                    is ProcessOutResult.Failure -> scope.launch {
                                        _authorizeInvoiceResult.emit(serviceResult.copy())
                                    }
                                }
                            }
                    } ?: run {
                        threeDSService.cleanup()
                        scope.launch {
                            _authorizeInvoiceResult.emit(
                                ProcessOutResult.Success(request.invoiceId)
                            )
                        }
                    }
                is ProcessOutResult.Failure -> {
                    threeDSService.cleanup()
                    scope.launch { _authorizeInvoiceResult.emit(result.copy()) }
                }
            }
        }
    }

    @Deprecated(
        message = "Use function authorizeInvoice(request, threeDSService)",
        replaceWith = ReplaceWith("authorizeInvoice(request, threeDSService)")
    )
    override fun authorizeInvoice(
        request: POInvoiceAuthorizationRequest,
        threeDSService: PO3DSService,
        callback: (ProcessOutResult<Unit>) -> Unit
    ) {
        scope.launch {
            when (val result = repository.authorizeInvoice(request)) {
                is ProcessOutResult.Success ->
                    result.value.customerAction?.let { action ->
                        this@InvoicesServiceImpl.threeDSService
                            .handle(action, threeDSService) { serviceResult ->
                                @Suppress("DEPRECATION")
                                when (serviceResult) {
                                    is ProcessOutResult.Success ->
                                        authorizeInvoice(
                                            request.copy(source = serviceResult.value),
                                            threeDSService,
                                            callback
                                        )
                                    is ProcessOutResult.Failure -> callback(serviceResult.copy())
                                }
                            }
                    } ?: run {
                        threeDSService.cleanup()
                        callback(ProcessOutResult.Success(Unit))
                    }
                is ProcessOutResult.Failure -> {
                    threeDSService.cleanup()
                    callback(result.copy())
                }
            }
        }
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

    @ProcessOutInternalApi
    override suspend fun createInvoice(
        request: POCreateInvoiceRequest
    ): ProcessOutResult<POInvoice> =
        repository.createInvoice(request)
}
