package com.processout.sdk.api.network

import com.processout.sdk.api.network.exception.*
import com.processout.sdk.core.ProcessOutResult
import com.squareup.moshi.Moshi
import retrofit2.Response

internal fun <T : Any> Response<T>.toFailure(moshi: Moshi): ProcessOutResult.Failure {
    val adapter = moshi.adapter(ProcessOutApiError::class.java)
    val errorBodyString = errorBody()?.string()
    val apiError = errorBodyString?.let {
        adapter.fromJson(errorBodyString)
    }

    val message = "Status code: ${code()}".let {
        errorBodyString?.let { error ->
            it.plus(" | Reason: $error")
        } ?: it
    }

    val exception = when (code()) {
        401 -> AuthenticationException(message, code(), apiError)
        404 -> NotFoundException(message, code(), apiError)
        in 400..499 -> ValidationException(message, code(), apiError)
        in 500..599 -> ServerException(message, code(), apiError)
        else -> ProcessOutApiException(message, code(), apiError)
    }
    return ProcessOutResult.Failure(message, exception)
}
