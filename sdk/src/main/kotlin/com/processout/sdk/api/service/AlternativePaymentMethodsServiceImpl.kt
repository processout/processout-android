package com.processout.sdk.api.service

import android.net.Uri
import com.processout.sdk.api.model.request.POAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult

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
        if (uri.host != "processout.return") {
            return ProcessOutResult.Failure(
                POFailure.Code.Internal(),
                "Invalid or malformed Alternative Payment Method URL."
            )
        }

        val gatewayToken = uri.getQueryParameter("token")
            ?: return ProcessOutResult.Failure(
                POFailure.Code.Internal(),
                "Invalid or malformed Alternative Payment Method URL."
            )

        var returnType = POAlternativePaymentMethodResponse.APMReturnType.AUTHORIZATION
        val customerTokenId = uri.getQueryParameter("token_id")
        val customerId = uri.getQueryParameter("customer_id")

        if (customerTokenId != null && customerId != null) {
            returnType = POAlternativePaymentMethodResponse.APMReturnType.CREATE_TOKEN
        }

        return ProcessOutResult.Success(
            POAlternativePaymentMethodResponse(
                gatewayToken,
                customerId,
                customerTokenId,
                returnType
            )
        )
    }
}
