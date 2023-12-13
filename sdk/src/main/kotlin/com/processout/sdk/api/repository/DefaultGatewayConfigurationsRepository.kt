package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.POAllGatewayConfigurationsRequest
import com.processout.sdk.api.model.request.POGatewayConfigurationRequest
import com.processout.sdk.api.model.response.GatewayConfigurationResponse
import com.processout.sdk.api.model.response.POAllGatewayConfigurations
import com.processout.sdk.api.model.response.POGatewayConfiguration
import com.processout.sdk.api.network.GatewayConfigurationsApi
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.map
import com.squareup.moshi.Moshi

internal class DefaultGatewayConfigurationsRepository(
    moshi: Moshi,
    private val api: GatewayConfigurationsApi
) : BaseRepository(moshi), POGatewayConfigurationsRepository {

    override suspend fun fetch(request: POAllGatewayConfigurationsRequest) =
        apiCall { api.fetch(request.toQuery()) }

    override fun fetch(
        request: POAllGatewayConfigurationsRequest,
        callback: ProcessOutCallback<POAllGatewayConfigurations>
    ) = apiCallScoped(callback) { api.fetch(request.toQuery()) }

    override suspend fun find(request: POGatewayConfigurationRequest) =
        apiCall {
            api.find(request.gatewayConfigurationId, request.toQuery())
        }.map { it.toModel() }

    override fun find(
        request: POGatewayConfigurationRequest,
        callback: ProcessOutCallback<POGatewayConfiguration>
    ) = apiCallScoped(callback, GatewayConfigurationResponse::toModel) {
        api.find(request.gatewayConfigurationId, request.toQuery())
    }
}

private fun POAllGatewayConfigurationsRequest.toQuery(): Map<String, String> {
    val query = mutableMapOf<String, String>()
    filter?.let { query["filter"] = it.queryValue }
    query["with_disabled"] = withDisabled.toString()
    query["expand_merchant_accounts"] = true.toString()
    return query
}

private fun POGatewayConfigurationRequest.toQuery(): Map<String, String> {
    val query = mutableMapOf<String, String>()
    if (withGateway) {
        query["expand[]"] = "gateway"
    }
    return query
}

private fun GatewayConfigurationResponse.toModel() = gatewayConfiguration
