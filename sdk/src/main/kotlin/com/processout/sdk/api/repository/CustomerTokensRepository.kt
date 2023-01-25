package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.POCreateCustomerRequest
import com.processout.sdk.api.model.request.POCustomerTokenRequest
import com.processout.sdk.api.model.response.POCustomer
import com.processout.sdk.api.model.response.POCustomerToken
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi

interface CustomerTokensRepository {

    suspend fun assignCustomerToken(
        customerId: String,
        tokenId: String,
        request: POCustomerTokenRequest
    ): ProcessOutResult<POCustomerToken>

    fun assignCustomerToken(
        customerId: String,
        tokenId: String,
        request: POCustomerTokenRequest,
        callback: ProcessOutCallback<POCustomerToken>
    )

    @ProcessOutInternalApi
    suspend fun createCustomerToken(
        customerId: String,
    ): ProcessOutResult<POCustomerToken>

    @ProcessOutInternalApi
    fun createCustomerToken(
        customerId: String,
        callback: ProcessOutCallback<POCustomerToken>
    )

    @ProcessOutInternalApi
    suspend fun createCustomer(request: POCreateCustomerRequest): ProcessOutResult<POCustomer>
}
