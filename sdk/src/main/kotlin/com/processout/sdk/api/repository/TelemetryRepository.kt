package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.TelemetryRequest
import com.processout.sdk.api.model.response.TelemetryResponse
import com.processout.sdk.core.ProcessOutResult

internal interface TelemetryRepository {

    suspend fun send(request: TelemetryRequest): ProcessOutResult<TelemetryResponse>
}
