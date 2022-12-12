package com.processout.sdk.api.network.exception

import com.processout.sdk.core.exception.ProcessOutException

class ValidationException(
    val code: Int,
    message: String,
    val apiError: ApiError? = null
) : ProcessOutException(message)
