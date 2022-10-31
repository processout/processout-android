package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.POCardTokenizationRequest
import com.processout.sdk.api.model.request.POCardTokenizationRequestWithDeviceData
import com.processout.sdk.api.model.request.POCardUpdateCVCRequest
import com.processout.sdk.api.model.request.PODeviceData
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.api.model.response.POCardResponse
import com.processout.sdk.api.network.CardsApi
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.map
import com.processout.sdk.di.ContextGraph

internal class CardsRepositoryImpl(
    private val api: CardsApi,
    private val contextGraph: ContextGraph
) : BaseRepository(), CardsRepository {

    override suspend fun tokenize(request: POCardTokenizationRequest) =
        apiCall {
            api.tokenize(
                request.toDeviceDataRequest(contextGraph.deviceData)
            )
        }.map { it.toModel() }

    override fun tokenize(
        request: POCardTokenizationRequest,
        callback: ProcessOutCallback<POCard>
    ) = apiCallScoped(callback, POCardResponse::toModel) {
        api.tokenize(request.toDeviceDataRequest(contextGraph.deviceData))
    }

    override suspend fun updateCVC(
        cardId: String,
        request: POCardUpdateCVCRequest
    ) = apiCall { api.updateCVC(cardId, request) }.map { it.toModel() }

    override fun updateCVC(
        cardId: String,
        request: POCardUpdateCVCRequest,
        callback: ProcessOutCallback<POCard>
    ) = apiCallScoped(callback, POCardResponse::toModel) { api.updateCVC(cardId, request) }
}

private fun POCardTokenizationRequest.toDeviceDataRequest(deviceData: PODeviceData) =
    POCardTokenizationRequestWithDeviceData(
        metadata,
        number,
        expMonth,
        expYear,
        cvc,
        name,
        contact,
        tokenType,
        paymentToken,
        deviceData
    )

private fun POCardResponse.toModel() = card
