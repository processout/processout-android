package com.processout.example

import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.handleFailure
import com.processout.sdk.core.handleSuccess

fun <T : Any> ProcessOutResult<T>.assertFailure() {
    handleSuccess { println(it) }
    handleFailure { message, cause ->
        throw AssertionError(message, cause)
    }
}
