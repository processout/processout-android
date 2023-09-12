package com.processout.sdk.api.model.request

/**
 * Request parameters used to fetch specific gateway configuration.
 *
 * @param[gatewayConfigurationId] Gateway configuration identifier.
 * @param[withGateway] Flag indicating whether the gateway should be expanded in response.
 */
data class POGatewayConfigurationRequest(
    val gatewayConfigurationId: String,
    val withGateway: Boolean = false
)
