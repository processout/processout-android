package com.processout.sdk.api.service

import com.processout.sdk.api.model.request.LogRequest
import com.processout.sdk.api.repository.LogsRepository
import com.processout.sdk.core.logger.BaseLoggerService
import com.processout.sdk.core.logger.LogEvent
import com.processout.sdk.core.logger.LogLevel

internal class RemoteLoggerService(
    minimumLevel: LogLevel,
    private val repository: LogsRepository
) : BaseLoggerService(minimumLevel) {

    override fun log(event: LogEvent) {
        repository.send(event.toRequest())
    }

}

private fun LogEvent.toRequest() = LogRequest(
    level = level.name.lowercase(),
    tag = tag,
    message = message,
    timestamp = timestamp,
    attributes = attributes
)
