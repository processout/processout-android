package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.LogRequest

internal interface LogsRepository {

    fun send(request: LogRequest)
}
