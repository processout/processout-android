@file:Suppress("NOTHING_TO_INLINE")

package com.processout.example.shared

import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.rawValue
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodResult

fun PONativeAlternativePaymentMethodResult.Failure.toMessage() =
    "${javaClass.simpleName}: ${code.rawValue}: $message"

fun ProcessOutResult.Failure.toMessage() =
    "${javaClass.simpleName}: ${code.rawValue}: $message"

// TODO: move those extensions to core module before next minor release.

inline fun <T : Any> ProcessOutResult<T>.onSuccess(
    action: (value: T) -> Unit
): ProcessOutResult<T> {
    if (this is ProcessOutResult.Success) {
        action(value)
    }
    return this
}

inline fun <T : Any> ProcessOutResult<T>.onFailure(
    action: (failure: ProcessOutResult.Failure) -> Unit
): ProcessOutResult<T> {
    if (this is ProcessOutResult.Failure) {
        action(this)
    }
    return this
}

inline fun <T : Any, R> ProcessOutResult<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (failure: ProcessOutResult.Failure) -> R
): R = when (this) {
    is ProcessOutResult.Success -> onSuccess(value)
    is ProcessOutResult.Failure -> onFailure(this)
}

inline fun <T : Any> ProcessOutResult<T>.getOrNull(): T? =
    when (this) {
        is ProcessOutResult.Success -> value
        is ProcessOutResult.Failure -> null
    }
