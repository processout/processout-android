package com.processout.sdk.core

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

/**
 * Performs the given action on the encapsulated value if this instance represents [Success].
 * Returns the original [ProcessOutResult] unchanged.
 */
inline fun <T : Any> ProcessOutResult<T>.onSuccess(
    action: (value: T) -> Unit
): ProcessOutResult<T> {
    if (this is Success) {
        action(value)
    }
    return this
}

/**
 * Performs the given action on the encapsulated failure if this instance represents [Failure].
 * Returns the original [ProcessOutResult] unchanged.
 */
inline fun <T : Any> ProcessOutResult<T>.onFailure(
    action: (failure: Failure) -> Unit
): ProcessOutResult<T> {
    if (this is Failure) {
        action(this)
    }
    return this
}

/**
 * Returns the result of [onSuccess] for the encapsulated value if this instance represents [Success]
 * or the result of [onFailure] for the encapsulated failure if it is [Failure].
 */
inline fun <T : Any, R> ProcessOutResult<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (failure: Failure) -> R
): R = when (this) {
    is Success -> onSuccess(value)
    is Failure -> onFailure(this)
}

/**
 * Returns the encapsulated value if this instance represents [Success] or _null_ if it is [Failure].
 */
fun <T : Any> ProcessOutResult<T>.getOrNull(): T? =
    when (this) {
        is Success -> value
        is Failure -> null
    }

/**
 * Returns the encapsulated result of the given transform function applied to the encapsulated value
 * if this instance represents [Success] or the unmodified copy if it is [Failure].
 */
inline fun <T : Any, R : Any> ProcessOutResult<T>.map(
    transform: (T) -> R
): ProcessOutResult<R> = when (this) {
    is Success -> Success(transform(value))
    is Failure -> this.copy()
}

/**
 * Returns the unmodified copy of the result.
 */
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(message = "Will be removed in the next major release.")
fun <T : Any> ProcessOutResult<T>.copy(): ProcessOutResult<T> =
    when (this) {
        is Success -> this.copy()
        is Failure -> this.copy()
    }

@Deprecated(
    message = "Use replacement function.",
    replaceWith = ReplaceWith("onSuccess(action)")
)
inline fun <T : Any> ProcessOutResult<T>.handleSuccess(
    block: (value: T) -> Unit
) {
    if (this is Success) {
        block(value)
    }
}

@Deprecated(
    message = "Use replacement function.",
    replaceWith = ReplaceWith("onFailure(action)")
)
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
