package com.processout.sdk.api.service

import com.processout.sdk.api.model.request.POAssignCustomerTokenRequest
import com.processout.sdk.api.model.request.POCreateCustomerRequest
import com.processout.sdk.api.model.response.POCustomer
import com.processout.sdk.api.model.response.POCustomerToken
import com.processout.sdk.api.repository.CustomerTokensRepository
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class CustomerTokensServiceImpl(
    private val scope: CoroutineScope,
    private val repository: CustomerTokensRepository,
    private val threeDSService: ThreeDSService
) : POCustomerTokensService {

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
                        } ?: callback(ProcessOutResult.Failure(POFailure.Code.Internal()))
                    }
                is ProcessOutResult.Failure -> {
                    threeDSService.cleanup()
                    callback(result.copy())
                }
            }
        }
    }

    @ProcessOutInternalApi
    override suspend fun createCustomerToken(
        customerId: String
    ): ProcessOutResult<POCustomerToken> =
        repository.createCustomerToken(customerId)

    @ProcessOutInternalApi
    override suspend fun createCustomer(
        request: POCreateCustomerRequest
    ): ProcessOutResult<POCustomer> =
        repository.createCustomer(request)
}
