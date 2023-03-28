package com.processout.sdk.api.network

import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.response.POCustomerResponse
import com.processout.sdk.api.model.response.POCustomerTokenResponse
import retrofit2.Response
import retrofit2.http.*

internal interface CustomerTokensApi {

    @PUT("/customers/{customer_id}/tokens/{token_id}")
    suspend fun assignCustomerToken(
        @Path("customer_id") customerId: String,
        @Path("token_id") tokenId: String,
        @Body request: POAssignCustomerTokenRequestWithDeviceData
    ): Response<POCustomerTokenResponse>

    @POST("/customers/{customer_id}/tokens")
    suspend fun createCustomerToken(
        @Path("customer_id") customerId: String,
    ): Response<POCustomerTokenResponse>

    @POST("/customers")
    suspend fun createCustomer(@Body request: POCreateCustomerRequest): Response<POCustomerResponse>
}
