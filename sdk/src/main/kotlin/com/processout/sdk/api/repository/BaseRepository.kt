@file:Suppress("MemberVisibilityCanBePrivate")

package com.processout.sdk.api.repository

import com.processout.sdk.core.*
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.retry.PORetryStrategy
import com.processout.sdk.core.retry.PORetryStrategy.Exponential
import kotlinx.coroutines.*
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

internal abstract class BaseRepository(
    private val failureMapper: ApiFailureMapper,
    private val retryStrategy: PORetryStrategy = Exponential(
        maxRetries = 4,
        initialDelay = 100,
        maxDelay = 1000,
        factor = 3.0
    ),
    protected val repositoryScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
    protected val workDispatcher: CoroutineDispatcher = Dispatchers.IO
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
            val response = retry(apiMethod, retryStrategy)
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

    private suspend fun <T : Any> retry(
        apiMethod: suspend () -> Response<T>,
        strategy: PORetryStrategy
    ): Response<T> {
        val iterator = strategy.iterator
        repeat(strategy.maxRetries - 1) {
            try {
                val response = apiMethod()
                val isRetryable = when (response.code()) {
                    408, 409, 425, 429,
                    in 500..599 -> true
                    else -> false
                }
                if (!isRetryable) return response
            } catch (_: IOException) {
                // network issue, retry
            }
            delay(iterator.next())
        }
        return apiMethod()
    }

    private fun <T : Any> Response<T>.nullBodyFailure(): ProcessOutResult.Failure {
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
