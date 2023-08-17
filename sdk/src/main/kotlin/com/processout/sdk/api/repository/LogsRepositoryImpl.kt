package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.LogRequest
import com.processout.sdk.api.network.LogsApi
import com.squareup.moshi.Moshi
import kotlinx.coroutines.launch

internal class LogsRepositoryImpl(
    moshi: Moshi,
    private val api: LogsApi
) : BaseRepository(moshi), LogsRepository {

    override fun send(request: LogRequest) {
        repositoryScope.launch {
            apiCall { api.send(request) }
        }
    }
}
