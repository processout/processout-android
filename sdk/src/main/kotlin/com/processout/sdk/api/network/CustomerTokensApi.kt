package com.processout.sdk.api.network

import com.processout.sdk.api.model.request.AssignCustomerTokenRequestWithDeviceData
import com.processout.sdk.api.model.request.POCreateCustomerRequest
import com.processout.sdk.api.model.request.POCreateCustomerTokenRequestBody
import com.processout.sdk.api.model.response.CustomerResponse
import com.processout.sdk.api.model.response.CustomerTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

internal interface CustomerTokensApi {

    @PUT("/customers/{customer_id}/tokens/{token_id}")
    suspend fun assignCustomerToken(
        @Path("customer_id") customerId: String,
        @Path("token_id") tokenId: String,
        @Body request: AssignCustomerTokenRequestWithDeviceData
    ): Response<CustomerTokenResponse>

    @POST("/customers/{customer_id}/tokens")
    suspend fun createCustomerToken(
        @Path("customer_id") customerId: String,
        @Body request: POCreateCustomerTokenRequestBody
    ): Response<CustomerTokenResponse>

    @POST("/customers")
    suspend fun createCustomer(@Body request: POCreateCustomerRequest): Response<CustomerResponse>
}
