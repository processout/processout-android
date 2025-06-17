package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.request.napm.v2.NativeAlternativePaymentRequestBody
import com.processout.sdk.api.model.request.napm.v2.NativeAlternativePaymentRequestBody.SubmitData
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentAuthorizationDetailsRequest
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentAuthorizationRequest
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentSubmitData.Parameter
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentSubmitData.Parameter.Value
import com.processout.sdk.api.model.response.*
import com.processout.sdk.api.model.response.napm.v2.NativeAlternativePaymentAuthorizationResponseBody
import com.processout.sdk.api.model.response.napm.v2.NativeAlternativePaymentElement
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentElement
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

    override suspend fun nativeAlternativePayment(
        request: PONativeAlternativePaymentAuthorizationDetailsRequest
    ) = apiCall {
        api.nativeAlternativePayment(
            invoiceId = request.invoiceId,
            gatewayConfigurationId = request.gatewayConfigurationId
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
        NativeAlternativePaymentRequestBody(
            gatewayConfigurationId = gatewayConfigurationId,
            submitData = submitData?.let { SubmitData(parameters = it.parameters.map()) }
        )

    private fun Map<String, Parameter>.map() =
        mapValues { (_, parameter) ->
            when (val value = parameter.value) {
                is Value.String -> value.value
                is Value.PhoneNumber -> value
            }
        }

    private fun NativeAlternativePaymentAuthorizationResponseBody.toModel() =
        PONativeAlternativePaymentAuthorizationResponse(
            state = state,
//            invoice = invoice, // TODO(v2): uncomment
//            paymentMethod = paymentMethod, // TODO(v2): uncomment
            elements = elements?.map {
                when (it) {
                    is NativeAlternativePaymentElement.Form ->
                        PONativeAlternativePaymentElement.Form(
                            parameterDefinitions = it.parameters.parameterDefinitions
                        )
                    is NativeAlternativePaymentElement.CustomerInstruction ->
                        PONativeAlternativePaymentElement.CustomerInstruction(
                            instruction = it.instruction
                        )
                    is NativeAlternativePaymentElement.CustomerInstructionGroup ->
                        PONativeAlternativePaymentElement.CustomerInstructionGroup(
                            label = it.label,
                            instructions = it.instructions
                        )
                    NativeAlternativePaymentElement.Unknown ->
                        PONativeAlternativePaymentElement.Unknown
                }
            },
            redirect = redirect
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
