package com.processout.sdk.api.network

import com.processout.sdk.api.model.request.CardUpdateRequestBody
import com.processout.sdk.api.model.request.CardTokenizationRequestWithDeviceData
import com.processout.sdk.api.model.request.POCardUpdateCVCRequest
import com.processout.sdk.api.model.response.POCardIssuerInformationResponse
import com.processout.sdk.api.model.response.POCardResponse
import retrofit2.Response
import retrofit2.http.*

internal interface CardsApi {

    @POST("/cards")
    suspend fun tokenize(
        @Body request: CardTokenizationRequestWithDeviceData
    ): Response<POCardResponse>

    @PUT("/cards/{id}")
    suspend fun updateCard(
        @Path("id") cardId: String,
        @Body request: CardUpdateRequestBody
    ): Response<POCardResponse>

    @Deprecated(
        message = "Use replacement function.",
        replaceWith = ReplaceWith("updateCard(cardId, request)")
    )
    @PUT("/cards/{id}")
    suspend fun updateCVC(
        @Path("id") cardId: String,
        @Body request: POCardUpdateCVCRequest
    ): Response<POCardResponse>

    @GET("/iins/{iin}")
    suspend fun fetchIssuerInformation(
        @Path("iin") iin: String
    ): Response<POCardIssuerInformationResponse>
}
