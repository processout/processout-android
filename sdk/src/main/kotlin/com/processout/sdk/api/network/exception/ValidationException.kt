package com.processout.sdk.api.network.exception

class ValidationException(
    message: String,
    code: Int,
    apiError: ProcessOutApiError? = null
) : ProcessOutApiException(message, code, apiError)
