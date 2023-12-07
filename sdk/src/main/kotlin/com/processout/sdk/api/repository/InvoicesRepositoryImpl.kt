package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.response.*
import com.processout.sdk.api.network.InvoicesApi
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.map
import com.processout.sdk.di.ContextGraph
import com.squareup.moshi.Moshi

internal class InvoicesRepositoryImpl(
    moshi: Moshi,
    private val api: InvoicesApi,
    private val contextGraph: ContextGraph
) : BaseRepository(moshi), InvoicesRepository {

    override suspend fun authorizeInvoice(
        request: POInvoiceAuthorizationRequest
    ) = apiCall {
        api.authorizeInvoice(request.invoiceId, request.toDeviceDataRequest())
    }

    override suspend fun initiatePayment(
        request: PONativeAlternativePaymentMethodRequest
    ) = apiCall {
        api.initiatePayment(request.invoiceId, request.toBody())
    }.map { it.toModel() }

    override fun initiatePayment(
        request: PONativeAlternativePaymentMethodRequest,
        callback: ProcessOutCallback<PONativeAlternativePaymentMethod>
    ) = apiCallScoped(callback, PONativeAlternativePaymentMethodResponse::toModel) {
        api.initiatePayment(request.invoiceId, request.toBody())
    }

    override suspend fun fetchNativeAlternativePaymentMethodTransactionDetails(
        invoiceId: String,
        gatewayConfigurationId: String
    ) = apiCall {
        api.fetchNativeAlternativePaymentMethodTransactionDetails(
            invoiceId, gatewayConfigurationId
        )
    }.map { it.toModel() }

    override fun fetchNativeAlternativePaymentMethodTransactionDetails(
        invoiceId: String,
        gatewayConfigurationId: String,
        callback: ProcessOutCallback<PONativeAlternativePaymentMethodTransactionDetails>
    ) = apiCallScoped(callback, PONativeAlternativePaymentMethodTransactionDetailsResponse::toModel) {
        api.fetchNativeAlternativePaymentMethodTransactionDetails(
            invoiceId, gatewayConfigurationId
        )
    }

    override suspend fun captureNativeAlternativePayment(
        invoiceId: String,
        gatewayConfigurationId: String
    ) = apiCall {
        api.captureNativeAlternativePayment(
            invoiceId,
            NativeAlternativePaymentCaptureRequest(gatewayConfigurationId)
        )
    }.map { it.toModel() }

    override fun captureNativeAlternativePayment(
        invoiceId: String,
        gatewayConfigurationId: String,
        callback: ProcessOutCallback<PONativeAlternativePaymentMethodCapture>
    ) = apiCallScoped(callback, POCaptureResponse::toModel) {
        api.captureNativeAlternativePayment(
            invoiceId,
            NativeAlternativePaymentCaptureRequest(gatewayConfigurationId)
        )
    }

    override suspend fun createInvoice(request: POCreateInvoiceRequest) =
        apiCall { api.createInvoice(request) }.map { it.invoice }

    private fun POInvoiceAuthorizationRequest.toDeviceDataRequest() =
        InvoiceAuthorizationRequestWithDeviceData(
            source,
            incremental,
            enableThreeDS2,
            preferredScheme,
            thirdPartySdkVersion,
            invoiceDetailsIds,
            overrideMacBlocking,
            initialSchemeTransactionId,
            autoCaptureAt,
            captureAmount,
            authorizeOnly,
            allowFallbackToSale,
            metadata,
            contextGraph.deviceData
        )

    private fun PONativeAlternativePaymentMethodRequest.toBody() =
        NativeAPMRequestBody(
            gatewayConfigurationId,
            NativeAPMRequestParameters(parameters)
        )
}

private fun PONativeAlternativePaymentMethodResponse.toModel() = nativeApm

private fun PONativeAlternativePaymentMethodTransactionDetailsResponse.toModel() = nativeApm

private fun POCaptureResponse.toModel() = nativeApm
