package com.processout.sdk.api.repository

import com.processout.sdk.api.model.POGatewayConfigurations
import com.processout.sdk.core.ProcessOutResult

internal interface GatewayConfigurationsRepository {

    suspend fun fetchGatewayConfigurations(): ProcessOutResult<POGatewayConfigurations>
}
