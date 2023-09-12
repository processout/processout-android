package com.processout.sdk.api.service

import com.processout.sdk.api.model.request.POAssignCustomerTokenRequest
import com.processout.sdk.api.model.request.POCreateCustomerRequest
import com.processout.sdk.api.model.response.POCustomer
import com.processout.sdk.api.model.response.POCustomerToken
import com.processout.sdk.api.repository.CustomerTokensRepository
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class CustomerTokensServiceImpl(
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
                        this@CustomerTokensServiceImpl.threeDSService
                            .handle(action, threeDSService) { serviceResult ->
                                when (serviceResult) {
                                    is ProcessOutResult.Success ->
                                        assignCustomerToken(
                                            request.copy(source = serviceResult.value),
                                            threeDSService
                                        )
                                    is ProcessOutResult.Failure -> scope.launch {
                                        _assignCustomerTokenResult.emit(serviceResult.copy())
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
                                )
                            )
                        }
                    }
                is ProcessOutResult.Failure -> {
                    threeDSService.cleanup()
                    scope.launch { _assignCustomerTokenResult.emit(result.copy()) }
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
                        this@CustomerTokensServiceImpl.threeDSService
                            .handle(action, threeDSService) { serviceResult ->
                                @Suppress("DEPRECATION")
                                when (serviceResult) {
                                    is ProcessOutResult.Success ->
                                        assignCustomerToken(
                                            request.copy(source = serviceResult.value),
                                            threeDSService,
                                            callback
                                        )
                                    is ProcessOutResult.Failure -> callback(serviceResult.copy())
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
                            )
                        )
                    }
                is ProcessOutResult.Failure -> {
                    threeDSService.cleanup()
                    callback(result.copy())
                }
            }
        }
    }

    override suspend fun createCustomerToken(
        customerId: String
    ): ProcessOutResult<POCustomerToken> =
        repository.createCustomerToken(customerId)

    override suspend fun createCustomer(
        request: POCreateCustomerRequest
    ): ProcessOutResult<POCustomer> =
        repository.createCustomer(request)
}
