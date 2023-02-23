package com.processout.sdk.api.repository

import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.map
import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

internal abstract class BaseRepository(
    protected val moshi: Moshi,
    protected val repositoryScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
    protected val workDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    protected suspend fun <T : Any> apiCall(
        apiMethod: suspend () -> Response<T>
    ): ProcessOutResult<T> = withContext(workDispatcher) {
        try {
            val response = apiMethod()
            when (response.isSuccessful) {
                true -> response.body()?.let { ProcessOutResult.Success(it) }
                    ?: ProcessOutResult.Failure(
                        "Response body is empty.",
                        POFailure.Code.Internal()
                    )
                false -> response.toFailure(moshi)
            }
        } catch (e: Exception) {
            when (e) {
                is SocketTimeoutException -> ProcessOutResult.Failure(
                    e.message ?: "Request timed out.",
                    POFailure.Code.Timeout(), cause = e
                )
                is IOException -> ProcessOutResult.Failure(
                    e.message ?: "Network is unreachable.",
                    POFailure.Code.NetworkUnreachable, cause = e
                )
                else -> ProcessOutResult.Failure(
                    "Unexpected exception during API call.",
                    POFailure.Code.Internal(), cause = e
                )
            }
        }
    }

    protected fun <T : Any> apiCallScoped(
        callback: ProcessOutCallback<T>,
        apiMethod: suspend () -> Response<T>
    ) {
        repositoryScope.launch {
            when (val result = apiCall(apiMethod)) {
                is ProcessOutResult.Success -> callback.onSuccess(result.value)
                is ProcessOutResult.Failure -> with(result) {
                    callback.onFailure(message, code, invalidFields, cause)
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
                    callback.onFailure(message, code, invalidFields, cause)
                }
            }
        }
    }
}
