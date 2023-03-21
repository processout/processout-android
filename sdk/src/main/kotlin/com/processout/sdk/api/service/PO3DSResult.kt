package com.processout.sdk.api.service

import com.processout.sdk.core.POFailure

sealed class PO3DSResult<out T : Any> {
    data class Success<out T : Any>(val value: T) : PO3DSResult<T>()
    data class Failure(
        val code: POFailure.Code,
        val message: String? = null
    ) : PO3DSResult<Nothing>()
}
