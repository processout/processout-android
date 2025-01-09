package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.POAssignCustomerTokenRequest
import com.processout.sdk.api.model.request.POCreateCustomerRequest
import com.processout.sdk.api.model.request.POCreateCustomerTokenRequest
import com.processout.sdk.api.model.request.PODeleteCustomerTokenRequest
import com.processout.sdk.api.model.response.CustomerTokenResponse
import com.processout.sdk.api.model.response.POCustomer
import com.processout.sdk.api.model.response.POCustomerToken
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi

internal interface CustomerTokensRepository {

    suspend fun assignCustomerToken(
        request: POAssignCustomerTokenRequest
    ): ProcessOutResult<CustomerTokenResponse>

    suspend fun deleteCustomerToken(
        request: PODeleteCustomerTokenRequest
    ): ProcessOutResult<Unit>

    /** @suppress */
    @ProcessOutInternalApi
    suspend fun createCustomerToken(
        request: POCreateCustomerTokenRequest
    ): ProcessOutResult<POCustomerToken>

    /** @suppress */
    @ProcessOutInternalApi
    suspend fun createCustomer(
        request: POCreateCustomerRequest
    ): ProcessOutResult<POCustomer>
}
