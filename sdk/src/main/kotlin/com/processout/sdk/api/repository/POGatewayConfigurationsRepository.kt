package com.processout.sdk.api.repository

import com.processout.sdk.api.model.request.POAllGatewayConfigurationsRequest
import com.processout.sdk.api.model.request.POGatewayConfigurationRequest
import com.processout.sdk.api.model.response.POAllGatewayConfigurations
import com.processout.sdk.api.model.response.POGatewayConfiguration
import com.processout.sdk.core.ProcessOutCallback
import com.processout.sdk.core.ProcessOutResult

/**
 * Provides functionality related to gateway configurations.
 */
interface POGatewayConfigurationsRepository {

    /**
     * Returns available gateway configurations matching the request.
     */
    suspend fun fetch(request: POAllGatewayConfigurationsRequest): ProcessOutResult<POAllGatewayConfigurations>

    /**
     * Returns available gateway configurations matching the request.
     */
    fun fetch(
        request: POAllGatewayConfigurationsRequest,
        callback: ProcessOutCallback<POAllGatewayConfigurations>
    )

    /**
     * Searches specific gateway configuration.
     */
    suspend fun find(request: POGatewayConfigurationRequest): ProcessOutResult<POGatewayConfiguration>

    /**
     * Searches specific gateway configuration.
     */
    fun find(
        request: POGatewayConfigurationRequest,
        callback: ProcessOutCallback<POGatewayConfiguration>
    )
}
