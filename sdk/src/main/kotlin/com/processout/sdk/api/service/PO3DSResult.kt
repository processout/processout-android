package com.processout.sdk.api.service

import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult

sealed class PO3DSResult<out T : Any> {
    data class Success<out T : Any>(val value: T) : PO3DSResult<T>()
    data class Failure(
        val code: POFailure.Code,
        val message: String? = null,
        val cause: Exception? = null
    ) : PO3DSResult<Nothing>()
}

internal fun ProcessOutResult.Failure.to3DSFailure() =
    PO3DSResult.Failure(code, message, cause)
