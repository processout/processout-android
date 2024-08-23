package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.response.*
import com.processout.sdk.api.network.InvoicesApi
import com.processout.sdk.api.network.InvoicesApi.Companion.HEADER_CLIENT_SECRET
import com.processout.sdk.core.*
import com.processout.sdk.di.ContextGraph
import kotlinx.coroutines.launch
import retrofit2.Response

internal class DefaultInvoicesRepository(
    failureMapper: ApiFailureMapper,
    private val api: InvoicesApi,
    private val contextGraph: ContextGraph
) : BaseRepository(failureMapper, contextGraph.mainScope), InvoicesRepository {

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

    override suspend fun invoice(request: POInvoiceRequest) =
        plainApiCall {
            api.invoice(
                invoiceId = request.invoiceId,
                clientSecret = request.clientSecret
            )
        }.map()

    override fun invoice(
        request: POInvoiceRequest,
        callback: ProcessOutCallback<POInvoice>
    ) {
        repositoryScope.launch {
            invoice(request)
                .onSuccess {
                    callback.onSuccess(it)
                }.onFailure {
                    with(it) {
                        callback.onFailure(code, message, invalidFields, cause)
                    }
                }
        }
    }

    override suspend fun createInvoice(request: POCreateInvoiceRequest) =
        plainApiCall { api.createInvoice(request) }.map()

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

    private fun ProcessOutResult<Response<InvoiceResponse>>.map() = fold(
        onSuccess = { response ->
            response.body()?.let { invoice ->
                ProcessOutResult.Success(
                    invoice.toModel(clientSecret = response.headers()[HEADER_CLIENT_SECRET])
                )
            } ?: response.nullBodyFailure()
        },
        onFailure = { it }
    )
}

private fun NativeAlternativePaymentMethodResponse.toModel() = nativeApm

private fun NativeAlternativePaymentMethodTransactionDetailsResponse.toModel() = nativeApm

private fun CaptureResponse.toModel() = nativeApm

private fun InvoiceResponse.toModel(clientSecret: String?) =
    with(invoice) {
        POInvoice(
            id = id,
            amount = amount,
            currency = currency,
            returnUrl = returnUrl,
            transaction = transaction,
            paymentMethods = paymentMethods,
            clientSecret = clientSecret
        )
    }
