package com.processout.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.model.request.POAllGatewayConfigurationsRequest
import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.response.POInvoice
import com.processout.sdk.core.handleSuccess
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ProcessOutViewModel : ViewModel() {

    private val gatewayConfigurations = ProcessOutApi.instance.gatewayConfigurations
    private val invoices = ProcessOutApi.instance.invoices

    private val _invoice = MutableSharedFlow<POInvoice>()
    val invoice = _invoice.asSharedFlow()

    fun fetchGatewayConfigurations() {
        viewModelScope.launch {
            val request = POAllGatewayConfigurationsRequest(
                POAllGatewayConfigurationsRequest.Filter.NATIVE_ALTERNATIVE_PAYMENT_METHODS
            )
            println(gatewayConfigurations.fetch(request))
        }
    }

    fun createInvoice() {
        viewModelScope.launch {
            invoices.createInvoice(
                POCreateInvoiceRequest("sandbox", "100", "USD")
            ).handleSuccess {
                _invoice.emit(it)
            }
        }
    }
}
