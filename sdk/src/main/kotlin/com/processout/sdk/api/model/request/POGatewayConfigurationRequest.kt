package com.processout.sdk.api.model.request

data class POGatewayConfigurationRequest(
    val gatewayConfigurationId: String,
    val withGateway: Boolean = false
)
