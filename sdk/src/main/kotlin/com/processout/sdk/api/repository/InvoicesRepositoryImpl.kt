package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.response.*
import com.processout.sdk.api.network.InvoicesApi
import com.processout.sdk.api.repository.shared.parseResponse
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.processout.sdk.core.map
import com.processout.sdk.di.ContextGraph
import com.squareup.moshi.Moshi

internal class InvoicesRepositoryImpl(
    moshi: Moshi,
    private val api: InvoicesApi,
    private val contextGraph: ContextGraph
) : BaseRepository(moshi), InvoicesRepository {

    override suspend fun authorize(
        invoiceId: String,
        request: POInvoiceAuthorizationRequest
    ) = apiCall {
        api.authorize(invoiceId, request.toDeviceDataRequest(contextGraph.deviceData))
    }.map { it.toModel(moshi) }

    override fun authorize(
        invoiceId: String,
        request: POInvoiceAuthorizationRequest,
        callback: ProcessOutCallback<POInvoiceAuthorizationSuccess>
    ) = apiCallScoped(callback, { it.toModel(moshi) }) {
        api.authorize(invoiceId, request.toDeviceDataRequest(contextGraph.deviceData))
    }

    override suspend fun capture(
        invoiceId: String,
        gatewayConfigurationId: String
    ) = apiCall {
        api.capture(
            invoiceId,
            PONativeAlternativePaymentCaptureRequest(gatewayConfigurationId)
        )
    }.map { POCaptureSuccess }

    override fun capture(
        invoiceId: String,
        gatewayConfigurationId: String,
        callback: ProcessOutCallback<POCaptureSuccess>
    ) = apiCallScoped(callback, { POCaptureSuccess }) {
        api.capture(
            invoiceId,
            PONativeAlternativePaymentCaptureRequest(gatewayConfigurationId)
        )
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

    // <--- Calls meant to be used for testing --->

    @ProcessOutInternalApi
    override suspend fun createInvoice(request: POCreateInvoiceRequest) =
        apiCall { api.createInvoice(request) }.map { it.invoice }
}

// <--- Native APM Payment Private Functions --->

private fun PONativeAlternativePaymentMethodRequest.toBody() =
    PONativeAPMRequestBody(
        gatewayConfigurationId,
        PONativeAPMRequestParameters(parameters)
    )

private fun PONativeAlternativePaymentMethodResponse.toModel() = nativeApm

// <--- Authorization Private Functions --->

private fun POInvoiceAuthorizationRequest.toDeviceDataRequest(deviceData: PODeviceData) =
    POInvoiceAuthorizationRequestWithDeviceData(
        source,
        incremental,
        enableThreeDS2,
        preferredScheme,
        thirdPartySdkVersion,
        invoiceDetailsIds,
        overrideMacBlocking,
        initialSchemeTransactionId,
        metadata,
        deviceData
    )

private fun POInvoiceAuthorizationResponse.toModel(moshi: Moshi) =
    POInvoiceAuthorizationSuccess(customerAction.parseResponse(moshi))

// <--- Native APM Transaction Details --->

private fun PONativeAlternativePaymentMethodTransactionDetailsResponse.toModel() = nativeApm
