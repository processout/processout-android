package com.processout.sdk.configuration

import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import com.processout.sdk.core.rawValue

fun <T : Any> ProcessOutResult<T>.assertFailure() {
    onSuccess { println(it) }
    onFailure {
        throw AssertionError("${it.message} | ${it.code.rawValue}", it.cause)
    }
}
