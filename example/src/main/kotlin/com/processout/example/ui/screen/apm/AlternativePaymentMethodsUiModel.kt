package com.processout.example.ui.screen.apm

data class AlternativePaymentMethodsUiModel(
    val gatewayConfigurations: List<GatewayConfiguration>
)

data class GatewayConfiguration(
    val id: String,
    val name: String
)
