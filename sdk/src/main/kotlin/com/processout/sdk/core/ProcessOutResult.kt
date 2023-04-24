package com.processout.sdk.core

sealed class ProcessOutResult<out T : Any> {
    data class Success<out T : Any>(val value: T) : ProcessOutResult<T>()
    data class Failure(
        val code: POFailure.Code,
        val message: String? = null,
        val invalidFields: List<POFailure.InvalidField>? = null,
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
    block: (
        code: POFailure.Code,
        message: String?,
        invalidFields: List<POFailure.InvalidField>?,
        cause: Exception?
    ) -> Unit
) {
    if (this is ProcessOutResult.Failure) {
        block(code, message, invalidFields, cause)
    }
}

inline fun <T : Any, R : Any> ProcessOutResult<T>.map(
    transform: (T) -> R
): ProcessOutResult<R> = when (this) {
    is ProcessOutResult.Success -> ProcessOutResult.Success(transform(value))
    is ProcessOutResult.Failure -> this.copy()
}
