package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.TelemetryRequest
import com.processout.sdk.api.network.TelemetryApi
import kotlinx.coroutines.CoroutineScope

internal class DefaultTelemetryRepository(
    failureMapper: ApiFailureMapper,
    repositoryScope: CoroutineScope,
    private val api: TelemetryApi
) : BaseRepository(failureMapper, repositoryScope), TelemetryRepository {

    override suspend fun send(request: TelemetryRequest) =
        apiCall { api.send(request) }
}
