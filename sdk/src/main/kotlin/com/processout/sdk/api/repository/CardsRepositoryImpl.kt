package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.POCardTokenizationRequest
import com.processout.sdk.api.model.request.POCardUpdateCVCRequest
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.api.model.response.POCardResponse
import com.processout.sdk.api.model.response.POGatewayConfigurationResponse
import com.processout.sdk.api.network.CardsApi
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.map

internal class CardsRepositoryImpl(
    private val api: CardsApi
) : BaseRepository(), CardsRepository {
    override suspend fun tokenize(request: POCardTokenizationRequest) =
        apiCall { api.tokenize(request) }.map { it.toModel() }

    override fun tokenize(
        request:  POCardTokenizationRequest,
        callback: ProcessOutCallback<POCard>
    ) = apiCallScoped(callback, POCardResponse::toModel) { api.tokenize(request) }

    override suspend fun updateCVC(
        cardId:  String,
        request: POCardUpdateCVCRequest
    ) = apiCall { api.updateCVC(cardId, request) }.map { it.toModel() }

    override fun updateCVC(
        cardId:   String,
        request:  POCardUpdateCVCRequest,
        callback: ProcessOutCallback<POCard>
    ) =  apiCallScoped(callback, POCardResponse::toModel) { api.updateCVC(cardId, request) }
}

private fun POCardResponse.toModel() = card