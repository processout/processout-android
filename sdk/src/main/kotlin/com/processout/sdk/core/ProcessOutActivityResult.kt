package com.processout.sdk.core

import android.os.Parcelable
import com.processout.sdk.core.ProcessOutActivityResult.Failure
import com.processout.sdk.core.ProcessOutActivityResult.Success
import kotlinx.parcelize.Parcelize

sealed class ProcessOutActivityResult<out T : Parcelable> : Parcelable {
    @Parcelize
    data class Success<out T : Parcelable>(val value: T) : ProcessOutActivityResult<T>()

    @Parcelize
    data class Failure(
        val code: POFailure.Code,
        val message: String? = null
    ) : ProcessOutActivityResult<Nothing>()
}

fun <T : Parcelable> ProcessOutResult<T>.toActivityResult(): ProcessOutActivityResult<T> =
    when (this) {
        is ProcessOutResult.Success -> Success(value)
        is ProcessOutResult.Failure -> Failure(code, message)
    }

/**
 * Performs the given action on the encapsulated value if this instance represents [Success].
 * Returns the original [ProcessOutActivityResult] unchanged.
 */
inline fun <T : Parcelable> ProcessOutActivityResult<T>.onSuccess(
    action: (value: T) -> Unit
): ProcessOutActivityResult<T> {
    if (this is Success) {
        action(value)
    }
    return this
}

/**
 * Performs the given action on the encapsulated failure if this instance represents [Failure].
 * Returns the original [ProcessOutActivityResult] unchanged.
 */
inline fun <T : Parcelable> ProcessOutActivityResult<T>.onFailure(
    action: (failure: Failure) -> Unit
): ProcessOutActivityResult<T> {
    if (this is Failure) {
        action(this)
    }
    return this
}

/**
 * Returns the result of [onSuccess] for the encapsulated value if this instance represents [Success]
 * or the result of [onFailure] for the encapsulated failure if it is [Failure].
 */
inline fun <T : Parcelable, R> ProcessOutActivityResult<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (failure: Failure) -> R
): R = when (this) {
    is Success -> onSuccess(value)
    is Failure -> onFailure(this)
}

/**
 * Returns the encapsulated value if this instance represents [Success] or _null_ if it is [Failure].
 */
fun <T : Parcelable> ProcessOutActivityResult<T>.getOrNull(): T? =
    when (this) {
        is Success -> value
        is Failure -> null
    }

@Parcelize
data object POUnit : Parcelable
