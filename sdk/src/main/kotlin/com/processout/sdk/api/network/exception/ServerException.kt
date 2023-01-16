package com.processout.sdk.api.network.exception

class ServerException(
    message: String,
    code: Int,
    apiError: ProcessOutApiError? = null
) : ProcessOutApiException(message, code, apiError)
