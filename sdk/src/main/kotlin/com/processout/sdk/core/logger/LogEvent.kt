package com.processout.sdk.core.logger

import java.util.Date

internal data class LogEvent(
    val level: POLogLevel,
    val tag: String,
    val message: String,
    val timestamp: Date,
    val attributes: Map<String, String>
)
