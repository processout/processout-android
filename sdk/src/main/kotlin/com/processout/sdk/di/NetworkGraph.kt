package com.processout.sdk.di

import com.processout.sdk.api.network.GatewayConfigurationsApi
import com.processout.sdk.api.network.NetworkConfiguration
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

internal interface NetworkGraph {
    val gatewayConfigurationsApi: GatewayConfigurationsApi
}

internal class NetworkGraphImpl(config: NetworkConfiguration) : NetworkGraph {

    private val okHttpClient: OkHttpClient =
        OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .build()

    private val moshi = Moshi.Builder().build()

    private val moshiConverterFactory = MoshiConverterFactory.create(moshi)

    private val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)
            .build()

    override val gatewayConfigurationsApi: GatewayConfigurationsApi =
        retrofit.create(GatewayConfigurationsApi::class.java)
}
