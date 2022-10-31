package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.request.PONativeAPMRequestBody
import com.processout.sdk.api.model.request.PONativeAPMRequestParameters
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethod
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodResponse
import com.processout.sdk.api.network.InvoicesApi
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.map
import com.processout.sdk.di.ContextGraph

internal class InvoicesRepositoryImpl(
    private val api: InvoicesApi,
    private val contextGraph: ContextGraph
) : BaseRepository(), InvoicesRepository {

    override suspend fun initiatePayment(request: PONativeAlternativePaymentMethodRequest) =
        apiCall {
            api.initiatePayment(request.invoiceId, request.toBody())
        }.map { it.toModel() }

    override fun initiatePayment(
        request: PONativeAlternativePaymentMethodRequest,
        callback: ProcessOutCallback<PONativeAlternativePaymentMethod>
    ) = apiCallScoped(callback, PONativeAlternativePaymentMethodResponse::toModel) {
        api.initiatePayment(request.invoiceId, request.toBody())
    }

    override suspend fun createInvoice(request: POCreateInvoiceRequest) =
        apiCall { api.createInvoice(request) }.map { it.invoice }
}

private fun PONativeAlternativePaymentMethodRequest.toBody() =
    PONativeAPMRequestBody(
        gatewayConfigurationId,
        PONativeAPMRequestParameters(parameters)
    )

private fun PONativeAlternativePaymentMethodResponse.toModel() = nativeApm
