package com.processout.sdk.api.network

import com.processout.sdk.api.model.request.TelemetryRequest
import com.processout.sdk.api.model.response.TelemetryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

internal interface TelemetryApi {

    @POST("/telemetry")
    suspend fun send(@Body request: TelemetryRequest): Response<TelemetryResponse>
}
