package com.processout.sdk.api.repository

import androidx.annotation.RestrictTo
import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.response.*
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.ProcessOutResult

interface CustomerTokensRepository {

    suspend fun assignCustomerToken(
        customerId: String,
        tokenId: String,
        request: POCustomerTokenRequest
    ): ProcessOutResult<POCustomerTokenSuccess>

    fun assignCustomerToken(
        customerId: String,
        tokenId: String,
        request: POCustomerTokenRequest,
        callback: ProcessOutCallback<POCustomerTokenSuccess>
    )

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    suspend fun createCustomerToken(
        customerId: String,
    ): ProcessOutResult<POCustomerTokenSuccess>

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun createCustomerToken(
        customerId: String,
        callback: ProcessOutCallback<POCustomerTokenSuccess>
    )

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    suspend fun createCustomer(request: POCreateCustomerRequest): ProcessOutResult<POCustomer>
}
