package com.processout.sdk.api.service

import com.processout.sdk.api.model.request.POAssignCustomerTokenRequest
import com.processout.sdk.api.model.request.POCreateCustomerRequest
import com.processout.sdk.api.model.request.POCreateCustomerTokenRequest
import com.processout.sdk.api.model.response.POCustomer
import com.processout.sdk.api.model.response.POCustomerToken
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.coroutines.flow.SharedFlow

/**
 * Provides functionality related to customer tokens.
 */
interface POCustomerTokensService {

    /**
     * Subscribe to this flow to collect result from [assignCustomerToken] invocation.
     */
    val assignCustomerTokenResult: SharedFlow<ProcessOutResult<POCustomerToken>>

    /**
     * Assign new source to existing customer token and optionally verify it
     * with the given request and 3DS service implementation.
     * Collect result by subscribing to [assignCustomerTokenResult] flow before invoking token assignment.
     */
    fun assignCustomerToken(
        request: POAssignCustomerTokenRequest,
        threeDSService: PO3DSService
    )

    @Deprecated(
        message = "Use function assignCustomerToken(request, threeDSService)",
        replaceWith = ReplaceWith("assignCustomerToken(request, threeDSService)")
    )
    fun assignCustomerToken(
        request: POAssignCustomerTokenRequest,
        threeDSService: PO3DSService,
        callback: (ProcessOutResult<POCustomerToken>) -> Unit
    )

    /** @suppress */
    @ProcessOutInternalApi
    suspend fun createCustomerToken(
        request: POCreateCustomerTokenRequest
    ): ProcessOutResult<POCustomerToken>

    /** @suppress */
    @ProcessOutInternalApi
    suspend fun createCustomer(request: POCreateCustomerRequest): ProcessOutResult<POCustomer>
}
