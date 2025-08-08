package com.processout.sdk.api.network

import com.processout.sdk.api.model.request.InvoiceAuthorizationRequestWithDeviceData
import com.processout.sdk.api.model.request.NativeAPMRequestBody
import com.processout.sdk.api.model.request.NativeAlternativePaymentCaptureRequest
import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.request.napm.v2.NativeAlternativePaymentRequestBody
import com.processout.sdk.api.model.response.*
import com.processout.sdk.api.model.response.napm.v2.NativeAlternativePaymentAuthorizationResponseBody
import com.processout.sdk.api.network.HeaderConstants.CLIENT_SECRET
import retrofit2.Response
import retrofit2.http.*

internal interface InvoicesApi {

    @POST("/invoices/{id}/authorize")
    suspend fun authorizeInvoice(
        @Path("id") invoiceId: String,
        @Body request: InvoiceAuthorizationRequestWithDeviceData,
        @Header(CLIENT_SECRET) clientSecret: String?
    ): Response<InvoiceAuthorizationResponse>

    @POST("/invoices/{id}/apm-payment")
    suspend fun authorizeInvoice(
        @Path("id") invoiceId: String,
        @Body request: NativeAlternativePaymentRequestBody
    ): Response<NativeAlternativePaymentAuthorizationResponseBody>

    @POST("/invoices/{id}/native-payment")
    suspend fun initiatePayment(
        @Path("id") invoiceId: String,
        @Body request: NativeAPMRequestBody
    ): Response<NativeAlternativePaymentMethodResponse>

    @GET("/invoices/{invoiceId}/native-payment/{gatewayConfigurationId}")
    suspend fun fetchNativeAlternativePaymentMethodTransactionDetails(
        @Path("invoiceId") invoiceId: String,
        @Path("gatewayConfigurationId") gatewayConfigurationId: String
    ): Response<NativeAlternativePaymentMethodTransactionDetailsResponse>

    @POST("/invoices/{id}/capture")
    suspend fun captureNativeAlternativePayment(
        @Path("id") invoiceId: String,
        @Body request: NativeAlternativePaymentCaptureRequest
    ): Response<CaptureResponse>

    @GET("/invoices/{id}")
    suspend fun invoice(
        @Path("id") invoiceId: String,
        @Query("expand") expandedProperties: String?,
        @Header(CLIENT_SECRET) clientSecret: String?
    ): Response<InvoiceResponse>

    @POST("/invoices")
    suspend fun createInvoice(
        @Body request: POCreateInvoiceRequest
    ): Response<InvoiceResponse>
}
