package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.POAssignCustomerTokenRequest
import com.processout.sdk.api.model.request.POAssignCustomerTokenRequestWithDeviceData
import com.processout.sdk.api.model.request.POCreateCustomerRequest
import com.processout.sdk.api.model.request.POCreateCustomerTokenRequest
import com.processout.sdk.api.network.CustomerTokensApi
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.map
import com.processout.sdk.di.ContextGraph
import com.squareup.moshi.Moshi

internal class CustomerTokensRepositoryImpl(
    moshi: Moshi,
    private val api: CustomerTokensApi,
    private val contextGraph: ContextGraph
) : BaseRepository(moshi), CustomerTokensRepository {

    override suspend fun assignCustomerToken(
        request: POAssignCustomerTokenRequest
    ) = apiCall {
        api.assignCustomerToken(
            request.customerId,
            request.tokenId,
            request.toDeviceDataRequest()
        )
    }

    override suspend fun createCustomerToken(request: POCreateCustomerTokenRequest) =
        apiCall { api.createCustomerToken(request.customerId, request.body) }.let { result ->
            when (result) {
                is ProcessOutResult.Success -> result.value.token?.let { token ->
                    ProcessOutResult.Success(token)
                } ?: ProcessOutResult.Failure(POFailure.Code.Internal())
                is ProcessOutResult.Failure -> result.copy()
            }
        }

    override suspend fun createCustomer(request: POCreateCustomerRequest) =
        apiCall { api.createCustomer(request) }.map { it.customer }

    private fun POAssignCustomerTokenRequest.toDeviceDataRequest() =
        POAssignCustomerTokenRequestWithDeviceData(
            source,
            preferredScheme,
            enableThreeDS2,
            verify,
            invoiceId,
            thirdPartySdkVersion,
            metadata,
            contextGraph.deviceData
        )
}
