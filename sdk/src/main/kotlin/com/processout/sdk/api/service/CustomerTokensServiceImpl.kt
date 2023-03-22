package com.processout.sdk.api.service

import com.processout.sdk.api.model.request.POCreateCustomerRequest
import com.processout.sdk.api.model.response.POCustomer
import com.processout.sdk.api.model.response.POCustomerToken
import com.processout.sdk.api.repository.CustomerTokensRepository
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi

internal class CustomerTokensServiceImpl(
    private val repository: CustomerTokensRepository,
    private val threeDSHandler: ThreeDSHandler
) : CustomerTokensService {

    @ProcessOutInternalApi
    override suspend fun createCustomerToken(
        customerId: String
    ): ProcessOutResult<POCustomerToken> =
        repository.createCustomerToken(customerId)

    @ProcessOutInternalApi
    override fun createCustomerToken(
        customerId: String,
        callback: ProcessOutCallback<POCustomerToken>
    ) {
        repository.createCustomerToken(customerId, callback)
    }

    @ProcessOutInternalApi
    override suspend fun createCustomer(
        request: POCreateCustomerRequest
    ): ProcessOutResult<POCustomer> =
        repository.createCustomer(request)
}
