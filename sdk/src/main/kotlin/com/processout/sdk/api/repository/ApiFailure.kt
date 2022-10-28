package com.processout.sdk.api.repository

import com.processout.sdk.api.network.exception.AuthenticationException
import com.processout.sdk.api.network.exception.NotFoundException
import com.processout.sdk.api.network.exception.ServerException
import com.processout.sdk.api.network.exception.ValidationException
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.exception.ProcessOutException
import retrofit2.Response

internal fun <T : Any> apiFailure(response: Response<T>): ProcessOutResult.Failure {
    val code = response.code()
    val message = "Status code: $code".let {
        response.errorBody()?.string()?.let { error ->
            it.plus(" | Reason: $error")
        } ?: it
    }

    var exception: ProcessOutException? = null
    when (code) {
        401 -> exception = AuthenticationException(code, message)
        404 -> exception = NotFoundException(code, message)
        in 400..499 -> exception = ValidationException(code, message)
        in 500..599 -> exception = ServerException(code, message)
    }
    return ProcessOutResult.Failure(message, exception)
}
