package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.request.napm.v2.NativeAlternativePaymentAuthorizationRequestBody
import com.processout.sdk.api.model.request.napm.v2.NativeAlternativePaymentAuthorizationRequestBody.SubmitData
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentAuthorizationRequest
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentAuthorizationRequest.Parameter.PhoneNumber
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentAuthorizationRequest.Parameter.Value
import com.processout.sdk.api.model.response.*
import com.processout.sdk.api.model.response.napm.v2.NativeAlternativePaymentAuthorizationResponseBody
import com.processout.sdk.api.model.response.napm.v2.NativeAlternativePaymentAuthorizationResponseBody.NextStep
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse
import com.processout.sdk.api.network.HeaderConstants.CLIENT_SECRET
import com.processout.sdk.api.network.InvoicesApi
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
        api.authorizeInvoice(
            invoiceId = request.invoiceId,
            request = request.withDeviceData(),
            clientSecret = request.clientSecret
        )
    }

    override suspend fun authorizeInvoice(
        request: PONativeAlternativePaymentAuthorizationRequest
    ) = apiCall {
        api.authorizeInvoice(
            invoiceId = request.invoiceId,
            request = request.toBody()
        )
    }.map { it.toModel() }

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
            saveSource = saveSource,
            incremental = incremental,
            preferredScheme = preferredScheme,
            thirdPartySdkVersion = thirdPartySdkVersion,
            overrideMacBlocking = overrideMacBlocking,
            initialSchemeTransactionId = initialSchemeTransactionId,
            autoCaptureAt = autoCaptureAt,
            captureAmount = captureAmount,
            allowFallbackToSale = allowFallbackToSale,
            metadata = metadata,
            deviceData = contextGraph.deviceData
        )

    private fun PONativeAlternativePaymentMethodRequest.toBody() =
        NativeAPMRequestBody(
            gatewayConfigurationId = gatewayConfigurationId,
            nativeApm = NativeAPMRequestParameters(parameterValues = parameters)
        )

    private fun PONativeAlternativePaymentAuthorizationRequest.toBody() =
        NativeAlternativePaymentAuthorizationRequestBody(
            gatewayConfigurationId = gatewayConfigurationId,
            submitData = parameters?.let { SubmitData(parameters = it.map()) }
        )

    private fun Map<String, PONativeAlternativePaymentAuthorizationRequest.Parameter>.map() =
        mapValues { (_, parameter) ->
            when (parameter) {
                is Value -> NativeAlternativePaymentAuthorizationRequestBody.Parameter(
                    value = parameter.value,
                    dialingCode = null
                )
                is PhoneNumber -> NativeAlternativePaymentAuthorizationRequestBody.Parameter(
                    value = parameter.value,
                    dialingCode = parameter.dialingCode
                )
            }
        }

    private fun NativeAlternativePaymentAuthorizationResponseBody.toModel() =
        PONativeAlternativePaymentAuthorizationResponse(
            state = state,
            nextStep = nextStep?.let {
                when (it) {
                    is NextStep.SubmitData -> PONativeAlternativePaymentAuthorizationResponse.NextStep.SubmitData(
                        parameterDefinitions = it.parameters.parameterDefinitions
                    )
                    is NextStep.Redirect -> PONativeAlternativePaymentAuthorizationResponse.NextStep.Redirect(
                        url = it.parameters.url
                    )
                    NextStep.Unknown -> PONativeAlternativePaymentAuthorizationResponse.NextStep.Unknown
                }
            },
            customerInstructions = customerInstructions
        )

    private fun ProcessOutResult<Response<InvoiceResponse>>.map() = fold(
        onSuccess = { response ->
            response.body()?.let { invoice ->
                ProcessOutResult.Success(
                    invoice.toModel(clientSecret = response.headers()[CLIENT_SECRET])
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
            customerId = customerId,
            transaction = transaction,
            paymentMethods = paymentMethods,
            clientSecret = clientSecret
        )
    }
