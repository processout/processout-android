package com.processout.sdk.api.network

import com.processout.sdk.api.model.request.POCardTokenizationRequest
import com.processout.sdk.api.model.request.POCardUpdateCVCRequest
import com.processout.sdk.api.model.response.POCardResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

internal interface CardsApi {

    @POST("/cards")
    suspend fun tokenize(@Body request: POCardTokenizationRequest): Response<POCardResponse>

    @PUT("/cards/{id}")
    suspend fun updateCVC(
        @Path("id") cardId: String,
        @Body request: POCardUpdateCVCRequest
    ): Response<POCardResponse>
}
