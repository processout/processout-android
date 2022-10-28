package com.processout.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.model.request.POAllGatewayConfigurationsRequest
import kotlinx.coroutines.launch

class ProcessOutViewModel : ViewModel() {

    private val gatewayConfigurations = ProcessOutApi.instance.gatewayConfigurations

    fun fetchGatewayConfigurations() {
        viewModelScope.launch {
            val request = POAllGatewayConfigurationsRequest(
                POAllGatewayConfigurationsRequest.Filter.NATIVE_ALTERNATIVE_PAYMENT_METHODS
            )
            println(gatewayConfigurations.fetch(request))
        }
    }
}
