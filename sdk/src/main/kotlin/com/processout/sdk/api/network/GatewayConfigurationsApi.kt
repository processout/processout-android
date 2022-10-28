package com.processout.sdk.api.network

import com.processout.sdk.api.model.response.POAllGatewayConfigurations
import com.processout.sdk.api.model.response.POGatewayConfigurationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

internal interface GatewayConfigurationsApi {

    @GET("/gateway-configurations")
    suspend fun fetch(@QueryMap options: Map<String, String>): Response<POAllGatewayConfigurations>

    @GET("/gateway-configurations/{id}")
    suspend fun find(
        @Path("id") gatewayConfigurationId: String,
        @QueryMap options: Map<String, String>
    ): Response<POGatewayConfigurationResponse>
}
