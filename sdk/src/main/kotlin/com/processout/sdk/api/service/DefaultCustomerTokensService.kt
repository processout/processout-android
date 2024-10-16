package com.processout.sdk.api.service

import com.processout.sdk.api.model.request.POAssignCustomerTokenRequest
import com.processout.sdk.api.model.request.POCreateCustomerRequest
import com.processout.sdk.api.model.request.POCreateCustomerTokenRequest
import com.processout.sdk.api.model.response.POCustomer
import com.processout.sdk.api.model.response.POCustomerToken
import com.processout.sdk.api.repository.CustomerTokensRepository
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogAttribute
import com.processout.sdk.core.logger.POLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class DefaultCustomerTokensService(
    private val scope: CoroutineScope,
    private val repository: CustomerTokensRepository,
    private val threeDSService: ThreeDSService
) : POCustomerTokensService {

    private val _assignCustomerTokenResult = MutableSharedFlow<ProcessOutResult<POCustomerToken>>()
    override val assignCustomerTokenResult = _assignCustomerTokenResult.asSharedFlow()

    override fun assignCustomerToken(
        request: POAssignCustomerTokenRequest,
        threeDSService: PO3DSService
    ) {
        scope.launch {
            when (val result = repository.assignCustomerToken(request)) {
                is ProcessOutResult.Success ->
                    result.value.customerAction?.let { action ->
                        this@DefaultCustomerTokensService.threeDSService
                            .handle(action, threeDSService) { serviceResult ->
                                when (serviceResult) {
                                    is ProcessOutResult.Success ->
                                        assignCustomerToken(
                                            request.copy(source = serviceResult.value),
                                            threeDSService
                                        )
                                    is ProcessOutResult.Failure -> {
                                        threeDSService.cleanup()
                                        scope.launch {
                                            _assignCustomerTokenResult.emit(serviceResult)
                                        }
                                    }
                                }
                            }
                    } ?: run {
                        threeDSService.cleanup()
                        result.value.token?.let { token ->
                            scope.launch {
                                _assignCustomerTokenResult.emit(
                                    ProcessOutResult.Success(token)
                                )
                            }
                        } ?: scope.launch {
                            _assignCustomerTokenResult.emit(
                                ProcessOutResult.Failure(
                                    POFailure.Code.Internal(),
                                    "Customer token is 'null'."
                                ).also { failure ->
                                    POLogger.warn(
                                        message = "Failed to assign customer token: %s", failure,
                                        attributes = mapOf(
                                            POLogAttribute.CUSTOMER_ID to request.customerId,
                                            POLogAttribute.CUSTOMER_TOKEN_ID to request.tokenId
                                        )
                                    )
                                }
                            )
                        }
                    }
                is ProcessOutResult.Failure -> {
                    POLogger.warn(
                        message = "Failed to assign customer token: %s", result,
                        attributes = mapOf(
                            POLogAttribute.CUSTOMER_ID to request.customerId,
                            POLogAttribute.CUSTOMER_TOKEN_ID to request.tokenId
                        )
                    )
                    threeDSService.cleanup()
                    scope.launch { _assignCustomerTokenResult.emit(result) }
                }
            }
        }
    }


    @Deprecated(
        message = "Use function assignCustomerToken(request, threeDSService)",
        replaceWith = ReplaceWith("assignCustomerToken(request, threeDSService)")
    )
    override fun assignCustomerToken(
        request: POAssignCustomerTokenRequest,
        threeDSService: PO3DSService,
        callback: (ProcessOutResult<POCustomerToken>) -> Unit
    ) {
        scope.launch {
            when (val result = repository.assignCustomerToken(request)) {
                is ProcessOutResult.Success ->
                    result.value.customerAction?.let { action ->
                        this@DefaultCustomerTokensService.threeDSService
                            .handle(action, threeDSService) { serviceResult ->
                                @Suppress("DEPRECATION")
                                when (serviceResult) {
                                    is ProcessOutResult.Success ->
                                        assignCustomerToken(
                                            request.copy(source = serviceResult.value),
                                            threeDSService,
                                            callback
                                        )
                                    is ProcessOutResult.Failure -> {
                                        threeDSService.cleanup()
                                        callback(serviceResult)
                                    }
                                }
                            }
                    } ?: run {
                        threeDSService.cleanup()
                        result.value.token?.let { token ->
                            callback(ProcessOutResult.Success(token))
                        } ?: callback(
                            ProcessOutResult.Failure(
                                POFailure.Code.Internal(),
                                "Customer token is 'null'."
                            ).also { failure ->
                                POLogger.warn(
                                    message = "Failed to assign customer token: %s", failure,
                                    attributes = mapOf(
                                        POLogAttribute.CUSTOMER_ID to request.customerId,
                                        POLogAttribute.CUSTOMER_TOKEN_ID to request.tokenId
                                    )
                                )
                            }
                        )
                    }
                is ProcessOutResult.Failure -> {
                    POLogger.warn(
                        message = "Failed to assign customer token: %s", result,
                        attributes = mapOf(
                            POLogAttribute.CUSTOMER_ID to request.customerId,
                            POLogAttribute.CUSTOMER_TOKEN_ID to request.tokenId
                        )
                    )
                    threeDSService.cleanup()
                    callback(result)
                }
            }
        }
    }

    override suspend fun createCustomerToken(
        request: POCreateCustomerTokenRequest
    ): ProcessOutResult<POCustomerToken> =
        repository.createCustomerToken(request)

    override suspend fun createCustomer(
        request: POCreateCustomerRequest
    ): ProcessOutResult<POCustomer> =
        repository.createCustomer(request)
}
