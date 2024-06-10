package com.processout.sdk.api.network.interceptor

import com.processout.sdk.core.locale.currentSdkLocale
import com.processout.sdk.di.ContextGraph
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.userAgent
import java.io.IOException
import java.util.UUID

internal class UserAgentInterceptor(
    private val contextGraph: ContextGraph,
    private val sdkVersion: String
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val userAgentComponents = arrayOf(
            System.getProperty("http.agent", "Android/${userAgent}"),
            "ProcessOut Android-Bindings",
            sdkVersion
        )
        val userAgentRequest = chain.request().newBuilder()
            .header("Idempotency-Key", UUID.randomUUID().toString())
            .header("User-Agent", userAgentComponents.joinToString(separator = "/"))
            .header("Accept-Language", contextGraph.configuration.application.currentSdkLocale().toLanguageTag())
            .header("Session-Id", contextGraph.configuration.sessionId)
            .build()
        return chain.proceed(userAgentRequest)
    }
}
