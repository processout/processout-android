package com.processout.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.model.request.POAllGatewayConfigurationsRequest
import com.processout.sdk.api.repository.GatewayConfigurationsRepository
import kotlinx.coroutines.launch

class ProcessOutViewModel : ViewModel() {

    private val gatewayConfigurationsRepository: GatewayConfigurationsRepository

    init {
        ProcessOutApi.configure(
            ProcessOutApi.Configuration(
                "test-proj"
            )
        )
        gatewayConfigurationsRepository = ProcessOutApi.instance.gatewayConfigurationsRepository
    }

    fun fetchGatewayConfigurations() {
        viewModelScope.launch {
            val request = POAllGatewayConfigurationsRequest(
                POAllGatewayConfigurationsRequest.Filter.NATIVE_ALTERNATIVE_PAYMENT_METHODS
            )
            println(gatewayConfigurationsRepository.fetch(request))
        }
    }
}
