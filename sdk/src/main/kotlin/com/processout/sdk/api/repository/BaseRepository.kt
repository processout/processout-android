package com.processout.sdk.api.repository

import com.processout.sdk.api.network.toFailure
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.exception.ProcessOutException
import com.processout.sdk.core.map
import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import retrofit2.Response
import java.io.IOException

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
                    ?: ProcessOutResult.Failure("Response body is empty.")
                false -> response.toFailure(moshi)
            }
        } catch (e: IOException) {
            ProcessOutResult.Failure(e.message ?: String(), e)
        } catch (e: Exception) {
            ProcessOutResult.Failure("Unexpected exception during API call.", e)
        }
    }

    protected fun <T : Any> apiCallScoped(
        callback: ProcessOutCallback<T>,
        apiMethod: suspend () -> Response<T>
    ) {
        repositoryScope.launch {
            when (val result = apiCall(apiMethod)) {
                is ProcessOutResult.Success -> callback.onSuccess(result.value)
                is ProcessOutResult.Failure -> callback.onFailure(
                    result.cause ?: ProcessOutException(result.message)
                )
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
                is ProcessOutResult.Failure -> callback.onFailure(
                    result.cause ?: ProcessOutException(result.message)
                )
            }
        }
    }
}
