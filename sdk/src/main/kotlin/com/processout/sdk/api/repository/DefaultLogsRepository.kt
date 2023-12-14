package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.LogRequest
import com.processout.sdk.api.network.LogsApi
import kotlinx.coroutines.launch

internal class DefaultLogsRepository(
    failureMapper: ApiFailureMapper,
    private val api: LogsApi
) : BaseRepository(failureMapper), LogsRepository {

    override fun send(request: LogRequest) {
        repositoryScope.launch {
            apiCall { api.send(request) }
        }
    }
}
