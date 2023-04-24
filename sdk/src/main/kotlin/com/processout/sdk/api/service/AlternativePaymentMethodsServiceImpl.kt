package com.processout.sdk.api.service

import android.net.Uri
import com.processout.sdk.api.model.request.POAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse.APMReturnType
import com.processout.sdk.core.POFailure.*
import com.processout.sdk.core.POFailure.Code.*
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.utils.findBy

internal class AlternativePaymentMethodsServiceImpl(
    private val configuration: AlternativePaymentMethodsConfiguration
) : AlternativePaymentMethodsService {

    override fun alternativePaymentMethodUri(request: POAlternativePaymentMethodRequest): ProcessOutResult<Uri> {
        var pathComponents = arrayListOf(
            configuration.projectId, request.invoiceId, "redirect",
            request.gatewayConfigurationId
        )
        if (request.customerId != null && request.tokenId != null) {
            pathComponents = arrayListOf(
                configuration.projectId, request.customerId.toString(),
                request.tokenId.toString(), "redirect", request.gatewayConfigurationId
            )
        }
        val uriBuilder = Uri.parse(configuration.baseUrl).buildUpon()
        pathComponents.forEach { pathComponent ->
            uriBuilder.appendPath(pathComponent)
        }
        request.additionalData?.forEach {
            uriBuilder.appendQueryParameter("additional_data[" + it.key + "]", it.value)
        }
        return ProcessOutResult.Success(uriBuilder.build())
    }

    override fun alternativePaymentMethodResponse(uri: Uri): ProcessOutResult<POAlternativePaymentMethodResponse> {
        val errorMessage = "Invalid or malformed Alternative Payment Method URL: $uri"
        if (uri.isOpaque)
            return ProcessOutResult.Failure(code = Internal(), message = errorMessage)

        uri.getQueryParameter("error_code")?.let { errorCode ->
            return ProcessOutResult.Failure(failureCode(errorCode))
        }

        val gatewayToken = uri.getQueryParameter("token")
            ?: return ProcessOutResult.Failure(code = Internal(), message = errorMessage)

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
