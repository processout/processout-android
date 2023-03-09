package com.processout.sdk.api.network.interceptor

import android.app.Application
import com.processout.sdk.utils.currentSdkLocale
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.userAgent
import java.io.IOException
import java.util.UUID

internal class UserAgentInterceptor(
    private val application: Application,
    private val sdkVersion: String
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val userAgentComponents = arrayOf(
            System.getProperty("http.agent", "Android/${userAgent}"),
            "ProcessOut Android-Bindings",
            sdkVersion
        )

        val request = chain.request()
        val userAgentRequest = request.newBuilder()
            .header("Idempotency-Key", UUID.randomUUID().toString())
            .header("User-Agent", userAgentComponents.joinToString(separator = "/"))
            .header("Accept-Language", application.currentSdkLocale().toLanguageTag())
            .build()
        return chain.proceed(userAgentRequest)
    }
}
