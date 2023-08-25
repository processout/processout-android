package com.processout.sdk.core.logger

import android.util.Log
import com.processout.sdk.core.annotation.ProcessOutInternalApi

@ProcessOutInternalApi
enum class POLogLevel(val priority: Int) {
    DEBUG(Log.DEBUG),
    INFO(Log.INFO),
    WARN(Log.WARN),
    ERROR(Log.ERROR)
}
