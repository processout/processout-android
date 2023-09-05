package com.processout.sdk.core

import android.os.Parcelable

/**
 * Provides [Success] or [Failure] as a result of operation.
 */
sealed class ProcessOutResult<out T : Any> {
    /**
     * Provides successful result with a value.
     */
    data class Success<out T : Any>(val value: T) : ProcessOutResult<T>()

    /**
     * Provides detailed information about an error that occurred.
     */
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

fun <T : Any> ProcessOutResult<T>.copy(): ProcessOutResult<T> =
    when (this) {
        is ProcessOutResult.Success -> this.copy()
        is ProcessOutResult.Failure -> this.copy()
    }

inline fun <T : Any, R : Any> ProcessOutResult<T>.map(
    transform: (T) -> R
): ProcessOutResult<R> = when (this) {
    is ProcessOutResult.Success -> ProcessOutResult.Success(transform(value))
    is ProcessOutResult.Failure -> this.copy()
}

internal fun <T : Parcelable> ProcessOutResult<T>.toActivityResult(): ProcessOutActivityResult<T> =
    when (this) {
        is ProcessOutResult.Success -> ProcessOutActivityResult.Success(value)
        is ProcessOutResult.Failure -> ProcessOutActivityResult.Failure(code, message)
    }
