package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.POAllGatewayConfigurationsRequest
import com.processout.sdk.api.model.response.POAllGatewayConfigurations
import com.processout.sdk.api.model.response.POGatewayConfiguration
import com.processout.sdk.api.model.response.POGatewayConfigurationResponse
import com.processout.sdk.api.network.GatewayConfigurationsApi
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.map

internal class GatewayConfigurationsRepositoryImpl(
    private val api: GatewayConfigurationsApi
) : BaseRepository(), GatewayConfigurationsRepository {

    override suspend fun fetch(request: POAllGatewayConfigurationsRequest) =
        apiCall { api.fetch(request.toQuery()) }

    override fun fetch(
        request: POAllGatewayConfigurationsRequest,
        callback: ProcessOutCallback<POAllGatewayConfigurations>
    ) = apiCallScoped(callback) { api.fetch(request.toQuery()) }

    override suspend fun find(id: String) = apiCall { api.find(id) }.map { it.toModel() }

    override fun find(id: String, callback: ProcessOutCallback<POGatewayConfiguration>) =
        apiCallScoped(callback, POGatewayConfigurationResponse::toModel) { api.find(id) }
}

private fun POAllGatewayConfigurationsRequest.toQuery(): Map<String, String> {
    val query = mutableMapOf<String, String>()
    filter?.let { query["filter"] = it.queryValue }
    query["with_disabled"] = withDisabled.toString()
    query["expand_merchant_accounts"] = true.toString()
    return query
}

private fun POGatewayConfigurationResponse.toModel() = gatewayConfiguration
