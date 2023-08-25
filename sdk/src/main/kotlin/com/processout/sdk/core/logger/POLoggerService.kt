package com.processout.sdk.core.logger

import com.processout.sdk.core.annotation.ProcessOutInternalApi

@ProcessOutInternalApi
interface POLoggerService {

    fun log(
        level: POLogLevel,
        message: String,
        vararg args: Any?,
        attributes: Map<String, String>?
    )
}
