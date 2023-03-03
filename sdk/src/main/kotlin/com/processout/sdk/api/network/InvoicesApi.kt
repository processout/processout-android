package com.processout.sdk.api.network

import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.response.POCaptureResponse
import com.processout.sdk.api.model.response.POInvoiceResponse
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodResponse
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodTransactionDetailsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface InvoicesApi {

    @POST("/invoices/{id}/authorize")
    suspend fun authorize(
        @Path("id") invoiceId: String,
        @Body request: POInvoiceAuthorizationRequestWithDeviceData
    ): Response<POInvoiceAuthorizationResponse>

    @POST("/invoices/{id}/capture")
    suspend fun capture(
        @Path("id") invoiceId: String,
        @Body request: PONativeAlternativePaymentCaptureRequest
    ): Response<POCaptureResponse>

    @POST("/invoices/{id}/native-payment")
    suspend fun initiatePayment(
        @Path("id") invoiceId: String,
        @Body request: PONativeAPMRequestBody
    ): Response<PONativeAlternativePaymentMethodResponse>

    @GET("/invoices/{invoiceId}/native-payment/{gatewayConfigurationId}")
    suspend fun fetchNativeAlternativePaymentMethodTransactionDetails(
        @Path("invoiceId") invoiceId: String,
        @Path("gatewayConfigurationId") gatewayConfigurationId: String
    ): Response<PONativeAlternativePaymentMethodTransactionDetailsResponse>

    @POST("/invoices")
    suspend fun createInvoice(@Body request: POCreateInvoiceRequest): Response<POInvoiceResponse>
}
