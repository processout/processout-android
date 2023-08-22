package com.processout.sdk.core.logger

import android.util.Log

internal class SystemLoggerService(
    minimumLevel: POLogLevel
) : BaseLoggerService(minimumLevel) {

    companion object {
        private const val MAX_LOG_LENGTH = 4000
    }

    override fun log(event: LogEvent) {
        val message = "${event.message} ${event.attributes}"
        message.chunked(MAX_LOG_LENGTH).forEach {
            Log.println(event.level.priority, event.tag, it)
        }
    }
}
