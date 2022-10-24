package com.processout.sdk.core

sealed class ProcessOutResult<out T : Any> {
    data class Success<out T : Any>(val value: T) : ProcessOutResult<T>()
    data class Failure(
        val message: String,
        val cause: Exception? = null
    ) : ProcessOutResult<Nothing>()
}

inline fun <T : Any> ProcessOutResult<T>.handleSuccess(
    block: (value: T) -> Unit
) {
    if (this is ProcessOutResult.Success) {
        block(value)
    }
}

inline fun <T : Any> ProcessOutResult<T>.handleFailure(
    block: (message: String, cause: Exception?) -> Unit
) {
    if (this is ProcessOutResult.Failure) {
        block(message, cause)
    }
}

inline fun <T : Any, R : Any> ProcessOutResult<T>.map(
    transform: (T) -> R
): ProcessOutResult<R> = when (this) {
    is ProcessOutResult.Success -> ProcessOutResult.Success(transform(value))
    is ProcessOutResult.Failure -> this.copy()
}
