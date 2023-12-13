package com.processout.sdk.di

import com.processout.sdk.api.network.*
import com.processout.sdk.api.network.interceptor.BasicAuthInterceptor
import com.processout.sdk.api.network.interceptor.UserAgentInterceptor
import com.processout.sdk.core.logger.POLogger
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Date
import java.util.concurrent.TimeUnit

internal interface NetworkGraph {
    val moshi: Moshi
    val gatewayConfigurationsApi: GatewayConfigurationsApi
    val invoicesApi: InvoicesApi
    val cardsApi: CardsApi
    val customerTokensApi: CustomerTokensApi
    val logsApi: LogsApi
}

internal class DefaultNetworkGraph(
    configuration: NetworkConfiguration
) : NetworkGraph {

    private val okHttpClient: OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(BasicAuthInterceptor(configuration.projectId, configuration.privateKey))
            .addInterceptor(UserAgentInterceptor(configuration.application, configuration.sdkVersion))
            .apply {
                if (configuration.debug) {
                    addInterceptor(HttpLoggingInterceptor { message ->
                        POLogger.debug(message)
                    }.apply { level = HttpLoggingInterceptor.Level.BODY })
                }
            }.build()

    override val moshi: Moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter())
        .build()

    private val moshiConverterFactory = MoshiConverterFactory.create(moshi)

    private val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(configuration.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)
            .build()

    override val gatewayConfigurationsApi: GatewayConfigurationsApi =
        retrofit.create(GatewayConfigurationsApi::class.java)

    override val invoicesApi: InvoicesApi = retrofit.create(InvoicesApi::class.java)

    override val cardsApi: CardsApi = retrofit.create(CardsApi::class.java)

    override val customerTokensApi: CustomerTokensApi = retrofit.create(CustomerTokensApi::class.java)

    override val logsApi: LogsApi = retrofit.create(LogsApi::class.java)
}
