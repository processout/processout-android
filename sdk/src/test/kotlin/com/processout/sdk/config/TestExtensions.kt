package com.processout.sdk.config

import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.handleFailure
import com.processout.sdk.core.handleSuccess
import com.processout.sdk.core.rawValue

fun <T : Any> ProcessOutResult<T>.assertFailure() {
    handleSuccess { println(it) }
    handleFailure { code, message, _, cause ->
        throw AssertionError("$message | ${code.rawValue}", cause)
    }
}
