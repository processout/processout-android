package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.POAssignCustomerTokenRequest
import com.processout.sdk.api.model.request.POCreateCustomerRequest
import com.processout.sdk.api.model.response.POCustomer
import com.processout.sdk.api.model.response.POCustomerTokenResponse
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi

internal interface CustomerTokensRepository {

    suspend fun assignCustomerToken(
        request: POAssignCustomerTokenRequest
    ): ProcessOutResult<POCustomerTokenResponse>

    @ProcessOutInternalApi
    suspend fun createCustomerToken(
        customerId: String,
    ): ProcessOutResult<POCustomerTokenResponse>

    @ProcessOutInternalApi
    suspend fun createCustomer(request: POCreateCustomerRequest): ProcessOutResult<POCustomer>
}
