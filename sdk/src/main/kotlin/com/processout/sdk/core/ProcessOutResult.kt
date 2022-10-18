package com.processout.sdk.core

sealed class ProcessOutResult<out T : Any> {
    data class Success<out T : Any>(val value: T) : ProcessOutResult<T>()
    data class Failure(
        val message: String,
        val cause: Throwable? = null
    ) : ProcessOutResult<Nothing>()
}
