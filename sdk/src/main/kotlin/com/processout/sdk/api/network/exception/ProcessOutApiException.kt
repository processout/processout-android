package com.processout.sdk.api.network.exception

import com.processout.sdk.core.exception.ProcessOutException

open class ProcessOutApiException(
    message: String,
    val code: Int,
    val apiError: ProcessOutApiError? = null
) : ProcessOutException(message)
