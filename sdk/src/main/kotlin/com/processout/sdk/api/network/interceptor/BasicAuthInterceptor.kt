@file:Suppress("RestrictedApi")

package com.processout.sdk.api.network.interceptor

import com.processout.sdk.di.ContextGraph
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

internal class BasicAuthInterceptor(
    private val contextGraph: ContextGraph
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = with(contextGraph.configuration) {
            chain.request().newBuilder()
                .header(
                    name = "Authorization",
                    value = Credentials.basic(
                        username = projectId,
                        password = privateKey
                    )
                ).build()
        }
        return chain.proceed(request)
    }
}
