package com.processout.sdk.api.network.interceptor

import com.processout.sdk.core.retry.PORetryStrategy
import com.processout.sdk.core.retry.PORetryStrategy.Exponential
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class RetryInterceptor(
    private val retryStrategy: PORetryStrategy = Exponential(
        maxRetries = 4,
        initialDelay = 100,
        maxDelay = 1000,
        factor = 3.0
    )
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val iterator = retryStrategy.iterator
        repeat(retryStrategy.maxRetries - 1) {
            var response: Response? = null
            try {
                response = chain.proceed(request)
                val isRetryable = when (response.code) {
                    408, 409, 425, 429,
                    in 500..599 -> true
                    else -> false
                }
                if (!isRetryable) return response
            } catch (_: IOException) {
                // network issue, retry
            }
            response?.body?.close()
            Thread.sleep(iterator.next())
        }
        return chain.proceed(request)
    }
}
