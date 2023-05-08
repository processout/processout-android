package com.processout.example.ui.screen.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.handleSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*

class PaymentViewModel(
    private val gatewayConfigurationId: String,
    private val invoices: POInvoicesService
) : ViewModel() {

    class Factory(
        private val gatewayConfigurationId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PaymentViewModel(
                gatewayConfigurationId,
                ProcessOut.instance.invoices
            ) as T
    }

    private val _uiState = Channel<PaymentUiModel>()
    val uiState = _uiState.receiveAsFlow()

    fun createInvoice(amount: String, currency: String) {
        viewModelScope.launch {
            val request = POCreateInvoiceRequest(UUID.randomUUID().toString(), amount, currency)
            invoices.createInvoice(request).handleSuccess { invoice ->
                _uiState.send(PaymentUiModel(gatewayConfigurationId, invoice.id))
            }
        }
    }
}
