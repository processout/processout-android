package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.response.*
import com.processout.sdk.api.network.InvoicesApi
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.map
import com.processout.sdk.di.ContextGraph

internal class DefaultInvoicesRepository(
    failureMapper: ApiFailureMapper,
    private val api: InvoicesApi,
    private val contextGraph: ContextGraph
) : BaseRepository(failureMapper), InvoicesRepository {

    override suspend fun authorizeInvoice(
        request: POInvoiceAuthorizationRequest
    ) = apiCall {
        api.authorizeInvoice(request.invoiceId, request.withDeviceData())
    }

    override suspend fun initiatePayment(
        request: PONativeAlternativePaymentMethodRequest
    ) = apiCall {
        api.initiatePayment(request.invoiceId, request.toBody())
    }.map { it.toModel() }

    override fun initiatePayment(
        request: PONativeAlternativePaymentMethodRequest,
        callback: ProcessOutCallback<PONativeAlternativePaymentMethod>
    ) = apiCallScoped(callback, NativeAlternativePaymentMethodResponse::toModel) {
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
    ) = apiCallScoped(callback, NativeAlternativePaymentMethodTransactionDetailsResponse::toModel) {
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
    ) = apiCallScoped(callback, CaptureResponse::toModel) {
        api.captureNativeAlternativePayment(
            invoiceId,
            NativeAlternativePaymentCaptureRequest(gatewayConfigurationId)
        )
    }

    override suspend fun invoice(invoiceId: String) =
        apiCall {
            api.invoice(
                invoiceId = invoiceId,
                clientSecret = null
            )
        }.map { it.invoice }

    override fun invoice(
        invoiceId: String,
        callback: ProcessOutCallback<POInvoice>
    ) = apiCallScoped(callback, InvoiceResponse::toModel) {
        api.invoice(
            invoiceId = invoiceId,
            clientSecret = null
        )
    }

    override suspend fun createInvoice(request: POCreateInvoiceRequest) =
        apiCall { api.createInvoice(request) }.map { it.invoice }

    private fun POInvoiceAuthorizationRequest.withDeviceData() =
        InvoiceAuthorizationRequestWithDeviceData(
            source = source,
            incremental = incremental,
            enableThreeDS2 = enableThreeDS2,
            preferredScheme = preferredScheme,
            thirdPartySdkVersion = thirdPartySdkVersion,
            invoiceDetailsIds = invoiceDetailsIds,
            overrideMacBlocking = overrideMacBlocking,
            initialSchemeTransactionId = initialSchemeTransactionId,
            autoCaptureAt = autoCaptureAt,
            captureAmount = captureAmount,
            authorizeOnly = authorizeOnly,
            allowFallbackToSale = allowFallbackToSale,
            metadata = metadata,
            deviceData = contextGraph.deviceData
        )

    private fun PONativeAlternativePaymentMethodRequest.toBody() =
        NativeAPMRequestBody(
            gatewayConfigurationId = gatewayConfigurationId,
            nativeApm = NativeAPMRequestParameters(parameterValues = parameters)
        )
}

private fun NativeAlternativePaymentMethodResponse.toModel() = nativeApm

private fun NativeAlternativePaymentMethodTransactionDetailsResponse.toModel() = nativeApm

private fun CaptureResponse.toModel() = nativeApm

private fun InvoiceResponse.toModel() = invoice
