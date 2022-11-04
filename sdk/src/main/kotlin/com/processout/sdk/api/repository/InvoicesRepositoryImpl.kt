package com.processout.sdk.api.repository

import android.net.Uri
import android.util.Base64
import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethod
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodResponse
import com.processout.sdk.api.network.InvoicesApi
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.exception.ProcessOutException
import com.processout.sdk.core.map
import com.processout.sdk.di.ContextGraph
import com.squareup.moshi.Moshi
import kotlinx.coroutines.launch

internal class InvoicesRepositoryImpl(
    private val api: InvoicesApi,
    private val contextGraph: ContextGraph,
    private val moshi: Moshi
) : BaseRepository(), InvoicesRepository {

    override suspend fun authorize(invoiceId: String, request: POInvoiceAuthorizationRequest) =
        when (val apiResult = apiCall {
            api.authorize(invoiceId, request.toDeviceDataRequest(contextGraph.deviceData))
        }) {
            is ProcessOutResult.Success -> {
                val parsedResponse = apiResult.value.parseResponse(moshi)
                parsedResponse?.let {
                    ProcessOutResult.Success(POInvoiceAuthorizeSuccess(it))
                } ?: ProcessOutResult.Success(POInvoiceAuthorizeSuccess(null))
            }
            is ProcessOutResult.Failure -> apiResult
        }

    override fun authorize(
        invoiceId: String,
        request: POInvoiceAuthorizationRequest,
        callback: ProcessOutCallback<POInvoiceAuthorizeSuccess>
    ) {
        repositoryScope.launch {
            when (val apiResult = apiCall {
                api.authorize(invoiceId, request.toDeviceDataRequest(contextGraph.deviceData))
            }) {
                is ProcessOutResult.Success -> {
                    val parsedResponse = apiResult.value.parseResponse(moshi)
                    parsedResponse?.let {
                        callback.onSuccess(POInvoiceAuthorizeSuccess(it))
                    } ?: callback.onSuccess(POInvoiceAuthorizeSuccess(null))
                }
                is ProcessOutResult.Failure -> callback.onFailure(
                    apiResult.cause ?: ProcessOutException(apiResult.message)
                )
            }
        }
    }

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
        deviceData
    )

private fun POAuthorizationResponse.parseResponse(moshi: Moshi) =
    customerAction?.let {
        when (it.type) {
            CustomerActionType.FINGERPRINT_MOBILE.value -> {
                val fingerprintData = moshi.adapter(POAuthenticationFingerprintData::class.java)
                    .fromJson(String(Base64.decode(it.value, Base64.NO_WRAP)))
                fingerprintData?.let {
                    POCustomerActionResponse.AuthenticationFingerprintData(fingerprintData)
                }
            }
            CustomerActionType.CHALLENGE_MOBILE.value -> {
                val challengeData = moshi.adapter(POAuthenticationChallengeData::class.java)
                    .fromJson(String(Base64.decode(it.value, Base64.NO_WRAP)))
                challengeData?.let {
                    POCustomerActionResponse.AuthenticationChallengeData(challengeData)
                }
            }
            CustomerActionType.URL.value -> {
                POCustomerActionResponse.UriData(Uri.parse(it.value))
            }
            CustomerActionType.REDIRECT.value -> {
                POCustomerActionResponse.UriData(Uri.parse(it.value))
            }
            CustomerActionType.FINGERPRINT.value -> {
                POCustomerActionResponse.UriData(Uri.parse(it.value))
            }
            else -> null
        }
    }
