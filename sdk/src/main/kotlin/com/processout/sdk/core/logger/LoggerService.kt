package com.processout.sdk.core.logger

internal interface LoggerService {

    fun log(
        level: LogLevel,
        message: String,
        vararg args: Any?,
        attributes: Map<String, String>?
    )
}
