package com.processout.sdk.api.network

import com.processout.sdk.api.model.request.LogRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

internal interface LogsApi {

    @POST("/logs")
    suspend fun send(@Body request: LogRequest): Response<Unit>
}
