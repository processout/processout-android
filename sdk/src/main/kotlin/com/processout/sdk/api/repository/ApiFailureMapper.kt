package com.processout.sdk.api.repository

import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.util.findBy
import com.squareup.moshi.JsonAdapter
import retrofit2.Response

internal class ApiFailureMapper(
    private val adapter: JsonAdapter<POFailure.ApiError>
) {

    fun <T : Any> map(response: Response<T>): ProcessOutResult.Failure {
        val errorBodyString = response.errorBody()?.string()
        val apiError = errorBodyString?.let {
            adapter.fromJson(it)
        }

        val failureCode = apiError?.errorType?.let {
            failureCode(statusCode = response.code(), errorType = it)
        } ?: POFailure.Code.Internal()

        val message = buildString {
            append("Status code: ${response.code()}")
            errorBodyString?.let {
                append(" | Reason: $it")
            }
        }
        return ProcessOutResult.Failure(
            code = failureCode,
            message = message,
            invalidFields = apiError?.invalidFields
        )
    }

    private fun failureCode(statusCode: Int, errorType: String): POFailure.Code {
        val failureCode = when (statusCode) {
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
}
