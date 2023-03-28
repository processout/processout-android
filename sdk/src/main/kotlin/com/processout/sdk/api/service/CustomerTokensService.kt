package com.processout.sdk.api.service

import com.processout.sdk.api.model.request.POAssignCustomerTokenRequest
import com.processout.sdk.api.model.request.POCreateCustomerRequest
import com.processout.sdk.api.model.response.POCustomer
import com.processout.sdk.api.model.response.POCustomerToken
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi

interface CustomerTokensService {

    fun assignCustomerToken(
        request: POAssignCustomerTokenRequest,
        threeDSHandler: PO3DSHandler,
        callback: (PO3DSResult<POCustomerToken>) -> Unit
    )

    @ProcessOutInternalApi
    suspend fun createCustomerToken(
        customerId: String,
    ): ProcessOutResult<POCustomerToken>

    @ProcessOutInternalApi
    suspend fun createCustomer(request: POCreateCustomerRequest): ProcessOutResult<POCustomer>
}
