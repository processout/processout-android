package com.processout.sdk.api.repository

import com.processout.sdk.core.*
import com.processout.sdk.core.logger.POLogger
import kotlinx.coroutines.*
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

internal abstract class BaseRepository(
    private val failureMapper: ApiFailureMapper,
    protected val repositoryScope: CoroutineScope,
    private val workDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    protected suspend fun <T : Any> apiCall(
        apiMethod: suspend () -> Response<T>
    ): ProcessOutResult<T> = plainApiCall(apiMethod)
        .fold(
            onSuccess = { response ->
                response.body()?.let { ProcessOutResult.Success(it) }
                    ?: response.nullBodyFailure()
            },
            onFailure = { it }
        )

    protected suspend fun <T : Any> plainApiCall(
        apiMethod: suspend () -> Response<T>
    ): ProcessOutResult<Response<T>> = withContext(workDispatcher) {
        try {
            val response = apiMethod()
            when (response.isSuccessful) {
                true -> ProcessOutResult.Success(response)
                false -> failureMapper.map(response)
            }
        } catch (e: Exception) {
            val repositoryMethodName = apiMethod.javaClass.name
            when (e) {
                is CancellationException -> {
                    val message = "Coroutine job is cancelled: $repositoryMethodName"
                    POLogger.info("%s | %s", message, e)
                    ensureActive()
                    ProcessOutResult.Failure(POFailure.Code.Cancelled, message, cause = e)
                }
                is SocketTimeoutException -> ProcessOutResult.Failure(
                    POFailure.Code.Timeout(),
                    "Request timed out: $repositoryMethodName", cause = e
                ).also { POLogger.info("%s", it) }
                is IOException -> ProcessOutResult.Failure(
                    POFailure.Code.NetworkUnreachable,
                    "Network is unreachable: $repositoryMethodName", cause = e
                ).also { POLogger.info("%s", it) }
                else -> ProcessOutResult.Failure(
                    POFailure.Code.Internal(),
                    "Unexpected exception during API call: $repositoryMethodName", cause = e
                ).also { POLogger.error("%s", it) }
            }
        }
    }

    protected fun <T : Any> Response<T>.nullBodyFailure(): ProcessOutResult.Failure {
        val request = raw().request
        return ProcessOutResult.Failure(
            POFailure.Code.Internal(),
            "Response body is empty: ${code()} ${request.method} ${request.url}"
        ).also { POLogger.error("%s", it) }
    }

    protected fun <T : Any> apiCallScoped(
        callback: ProcessOutCallback<T>,
        apiMethod: suspend () -> Response<T>
    ) {
        repositoryScope.launch {
            when (val result = apiCall(apiMethod)) {
                is ProcessOutResult.Success -> callback.onSuccess(result.value)
                is ProcessOutResult.Failure -> with(result) {
                    callback.onFailure(code, message, invalidFields, cause)
                }
            }
        }
    }

    protected fun <T : Any, R : Any> apiCallScoped(
        callback: ProcessOutCallback<R>,
        transform: (T) -> R,
        apiMethod: suspend () -> Response<T>
    ) {
        repositoryScope.launch {
            when (val result = apiCall(apiMethod).map(transform)) {
                is ProcessOutResult.Success -> callback.onSuccess(result.value)
                is ProcessOutResult.Failure -> with(result) {
                    callback.onFailure(code, message, invalidFields, cause)
                }
            }
        }
    }
}
