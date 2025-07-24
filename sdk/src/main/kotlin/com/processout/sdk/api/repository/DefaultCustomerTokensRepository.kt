package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.request.napm.v2.NativeAlternativePaymentRequestBody
import com.processout.sdk.api.model.request.napm.v2.NativeAlternativePaymentRequestBody.SubmitData
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentSubmitData.Parameter
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentSubmitData.Parameter.Value
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentTokenizationRequest
import com.processout.sdk.api.model.response.napm.v2.NativeAlternativePaymentElement
import com.processout.sdk.api.model.response.napm.v2.NativeAlternativePaymentTokenizationResponseBody
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentElement
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentTokenizationResponse
import com.processout.sdk.api.network.CustomerTokensApi
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.map
import com.processout.sdk.di.ContextGraph

internal class DefaultCustomerTokensRepository(
    failureMapper: ApiFailureMapper,
    private val api: CustomerTokensApi,
    private val contextGraph: ContextGraph
) : BaseRepository(failureMapper, contextGraph.mainScope), CustomerTokensRepository {

    override suspend fun assignCustomerToken(
        request: POAssignCustomerTokenRequest
    ) = apiCall {
        api.assignCustomerToken(
            customerId = request.customerId,
            tokenId = request.tokenId,
            request = request.withDeviceData()
        )
    }

    override suspend fun tokenize(
        request: PONativeAlternativePaymentTokenizationRequest
    ) = apiCall {
        api.tokenize(
            customerId = request.customerId,
            tokenId = request.customerTokenId,
            request = request.toBody()
        )
    }.map { it.toModel() }

    override suspend fun deleteCustomerToken(
        request: PODeleteCustomerTokenRequest
    ) = apiCall {
        api.deleteCustomerToken(
            customerId = request.customerId,
            tokenId = request.tokenId,
            clientSecret = request.clientSecret
        )
    }

    override suspend fun createCustomerToken(request: POCreateCustomerTokenRequest) =
        apiCall { api.createCustomerToken(request.customerId, request.body) }.let { result ->
            when (result) {
                is ProcessOutResult.Success -> result.value.token?.let { token ->
                    ProcessOutResult.Success(token)
                } ?: ProcessOutResult.Failure(POFailure.Code.Internal())
                is ProcessOutResult.Failure -> result
            }
        }

    override suspend fun createCustomer(request: POCreateCustomerRequest) =
        apiCall { api.createCustomer(request) }.map { it.customer }

    private fun POAssignCustomerTokenRequest.withDeviceData() =
        AssignCustomerTokenRequestWithDeviceData(
            source = source,
            preferredScheme = preferredScheme,
            enableThreeDS2 = enableThreeDS2,
            verify = verify,
            invoiceId = invoiceId,
            thirdPartySdkVersion = thirdPartySdkVersion,
            metadata = metadata,
            deviceData = contextGraph.deviceData
        )

    private fun PONativeAlternativePaymentTokenizationRequest.toBody() =
        NativeAlternativePaymentRequestBody(
            gatewayConfigurationId = gatewayConfigurationId,
            source = null,
            submitData = submitData?.let { SubmitData(parameters = it.parameters.map()) }
        )

    private fun Map<String, Parameter>.map() =
        mapValues { (_, parameter) ->
            when (val value = parameter.value) {
                is Value.String -> value.value
                is Value.PhoneNumber -> value
            }
        }

    private fun NativeAlternativePaymentTokenizationResponseBody.toModel() =
        PONativeAlternativePaymentTokenizationResponse(
            state = state,
            paymentMethod = paymentMethod,
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
}
