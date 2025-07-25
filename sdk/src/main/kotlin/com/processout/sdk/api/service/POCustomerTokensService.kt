package com.processout.sdk.api.service

import com.processout.sdk.api.model.request.POAssignCustomerTokenRequest
import com.processout.sdk.api.model.request.POCreateCustomerRequest
import com.processout.sdk.api.model.request.POCreateCustomerTokenRequest
import com.processout.sdk.api.model.request.PODeleteCustomerTokenRequest
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentTokenizationRequest
import com.processout.sdk.api.model.response.POCustomer
import com.processout.sdk.api.model.response.POCustomerToken
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentTokenizationResponse
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow

/**
 * Provides functionality related to customer tokens.
 */
interface POCustomerTokensService {

    /**
     * Subscribe to this flow to collect result from [assignCustomerToken] invocation.
     */
    @Deprecated(message = "Use function: assign(request, threeDSService)")
    val assignCustomerTokenResult: SharedFlow<ProcessOutResult<POCustomerToken>>

    /**
     * Assign new source to the existing customer token and optionally verify it
     * with the given request and 3DS service implementation.
     * Collect the result by subscribing to [assignCustomerTokenResult] flow before invoking this function.
     * Returns coroutine job.
     */
    @Deprecated(
        message = "Use replacement function.",
        replaceWith = ReplaceWith("assign(request, threeDSService)")
    )
    fun assignCustomerToken(
        request: POAssignCustomerTokenRequest,
        threeDSService: PO3DSService
    ): Job

    /**
     * Assign new source to the existing customer token and optionally verify it
     * with the given request and 3DS service implementation.
     * Result provided in the callback.
     * Returns coroutine job.
     */
    @Deprecated(
        message = "Use replacement function.",
        replaceWith = ReplaceWith("assign(request, threeDSService)")
    )
    fun assignCustomerToken(
        request: POAssignCustomerTokenRequest,
        threeDSService: PO3DSService,
        callback: (ProcessOutResult<POCustomerToken>) -> Unit
    ): Job

    /**
     * Assign new source to the existing customer token and optionally verify it
     * with the given request and 3DS service implementation.
     */
    suspend fun assign(
        request: POAssignCustomerTokenRequest,
        threeDSService: PO3DSService
    ): ProcessOutResult<POCustomerToken>

    /**
     * Tokenize native alternative payment.
     */
    suspend fun tokenize(
        request: PONativeAlternativePaymentTokenizationRequest
    ): ProcessOutResult<PONativeAlternativePaymentTokenizationResponse>

    /**
     * Deletes customer token.
     */
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
