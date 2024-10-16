package com.processout.sdk.core.logger

import java.util.Calendar

internal abstract class BaseLoggerService(
    private val minimumLevel: POLogLevel
) : POLoggerService {

    private val loggerPackageName = POLoggerService::class.java.`package`?.name

    override fun log(
        level: POLogLevel,
        message: String,
        vararg args: Any?,
        attributes: Map<String, String>?
    ) {
        if (level.priority < minimumLevel.priority)
            return

        val formattedMessage = if (args.isNotEmpty()) message.format(*args) else message

        val stackTraceElement = Throwable().stackTrace.find { element ->
            // Find first element in the stack trace that do not belong to logger package.
            // This element refers to the class that actually invoked the logging.
            loggerPackageName?.let {
                !element.className.startsWith(it)
            } ?: false
        }

        log(
            LogEvent(
                level = level,
                simpleClassName = stackTraceElement?.simpleClassName ?: "<Undefined>",
                lineNumber = stackTraceElement?.lineNumber ?: 0,
                message = formattedMessage,
                timestamp = Calendar.getInstance().time,
                attributes = attributes
            )
        )
    }

    private val StackTraceElement.simpleClassName: String
        get() = className.substringAfterLast(".").substringBefore("$")

    protected abstract fun log(event: LogEvent)
}
