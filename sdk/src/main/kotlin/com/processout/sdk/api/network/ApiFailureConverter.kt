package com.processout.sdk.api.network

import com.processout.sdk.api.network.exception.*
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.exception.ProcessOutException
import com.squareup.moshi.Moshi
import retrofit2.Response

internal fun <T : Any> Response<T>.toFailure(moshi: Moshi): ProcessOutResult.Failure {
    val adapter = moshi.adapter(ApiError::class.java)
    val errorBodyString = errorBody()?.string()
    val apiError = errorBodyString?.let {
        adapter.fromJson(errorBodyString)
    }

    val message = "Status code: ${code()}".let {
        errorBodyString?.let { error ->
            it.plus(" | Reason: $error")
        } ?: it
    }

    var exception: ProcessOutException? = null
    when (code()) {
        401 -> exception = AuthenticationException(code(), message)
        404 -> exception = NotFoundException(code(), message)
        in 400..499 -> exception = ValidationException(code(), message, apiError)
        in 500..599 -> exception = ServerException(code(), message)
    }
    return ProcessOutResult.Failure(message, exception)
}
