@file:Suppress("OVERRIDE_DEPRECATION")

package com.processout.sdk.api.service

import com.processout.sdk.api.model.request.POAssignCustomerTokenRequest
import com.processout.sdk.api.model.request.POCreateCustomerRequest
import com.processout.sdk.api.model.request.POCreateCustomerTokenRequest
import com.processout.sdk.api.model.request.PODeleteCustomerTokenRequest
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentTokenizationRequest
import com.processout.sdk.api.model.response.POCustomer
import com.processout.sdk.api.model.response.POCustomerToken
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentTokenizationResponse
import com.processout.sdk.api.repository.CustomerTokensRepository
import com.processout.sdk.core.POFailure.Code.Cancelled
import com.processout.sdk.core.POFailure.Code.Internal
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.fold
import com.processout.sdk.core.logger.POLogAttribute
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class DefaultCustomerTokensService(
    private val scope: CoroutineScope,
    private val repository: CustomerTokensRepository,
    private val customerActionsService: CustomerActionsService
) : POCustomerTokensService {

    private val _assignCustomerTokenResult = MutableSharedFlow<ProcessOutResult<POCustomerToken>>()
    override val assignCustomerTokenResult = _assignCustomerTokenResult.asSharedFlow()

    override fun assignCustomerToken(
        request: POAssignCustomerTokenRequest,
        threeDSService: PO3DSService
    ): Job = scope.launch {
        val logAttributes = mapOf(
            POLogAttribute.CUSTOMER_ID to request.customerId,
            POLogAttribute.CUSTOMER_TOKEN_ID to request.tokenId
        )
        repository.assignCustomerToken(request)
            .onSuccess { response ->
                if (response.customerAction == null) {
                    threeDSService.cleanup()
                    if (response.token == null) {
                        val failure = ProcessOutResult.Failure(
                            code = Internal(),
                            message = "Customer token is null."
                        )
                        POLogger.warn(
                            message = "Failed to assign customer token: %s", failure,
                            attributes = logAttributes
                        )
                        _assignCustomerTokenResult.emit(failure)
                        return@onSuccess
                    }
                    _assignCustomerTokenResult.emit(
                        ProcessOutResult.Success(response.token)
                    )
                    return@onSuccess
                }
                customerActionsService.handle(response.customerAction, threeDSService)
                    .onSuccess { newSource ->
                        assignCustomerToken(
                            request.copy(source = newSource),
                            threeDSService
                        )
                    }.onFailure { failure ->
                        POLogger.warn(
                            message = "Failed to assign customer token: %s", failure,
                            attributes = logAttributes
                        )
                        threeDSService.cleanup()
                        _assignCustomerTokenResult.emit(failure)
                    }
            }.onFailure { failure ->
                POLogger.warn(
                    message = "Failed to assign customer token: %s", failure,
                    attributes = logAttributes
                )
                threeDSService.cleanup()
                _assignCustomerTokenResult.emit(failure)
            }
    }

    override fun assignCustomerToken(
        request: POAssignCustomerTokenRequest,
        threeDSService: PO3DSService,
        callback: (ProcessOutResult<POCustomerToken>) -> Unit
    ): Job = scope.launch {
        val logAttributes = mapOf(
            POLogAttribute.CUSTOMER_ID to request.customerId,
            POLogAttribute.CUSTOMER_TOKEN_ID to request.tokenId
        )
        repository.assignCustomerToken(request)
            .onSuccess { response ->
                if (response.customerAction == null) {
                    threeDSService.cleanup()
                    if (response.token == null) {
                        val failure = ProcessOutResult.Failure(
                            code = Internal(),
                            message = "Customer token is null."
                        )
                        POLogger.warn(
                            message = "Failed to assign customer token: %s", failure,
                            attributes = logAttributes
                        )
                        callback(failure)
                        return@onSuccess
                    }
                    callback(ProcessOutResult.Success(response.token))
                    return@onSuccess
                }
                customerActionsService.handle(response.customerAction, threeDSService)
                    .onSuccess { newSource ->
                        assignCustomerToken(
                            request.copy(source = newSource),
                            threeDSService,
                            callback
                        )
                    }.onFailure { failure ->
                        POLogger.warn(
                            message = "Failed to assign customer token: %s", failure,
                            attributes = logAttributes
                        )
                        threeDSService.cleanup()
                        callback(failure)
                    }
            }.onFailure { failure ->
                POLogger.warn(
                    message = "Failed to assign customer token: %s", failure,
                    attributes = logAttributes
                )
                threeDSService.cleanup()
                callback(failure)
            }
    }

    override suspend fun assign(
        request: POAssignCustomerTokenRequest,
        threeDSService: PO3DSService
    ): ProcessOutResult<POCustomerToken> {
        val logAttributes = mapOf(
            POLogAttribute.CUSTOMER_ID to request.customerId,
            POLogAttribute.CUSTOMER_TOKEN_ID to request.tokenId
        )
        return try {
            repository.assignCustomerToken(request)
                .fold(
                    onSuccess = { response ->
                        if (response.customerAction == null) {
                            threeDSService.cleanup()
                            if (response.token == null) {
                                val failure = ProcessOutResult.Failure(
                                    code = Internal(),
                                    message = "Customer token is null."
                                )
                                POLogger.warn(
                                    message = "Failed to assign customer token: %s", failure,
                                    attributes = logAttributes
                                )
                                return@fold failure
                            }
                            return@fold ProcessOutResult.Success(response.token)
                        }
                        customerActionsService.handle(response.customerAction, threeDSService)
                            .fold(
                                onSuccess = { newSource ->
                                    assign(
                                        request.copy(source = newSource),
                                        threeDSService
                                    )
                                },
                                onFailure = { failure ->
                                    POLogger.warn(
                                        message = "Failed to assign customer token: %s", failure,
                                        attributes = logAttributes
                                    )
                                    threeDSService.cleanup()
                                    failure
                                }
                            )
                    },
                    onFailure = { failure ->
                        POLogger.warn(
                            message = "Failed to assign customer token: %s", failure,
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
                    message = "Customer token assigning has been cancelled: %s", failure,
                    attributes = logAttributes
                )
                threeDSService.cleanup()
                ensureActive()
                failure
            }
        }
    }

    override suspend fun tokenize(
        request: PONativeAlternativePaymentTokenizationRequest
    ): ProcessOutResult<PONativeAlternativePaymentTokenizationResponse> =
        repository.tokenize(request)

    override suspend fun deleteCustomerToken(
        request: PODeleteCustomerTokenRequest
    ): ProcessOutResult<Unit> =
        repository.deleteCustomerToken(request)

    override suspend fun createCustomerToken(
        request: POCreateCustomerTokenRequest
    ): ProcessOutResult<POCustomerToken> =
        repository.createCustomerToken(request)

    override suspend fun createCustomer(
        request: POCreateCustomerRequest
    ): ProcessOutResult<POCustomer> =
        repository.createCustomer(request)
}
