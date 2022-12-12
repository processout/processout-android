package com.processout.sdk.api.repository

import androidx.annotation.RestrictTo
import com.processout.sdk.api.model.request.POCreateCustomerRequest
import com.processout.sdk.api.model.request.POCustomerTokenRequest
import com.processout.sdk.api.model.request.POCustomerTokenRequestWithDeviceData
import com.processout.sdk.api.model.request.PODeviceData
import com.processout.sdk.api.model.response.POCustomerToken
import com.processout.sdk.api.model.response.POCustomerTokenResponse
import com.processout.sdk.api.network.CustomerTokensApi
import com.processout.sdk.api.repository.shared.parseResponse
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.map
import com.processout.sdk.di.ContextGraph
import com.squareup.moshi.Moshi

internal class CustomerTokensRepositoryImpl(
    moshi: Moshi,
    private val api: CustomerTokensApi,
    private val contextGraph: ContextGraph
) : BaseRepository(moshi), CustomerTokensRepository {

    override suspend fun assignCustomerToken(
        customerId: String,
        tokenId: String,
        request: POCustomerTokenRequest
    ) = apiCall {
        api.assignCustomerToken(customerId, tokenId, request.toDeviceDataRequest(contextGraph.deviceData))
    }.map { it.toModel(moshi) }

    override fun assignCustomerToken(
        customerId: String,
        tokenId: String,
        request: POCustomerTokenRequest,
        callback: ProcessOutCallback<POCustomerToken>
    ) = apiCallScoped(callback, { it.toModel(moshi) }) {
        api.assignCustomerToken(customerId, tokenId, request.toDeviceDataRequest(contextGraph.deviceData))
    }

    // <--- Calls meant to be used for testing --->

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    override suspend fun createCustomerToken(customerId: String) =
        apiCall { api.createCustomerToken(customerId) }.map { it.toModel(moshi) }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    override fun createCustomerToken(
        customerId: String,
        callback: ProcessOutCallback<POCustomerToken>
    ) = apiCallScoped(callback, { it.toModel(moshi) }) {
        api.createCustomerToken(customerId)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    override suspend fun createCustomer(request: POCreateCustomerRequest) =
        apiCall { api.createCustomer(request) }.map { it.customer }
}

// <--- CustomerTokens Private Functions --->

private fun POCustomerTokenRequest.toDeviceDataRequest(deviceData: PODeviceData) =
    POCustomerTokenRequestWithDeviceData(
        source,
        threeDS2Enabled,
        verify,
        metadata,
        verifyMetadata,
        thirdPartySDKVersion,
        preferredScheme,
        verificationInvoiceUID,
        manualInvoiceCancellation,
        description,
        returnURL,
        cancelURL,
        deviceData
    )

private fun POCustomerTokenResponse.toModel(moshi: Moshi) =
    POCustomerToken(token, customerAction.parseResponse(moshi))
