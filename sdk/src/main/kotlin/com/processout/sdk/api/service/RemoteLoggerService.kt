package com.processout.sdk.api.service

import com.processout.sdk.api.model.request.LogRequest
import com.processout.sdk.api.repository.LogsRepository
import com.processout.sdk.core.logger.BaseLoggerService
import com.processout.sdk.core.logger.LogEvent
import com.processout.sdk.core.logger.POLogLevel

internal class RemoteLoggerService(
    minimumLevel: POLogLevel,
    private val repository: LogsRepository
) : BaseLoggerService(minimumLevel) {

    companion object {
        private const val ATTRIBUTE_LINE = "Line"
    }

    override fun log(event: LogEvent) {
        repository.send(event.toRequest())
    }

    private fun LogEvent.toRequest() = LogRequest(
        level = level.name.lowercase(),
        tag = simpleClassName,
        message = message,
        timestamp = timestamp,
        attributes = mutableMapOf(
            ATTRIBUTE_LINE to lineNumber.toString()
        ).apply { attributes?.let { putAll(it) } }
    )
}
