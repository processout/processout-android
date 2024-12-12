package com.processout.sdk.api.network.interceptor

import com.processout.sdk.api.preferences.Preferences
import com.processout.sdk.core.locale.currentSdkLocale
import com.processout.sdk.di.ContextGraph
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.userAgent

internal class UserAgentInterceptor(
    private val contextGraph: ContextGraph,
    private val preferences: Preferences,
    private val sdkVersion: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val userAgentComponents = arrayOf(
            System.getProperty("http.agent", "Android/${userAgent}"),
            "ProcessOut Android-Bindings",
            sdkVersion
        )
        val request = chain.request().newBuilder()
            .header("User-Agent", userAgentComponents.joinToString(separator = "/"))
            .header("Accept-Language", contextGraph.configuration.application.currentSdkLocale().toLanguageTag())
            .header("Session-Id", contextGraph.configuration.sessionId)
            .header("Installation-Id", preferences.installationId)
            .header("Device-System-Name", contextGraph.deviceData.channel)
            .header("Device-System-Version", contextGraph.deviceData.systemApiLevel)
            .header("Product-Version", sdkVersion)
            .build()
        return chain.proceed(request)
    }
}
