package com.processout.example.ui.screen.apm

data class APMUiModel(
    val gatewayConfigurations: List<GatewayConfiguration>
)

data class GatewayConfiguration(
    val id: String,
    val name: String
)
