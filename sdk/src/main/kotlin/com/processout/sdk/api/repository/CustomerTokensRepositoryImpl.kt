package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.response.POCustomerTokenSuccess
import com.processout.sdk.api.network.CustomerTokensApi
import com.processout.sdk.api.repository.extension.parseResponse
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.exception.ProcessOutException
import com.processout.sdk.core.map
import com.processout.sdk.di.ContextGraph
import com.squareup.moshi.Moshi
import kotlinx.coroutines.launch

internal class CustomerTokensRepositoryImpl(
    private val api: CustomerTokensApi,
    private val contextGraph: ContextGraph,
    private val moshi: Moshi
) : BaseRepository(), CustomerTokensRepository {

    override suspend fun assignCustomerToken(
        customerId: String,
        tokenId: String,
        request: POCustomerTokenRequest
    ) = when (val apiResult = apiCall {
            api.assignCustomerToken(customerId, tokenId, request.toDeviceDataRequest(contextGraph.deviceData))
        }) {
            is ProcessOutResult.Success -> {
                val parsedResponse = apiResult.value.customerAction.parseResponse(moshi)
                parsedResponse?.let {
                    ProcessOutResult.Success(POCustomerTokenSuccess(apiResult.value.token, it))
                } ?: ProcessOutResult.Success(POCustomerTokenSuccess(apiResult.value.token,null))
            }
            is ProcessOutResult.Failure -> apiResult
        }

    override fun assignCustomerToken(
        customerId: String,
        tokenId: String,
        request: POCustomerTokenRequest,
        callback: ProcessOutCallback<POCustomerTokenSuccess>
    ) {
        repositoryScope.launch {
            when (val apiResult = apiCall {
                api.assignCustomerToken(customerId, tokenId, request.toDeviceDataRequest(contextGraph.deviceData))
            }) {
                is ProcessOutResult.Success -> {
                    val parsedResponse = apiResult.value.customerAction.parseResponse(moshi)
                    parsedResponse?.let {
                        callback.onSuccess(POCustomerTokenSuccess(apiResult.value.token, it))
                    } ?: callback.onSuccess(POCustomerTokenSuccess(apiResult.value.token,null))
                }
                is ProcessOutResult.Failure -> callback.onFailure(
                    apiResult.cause ?: ProcessOutException(apiResult.message)
                )
            }
        }
    }

    // <--- Calls meant to be used for testing --->

    override suspend fun createCustomerToken(
        customerId: String
    ) = when (val apiResult = apiCall {
        api.createCustomerToken(customerId)
    }) {
        is ProcessOutResult.Success -> {
            val parsedResponse = apiResult.value.customerAction.parseResponse(moshi)
            parsedResponse?.let {
                ProcessOutResult.Success(POCustomerTokenSuccess(apiResult.value.token, it))
            } ?: ProcessOutResult.Success(POCustomerTokenSuccess(apiResult.value.token,null))
        }
        is ProcessOutResult.Failure -> apiResult
    }

    override fun createCustomerToken(
        customerId: String,
        callback: ProcessOutCallback<POCustomerTokenSuccess>
    ) {
        repositoryScope.launch {
            when (val apiResult = apiCall {
                api.createCustomerToken(customerId)
            }) {
                is ProcessOutResult.Success -> {
                    val parsedResponse = apiResult.value.customerAction.parseResponse(moshi)
                    parsedResponse?.let {
                        callback.onSuccess(POCustomerTokenSuccess(apiResult.value.token, it))
                    } ?: callback.onSuccess(POCustomerTokenSuccess(apiResult.value.token,null))
                }
                is ProcessOutResult.Failure -> callback.onFailure(
                    apiResult.cause ?: ProcessOutException(apiResult.message)
                )
            }
        }
    }

    override suspend fun createCustomer(request: POCreateCustomerRequest) =
        apiCall { api.createCustomer(request) }.map { it.customer }
}

// <--- CustomerTokens Private Functions --->
private fun POCustomerTokenRequest.toDeviceDataRequest(deviceData: PODeviceData) =
    POCustomerTokenRequestWithDeviceData(
        source,
        threeDS2Enabled,
        verify,
        metadata,
        verifyMetadata,
        thirdPartySDKVersion,
        preferredScheme,
        verificationInvoiceUID,
        manualInvoiceCancellation,
        description,
        returnURL,
        cancelURL,
        deviceData
    )
