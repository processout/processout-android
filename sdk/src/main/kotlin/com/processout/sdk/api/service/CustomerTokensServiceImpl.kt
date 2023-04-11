package com.processout.sdk.api.service

import com.processout.sdk.api.model.request.POAssignCustomerTokenRequest
import com.processout.sdk.api.model.request.POCreateCustomerRequest
import com.processout.sdk.api.model.response.POCustomer
import com.processout.sdk.api.model.response.POCustomerToken
import com.processout.sdk.api.repository.CustomerTokensRepository
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.processout.sdk.core.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class CustomerTokensServiceImpl(
    private val scope: CoroutineScope,
    private val repository: CustomerTokensRepository,
    private val threeDSService: ThreeDSService
) : CustomerTokensService {

    override fun assignCustomerToken(
        request: POAssignCustomerTokenRequest,
        threeDSHandler: PO3DSHandler,
        callback: (PO3DSResult<POCustomerToken>) -> Unit
    ) {
        scope.launch {
            when (val result = repository.assignCustomerToken(request)) {
                is ProcessOutResult.Success ->
                    result.value.customerAction?.let { action ->
                        threeDSService.handle(action, threeDSHandler) { serviceResult ->
                            when (serviceResult) {
                                is PO3DSResult.Success ->
                                    assignCustomerToken(
                                        request.copy(source = serviceResult.value),
                                        threeDSHandler,
                                        callback
                                    )
                                is PO3DSResult.Failure -> callback(serviceResult.copy())
                            }
                        }
                    } ?: run {
                        threeDSHandler.cleanup()
                        callback(PO3DSResult.Success(result.value.token))
                    }
                is ProcessOutResult.Failure -> {
                    threeDSHandler.cleanup()
                    callback(result.to3DSFailure())
                }
            }
        }
    }

    @ProcessOutInternalApi
    override suspend fun createCustomerToken(
        customerId: String
    ): ProcessOutResult<POCustomerToken> =
        repository.createCustomerToken(customerId).map { it.token }

    @ProcessOutInternalApi
    override suspend fun createCustomer(
        request: POCreateCustomerRequest
    ): ProcessOutResult<POCustomer> =
        repository.createCustomer(request)
}
