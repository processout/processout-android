package com.processout.sdk.api.service

import android.net.Uri
import com.processout.sdk.api.model.request.POAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse.APMReturnType
import com.processout.sdk.core.POFailure.*
import com.processout.sdk.core.POFailure.Code.*
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.util.findBy
import com.processout.sdk.di.ContextGraph

internal class DefaultAlternativePaymentMethodsService(
    private val baseUrl: String,
    private val contextGraph: ContextGraph
) : POAlternativePaymentMethodsService {

    override fun alternativePaymentMethodUri(request: POAlternativePaymentMethodRequest): ProcessOutResult<Uri> {
        val projectId = contextGraph.configuration.projectId
        val customerId = request.customerId
        val tokenId = request.tokenId
        val pathComponents = if (customerId != null && tokenId != null) {
            arrayListOf(
                projectId,
                customerId,
                tokenId,
                "redirect",
                request.gatewayConfigurationId
            )
        } else {
            val arrayList = arrayListOf(
                projectId,
                request.invoiceId,
                "redirect",
                request.gatewayConfigurationId
            )
            if (tokenId != null) {
                arrayList.addAll(arrayListOf("tokenized", tokenId))
            }
            arrayList
        }
        val uriBuilder = Uri.parse(baseUrl).buildUpon()
        pathComponents.forEach { uriBuilder.appendPath(it) }
        request.additionalData?.forEach {
            uriBuilder.appendQueryParameter("additional_data[${it.key}]", it.value)
        }
        return ProcessOutResult.Success(uriBuilder.build())
    }

    override fun alternativePaymentMethodResponse(uri: Uri): ProcessOutResult<POAlternativePaymentMethodResponse> {
        if (uri.isOpaque) {
            return ProcessOutResult.Failure(
                code = Internal(),
                message = "Invalid or malformed alternative payment method URI: $uri"
            )
        }

        uri.getQueryParameter("error_code")?.let { errorCode ->
            return ProcessOutResult.Failure(failureCode(errorCode))
        }

        val gatewayToken = uri.getQueryParameter("token") ?: String()

        val customerId = uri.getQueryParameter("customer_id")
        val tokenId = uri.getQueryParameter("token_id")
        val returnType = if (customerId != null && tokenId != null)
            APMReturnType.CREATE_TOKEN else APMReturnType.AUTHORIZATION

        return ProcessOutResult.Success(
            POAlternativePaymentMethodResponse(
                gatewayToken = gatewayToken,
                customerId = customerId,
                tokenId = tokenId,
                returnType = returnType
            )
        )
    }

    private fun failureCode(errorCode: String): Code =
        AuthenticationCode::rawValue.findBy(errorCode)
            ?.let { Authentication(it) }
            ?: NotFoundCode::rawValue.findBy(errorCode)
                ?.let { NotFound(it) }
            ?: ValidationCode::rawValue.findBy(errorCode)
                ?.let { Validation(it) }
            ?: GenericCode::rawValue.findBy(errorCode)
                ?.let { Generic(it) }
            ?: TimeoutCode::rawValue.findBy(errorCode)
                ?.let { Timeout(it) }
            ?: InternalCode::rawValue.findBy(errorCode)
                ?.let { Internal(it) }
            ?: Unknown(errorCode)
}
