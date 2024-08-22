package com.processout.sdk.api.network

import com.processout.sdk.api.model.request.InvoiceAuthorizationRequestWithDeviceData
import com.processout.sdk.api.model.request.NativeAPMRequestBody
import com.processout.sdk.api.model.request.NativeAlternativePaymentCaptureRequest
import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.response.*
import retrofit2.Response
import retrofit2.http.*

internal interface InvoicesApi {

    companion object {
        const val HEADER_CLIENT_SECRET = "x-processout-client-secret"
    }

    @POST("/invoices/{id}/authorize")
    suspend fun authorizeInvoice(
        @Path("id") invoiceId: String,
        @Body request: InvoiceAuthorizationRequestWithDeviceData
    ): Response<InvoiceAuthorizationResponse>

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

    @GET("/invoices/{id}?expand=transaction")
    suspend fun invoice(
        @Path("id") invoiceId: String,
        @Header(HEADER_CLIENT_SECRET) clientSecret: String?
    ): Response<InvoiceResponse>

    @POST("/invoices")
    suspend fun createInvoice(
        @Body request: POCreateInvoiceRequest
    ): Response<InvoiceResponse>
}
