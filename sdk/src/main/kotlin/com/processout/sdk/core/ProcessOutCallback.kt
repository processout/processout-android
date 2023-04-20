package com.processout.sdk.core

interface ProcessOutCallback<in T : Any> {
    fun onSuccess(result: T)
    fun onFailure(
        code: POFailure.Code,
        message: String? = null,
        invalidFields: List<POFailure.InvalidField>? = null,
        cause: Exception? = null
    )
}
