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

    val failureCode: POFailure.Code? = when (code()) {
        401 -> POFailure.AuthenticationCode::rawValue.findBy(apiError?.errorType)
            ?.let { POFailure.Code.Authentication(it) }
        404 -> POFailure.NotFoundCode::rawValue.findBy(apiError?.errorType)
            ?.let { POFailure.Code.NotFound(it) }
        in 400..499 ->
            POFailure.ValidationCode::rawValue.findBy(apiError?.errorType)
                ?.let { POFailure.Code.Validation(it) }
                ?: POFailure.GenericCode::rawValue.findBy(apiError?.errorType)
                    ?.let { POFailure.Code.Generic(it) }
        in 500..599 -> POFailure.Code.Internal
        else -> POFailure.Code.Unknown
    }

    return ProcessOutResult.Failure(
        message,
        failureCode ?: POFailure.Code.Unknown,
        apiError?.invalidFields
    )
}
