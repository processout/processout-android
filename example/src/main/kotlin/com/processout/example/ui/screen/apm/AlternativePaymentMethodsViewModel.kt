package com.processout.example.ui.screen.apm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.model.request.POAllGatewayConfigurationsRequest
import com.processout.sdk.api.model.response.POAllGatewayConfigurations
import com.processout.sdk.api.repository.GatewayConfigurationsRepository
import com.processout.sdk.core.handleSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlternativePaymentMethodsViewModel(
    private val filter: POAllGatewayConfigurationsRequest.Filter,
    private val gatewayConfigurations: GatewayConfigurationsRepository
) : ViewModel() {

    class Factory(
        private val filter: POAllGatewayConfigurationsRequest.Filter
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AlternativePaymentMethodsViewModel(
                filter,
                ProcessOutApi.instance.gatewayConfigurations
            ) as T
    }

    private val _uiState = MutableStateFlow(AlternativePaymentMethodsUiModel(emptyList()))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val request = POAllGatewayConfigurationsRequest(filter)
            gatewayConfigurations.fetch(request).handleSuccess { result ->
                _uiState.update {
                    it.copy(gatewayConfigurations = result.toUiModel())
                }
            }
        }
    }
}

private fun POAllGatewayConfigurations.toUiModel() =
    gatewayConfigurations.map {
        GatewayConfiguration(it.id, it.gateway?.displayName ?: it.id)
    }
