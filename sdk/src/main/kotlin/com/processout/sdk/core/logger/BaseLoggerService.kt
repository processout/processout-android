package com.processout.sdk.core.logger

import android.os.Build
import com.processout.sdk.BuildConfig
import java.util.Calendar

internal abstract class BaseLoggerService(
    private val minimumLevel: POLogLevel
) : POLoggerService {

    companion object {
        private const val MAX_TAG_LENGTH_BEFORE_API_26 = 23
        private const val ATTRIBUTE_LINE = "Line"
    }

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
            // Find first element in the stack trace that do not belongs to logger package.
            // This element refers to the class that actually invoked logging.
            loggerPackageName?.let {
                element.className.startsWith(it).not()
            } ?: false
        }
        val additionalAttributes = mutableMapOf(
            ATTRIBUTE_LINE to stackTraceElement?.lineNumber.toString()
        )
        attributes?.let { additionalAttributes.putAll(it) }

        log(
            LogEvent(
                level = level,
                tag = createTag(stackTraceElement),
                message = formattedMessage,
                timestamp = Calendar.getInstance().time,
                attributes = additionalAttributes
            )
        )
    }

    private fun createTag(element: StackTraceElement?): String {
        val tag = element?.className
            ?.substringAfterLast(".")
            ?.substringBefore("$")
            ?: BuildConfig.LIBRARY_NAME
        // Tag length limit was removed in Android 8.0 (API 26).
        return if (tag.length <= MAX_TAG_LENGTH_BEFORE_API_26 || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            tag else tag.take(MAX_TAG_LENGTH_BEFORE_API_26)
    }

    protected abstract fun log(event: LogEvent)
}
