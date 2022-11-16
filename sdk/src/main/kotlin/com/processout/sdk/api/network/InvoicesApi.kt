package com.processout.sdk.api.network

import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.request.PONativeAPMRequestBody
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

    @POST("/invoices/{id}/native-payment")
    suspend fun initiatePayment(
        @Path("id") invoiceId: String,
        @Body request: PONativeAPMRequestBody
    ): Response<PONativeAlternativePaymentMethodResponse>

    @POST("/invoices")
    suspend fun createInvoice(@Body request: POCreateInvoiceRequest): Response<POInvoiceResponse>
}
