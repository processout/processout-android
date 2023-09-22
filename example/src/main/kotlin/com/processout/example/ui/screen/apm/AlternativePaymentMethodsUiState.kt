package com.processout.example.ui.screen.apm

sealed class AlternativePaymentMethodsUiState {
    data object Initial : AlternativePaymentMethodsUiState()
    data object Refreshing : AlternativePaymentMethodsUiState()
    data class Loaded(val uiModel: AlternativePaymentMethodsUiModel) : AlternativePaymentMethodsUiState()
}

data class AlternativePaymentMethodsUiModel(
    val gatewayConfigurations: List<GatewayConfiguration>
)

data class GatewayConfiguration(
    val id: String,
    val name: String
)
