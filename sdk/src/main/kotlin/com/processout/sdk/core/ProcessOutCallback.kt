package com.processout.sdk.core

/**
 * Provides [onSuccess] or [onFailure] callbacks as a result of operation.
 */
interface ProcessOutCallback<in T : Any> {
    /**
     * Callback with successful result.
     */
    fun onSuccess(result: T)

    /**
     * Callback with detailed information about an error that occurred.
     */
    fun onFailure(
        code: POFailure.Code,
        message: String? = null,
        invalidFields: List<POFailure.InvalidField>? = null,
        cause: Exception? = null
    )
}
