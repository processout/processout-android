package com.processout.sdk.core

interface ProcessOutCallback<in T : Any> {
    fun onSuccess(result: T)
    fun onFailure(
        message: String,
        code: POFailure.Code,
        invalidFields: List<POFailure.InvalidField>? = null,
        cause: Exception? = null
    )
}
