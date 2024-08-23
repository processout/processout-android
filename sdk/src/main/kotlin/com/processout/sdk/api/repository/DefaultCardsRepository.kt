package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.response.CardIssuerInformationResponse
import com.processout.sdk.api.model.response.CardResponse
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.api.model.response.POCardIssuerInformation
import com.processout.sdk.api.network.CardsApi
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.map
import com.processout.sdk.di.ContextGraph

internal class DefaultCardsRepository(
    failureMapper: ApiFailureMapper,
    private val api: CardsApi,
    private val contextGraph: ContextGraph
) : BaseRepository(failureMapper, contextGraph.mainScope), POCardsRepository {

    override suspend fun tokenize(
        request: POCardTokenizationRequest
    ) = apiCall {
        api.tokenize(request.withDeviceData())
    }.map { it.toModel() }

    override fun tokenize(
        request: POCardTokenizationRequest,
        callback: ProcessOutCallback<POCard>
    ) = apiCallScoped(callback, CardResponse::toModel) {
        api.tokenize(request.withDeviceData())
    }

    override suspend fun updateCard(
        request: POCardUpdateRequest
    ) = apiCall {
        api.updateCard(request.cardId, request.toBody())
    }.map { it.toModel() }

    override fun updateCard(
        request: POCardUpdateRequest,
        callback: ProcessOutCallback<POCard>
    ) = apiCallScoped(callback, CardResponse::toModel) {
        api.updateCard(request.cardId, request.toBody())
    }

    @Deprecated(
        message = "Use replacement function.",
        replaceWith = ReplaceWith("updateCard(request)")
    )
    override suspend fun updateCVC(
        cardId: String,
        request: POCardUpdateCVCRequest
    ) = apiCall { api.updateCVC(cardId, request) }.map { it.toModel() }

    @Deprecated(
        message = "Use replacement function.",
        replaceWith = ReplaceWith("updateCard(request, callback)")
    )
    override fun updateCVC(
        cardId: String,
        request: POCardUpdateCVCRequest,
        callback: ProcessOutCallback<POCard>
    ) = apiCallScoped(callback, CardResponse::toModel) {
        api.updateCVC(cardId, request)
    }

    override suspend fun fetchIssuerInformation(iin: String) =
        apiCall { api.fetchIssuerInformation(iin) }.map { it.toModel() }

    override fun fetchIssuerInformation(
        iin: String,
        callback: ProcessOutCallback<POCardIssuerInformation>
    ) = apiCallScoped(callback, CardIssuerInformationResponse::toModel) {
        api.fetchIssuerInformation(iin)
    }

    private fun POCardTokenizationRequest.withDeviceData() =
        CardTokenizationRequestWithDeviceData(
            metadata = metadata,
            number = number,
            expMonth = expMonth,
            expYear = expYear,
            cvc = cvc,
            name = name,
            contact = contact,
            preferredScheme = preferredScheme,
            tokenType = tokenType?.value ?: String(),
            paymentToken = paymentToken,
            deviceData = contextGraph.deviceData
        )

    private fun POCardUpdateRequest.toBody() = CardUpdateRequestBody(cvc = cvc)
}

private fun CardResponse.toModel() = card

private fun CardIssuerInformationResponse.toModel() = cardInformation
