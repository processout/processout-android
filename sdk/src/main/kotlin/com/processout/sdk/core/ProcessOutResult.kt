package com.processout.sdk.core

import android.os.Parcelable
import com.processout.sdk.core.ProcessOutResult.Failure
import com.processout.sdk.core.ProcessOutResult.Success

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
    if (this is Success) {
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
    if (this is Failure) {
        block(code, message, invalidFields, cause)
    }
}

fun <T : Any> ProcessOutResult<T>.copy(): ProcessOutResult<T> =
    when (this) {
        is Success -> this.copy()
        is Failure -> this.copy()
    }

inline fun <T : Any, R : Any> ProcessOutResult<T>.map(
    transform: (T) -> R
): ProcessOutResult<R> = when (this) {
    is Success -> Success(transform(value))
    is Failure -> this.copy()
}

fun <T : Parcelable> ProcessOutResult<T>.toActivityResult(): ProcessOutActivityResult<T> =
    when (this) {
        is Success -> ProcessOutActivityResult.Success(value)
        is Failure -> ProcessOutActivityResult.Failure(code, message)
    }
