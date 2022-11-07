package com.processout.sdk.api.network

import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.request.PONativeAPMRequestBody
import com.processout.sdk.api.model.response.POCustomerResponse
import com.processout.sdk.api.model.response.POCustomerTokenResponse
import com.processout.sdk.api.model.response.POInvoiceResponse
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodResponse
import retrofit2.Response
import retrofit2.http.*

internal interface InvoicesApi {

    @POST("/invoices/{id}/authorize")
    suspend fun authorize(
        @Path("id") invoiceId: String,
        @Body request: POInvoiceAuthorizationRequestWithDeviceData
    ): Response<POAuthorizationResponse>

    @POST("/customers/{customer_id}/tokens")
    suspend fun createCustomerToken(
        @Path("customer_id") customerId: String,
    ): Response<POCustomerTokenResponse>

    @PUT("/customers/{customer_id}/tokens/{token_id}")
    suspend fun assignCustomerToken(
        @Path("customer_id") customerId: String,
        @Path("token_id") tokenId: String,
        @Body request: POCustomerTokenRequestWithDeviceData
    ): Response<POCustomerTokenResponse>

    @POST("/invoices/{id}/native-payment")
    suspend fun initiatePayment(
        @Path("id") invoiceId: String,
        @Body request: PONativeAPMRequestBody
    ): Response<PONativeAlternativePaymentMethodResponse>

    @POST("/invoices")
    suspend fun createInvoice(@Body request: POCreateInvoiceRequest): Response<POInvoiceResponse>

    @POST("/customers")
    suspend fun createCustomer(@Body request: POCreateCustomerRequest): Response<POCustomerResponse>
}
