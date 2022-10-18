package com.processout.sdk.api.network

import com.processout.sdk.api.model.POGatewayConfigurations
import retrofit2.Response
import retrofit2.http.GET

internal interface GatewayConfigurationsApi {

    @GET("/gateway-configurations")
    suspend fun getGatewayConfigurations(): Response<POGatewayConfigurations>
}
