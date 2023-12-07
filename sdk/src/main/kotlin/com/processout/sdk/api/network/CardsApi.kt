package com.processout.sdk.api.network

import com.processout.sdk.api.model.request.CardUpdateRequestBody
import com.processout.sdk.api.model.request.CardTokenizationRequestWithDeviceData
import com.processout.sdk.api.model.request.POCardUpdateCVCRequest
import com.processout.sdk.api.model.response.CardIssuerInformationResponse
import com.processout.sdk.api.model.response.CardResponse
import retrofit2.Response
import retrofit2.http.*

internal interface CardsApi {

    @POST("/cards")
    suspend fun tokenize(
        @Body request: CardTokenizationRequestWithDeviceData
    ): Response<CardResponse>

    @PUT("/cards/{id}")
    suspend fun updateCard(
        @Path("id") cardId: String,
        @Body request: CardUpdateRequestBody
    ): Response<CardResponse>

    @Deprecated(
        message = "Use replacement function.",
        replaceWith = ReplaceWith("updateCard(cardId, request)")
    )
    @PUT("/cards/{id}")
    suspend fun updateCVC(
        @Path("id") cardId: String,
        @Body request: POCardUpdateCVCRequest
    ): Response<CardResponse>

    @GET("/iins/{iin}")
    suspend fun fetchIssuerInformation(
        @Path("iin") iin: String
    ): Response<CardIssuerInformationResponse>
}
