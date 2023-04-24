package com.processout.sdk.api.repository

import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.utils.findBy
import com.squareup.moshi.Moshi
import retrofit2.Response

internal fun <T : Any> Response<T>.toFailure(moshi: Moshi): ProcessOutResult.Failure {
    val adapter = moshi.adapter(POFailure.ApiError::class.java)
    val errorBodyString = errorBody()?.string()
    val apiError = errorBodyString?.let {
        adapter.fromJson(errorBodyString)
    }

    val message = "Status code: ${code()}".let {
        errorBodyString?.let { error ->
            it.plus(" | Reason: $error")
        } ?: it
    }

    val failureCode = apiError?.errorType?.let {
        failureCode(code(), it)
    } ?: POFailure.Code.Internal()

    return ProcessOutResult.Failure(failureCode, message, apiError?.invalidFields)
}

private fun failureCode(httpStatusCode: Int, errorType: String): POFailure.Code {
    val failureCode = when (httpStatusCode) {
        401 -> POFailure.AuthenticationCode::rawValue.findBy(errorType)
            ?.let { POFailure.Code.Authentication(it) }
        404 -> POFailure.NotFoundCode::rawValue.findBy(errorType)
            ?.let { POFailure.Code.NotFound(it) }
        in 400..599 ->
            POFailure.ValidationCode::rawValue.findBy(errorType)
                ?.let { POFailure.Code.Validation(it) }
                ?: POFailure.GenericCode::rawValue.findBy(errorType)
                    ?.let { POFailure.Code.Generic(it) }
                ?: POFailure.TimeoutCode::rawValue.findBy(errorType)
                    ?.let { POFailure.Code.Timeout(it) }
                ?: POFailure.InternalCode::rawValue.findBy(errorType)
                    ?.let { POFailure.Code.Internal(it) }
        else -> null
    }
    return failureCode ?: POFailure.Code.Unknown(errorType)
}
