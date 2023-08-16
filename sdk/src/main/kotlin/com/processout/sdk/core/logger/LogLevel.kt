package com.processout.sdk.core.logger

import android.util.Log

internal enum class LogLevel(val priority: Int) {
    DEBUG(Log.DEBUG),
    INFO(Log.INFO),
    WARN(Log.WARN),
    ERROR(Log.ERROR)
}
