package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.TelemetryRequest
import com.processout.sdk.api.network.TelemetryApi

internal class DefaultTelemetryRepository(
    failureMapper: ApiFailureMapper,
    private val api: TelemetryApi
) : BaseRepository(failureMapper), TelemetryRepository {

    override suspend fun send(request: TelemetryRequest) =
        apiCall { api.send(request) }
}
