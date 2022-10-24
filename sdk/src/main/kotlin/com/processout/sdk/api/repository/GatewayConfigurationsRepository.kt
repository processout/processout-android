package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.POAllGatewayConfigurationsRequest
import com.processout.sdk.api.model.response.POAllGatewayConfigurations
import com.processout.sdk.api.model.response.POGatewayConfiguration
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.ProcessOutResult

interface GatewayConfigurationsRepository {

    suspend fun fetch(request: POAllGatewayConfigurationsRequest): ProcessOutResult<POAllGatewayConfigurations>
    fun fetch(
        request: POAllGatewayConfigurationsRequest,
        callback: ProcessOutCallback<POAllGatewayConfigurations>
    )

    suspend fun find(id: String): ProcessOutResult<POGatewayConfiguration>
    fun find(id: String, callback: ProcessOutCallback<POGatewayConfiguration>)
}
