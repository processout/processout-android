package com.processout.sdk.core.logger

import android.util.Log
import com.processout.sdk.BuildConfig

internal class SystemLoggerService(
    minimumLevel: POLogLevel
) : BaseLoggerService(minimumLevel) {

    companion object {
        private const val MAX_LOG_LENGTH = 4000
    }

    override fun log(event: LogEvent) {
        val message = buildString {
            append("[")
            append(event.simpleClassName)
            append(":")
            append(event.lineNumber)
            append("]")
            append(" ")
            append(event.message)
            event.attributes?.let {
                if (it.isNotEmpty()) {
                    append(" ")
                    append(it)
                }
            }
        }
        message.chunked(MAX_LOG_LENGTH).forEach {
            Log.println(event.level.priority, BuildConfig.LIBRARY_PACKAGE_NAME, it)
        }
    }
}
