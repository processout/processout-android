@file:Suppress("RestrictedApi")

package com.processout.sdk.api.network.interceptor

import com.processout.sdk.di.ContextGraph
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class BasicAuthInterceptor(
    private val contextGraph: ContextGraph
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val updatedRequest = with(contextGraph.configuration) {
            chain.request().newBuilder()
                .header(
                    name = "Authorization",
                    value = Credentials.basic(
                        username = projectId,
                        password = privateKey
                    )
                ).build()
        }
        return chain.proceed(updatedRequest)
    }
}
