package com.processout.sdk.di

import com.processout.sdk.BuildConfig
import com.processout.sdk.api.network.CardsApi
import com.processout.sdk.api.network.GatewayConfigurationsApi
import com.processout.sdk.api.network.InvoicesApi
import com.processout.sdk.api.network.CustomerTokensApi
import com.processout.sdk.api.network.NetworkConfiguration
import com.processout.sdk.api.network.interceptor.BasicAuthInterceptor
import com.processout.sdk.api.network.interceptor.UserAgentInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

internal interface NetworkGraph {
    val moshi: Moshi
    val gatewayConfigurationsApi: GatewayConfigurationsApi
    val invoicesApi: InvoicesApi
    val cardsApi: CardsApi
    val customerTokensApi: CustomerTokensApi
}

internal class NetworkGraphImpl(config: NetworkConfiguration) : NetworkGraph {

    private val okHttpClient: OkHttpClient =
        OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(BasicAuthInterceptor(config.projectId, config.privateKey))
            .addInterceptor(UserAgentInterceptor(config.sdkVersion))
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }.build()

    override val moshi: Moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter())
        .build()
    private val moshiConverterFactory = MoshiConverterFactory.create(moshi)

    private val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)
            .build()

    override val gatewayConfigurationsApi: GatewayConfigurationsApi =
        retrofit.create(GatewayConfigurationsApi::class.java)

    override val invoicesApi: InvoicesApi = retrofit.create(InvoicesApi::class.java)

    override val cardsApi: CardsApi = retrofit.create(CardsApi::class.java)

    override val customerTokensApi: CustomerTokensApi = retrofit.create(CustomerTokensApi::class.java)
}
