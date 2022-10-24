package com.processout.sdk.api.network.interceptor

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class BasicAuthInterceptor(username: String, password: String) : Interceptor {

    private val credentials: String = Credentials.basic(username, password)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authRequest = request.newBuilder()
            .header("Authorization", credentials)
            .build()
        return chain.proceed(authRequest)
    }
}
