package com.processout.sdk.core.logger

import android.util.Log

internal class SystemLoggerService(
    minimumLevel: LogLevel
) : BaseLoggerService(minimumLevel) {

    companion object {
        private const val MAX_LOG_LENGTH = 4000
    }

    override fun log(event: LogEvent) {
        val message = "${event.message} ${event.attributes}"
        // Split by line and ensure each line can fit into maximum length.
        var i = 0
        val length = message.length
        while (i < length) {
            var newline = message.indexOf('\n', i)
            newline = if (newline != -1) newline else length
            do {
                val end = minOf(newline, i + MAX_LOG_LENGTH)
                Log.println(event.level.priority, event.tag, message.substring(i, end))
                i = end
            } while (i < newline)
            i++
        }
    }
}
