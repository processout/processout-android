package com.processout.example.ui.screen.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.example.ui.screen.payment.PaymentUiState.Failure
import com.processout.example.ui.screen.payment.PaymentUiState.Initial
import com.processout.example.ui.screen.payment.PaymentUiState.Submitted
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.ProcessOutResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _uiState = MutableStateFlow<PaymentUiState>(Initial)
    val uiState = _uiState.asStateFlow()

    fun createInvoice(amount: String, currency: String) {
        _uiState.value = PaymentUiState.Submitting
        viewModelScope.launch {
            val request = POCreateInvoiceRequest(UUID.randomUUID().toString(), amount, currency)
            invoices.createInvoice(request).let { result ->
                when (result) {
                    is ProcessOutResult.Success -> _uiState.value = Submitted(
                        PaymentUiModel(gatewayConfigurationId, result.value.id)
                    )
                    is ProcessOutResult.Failure -> _uiState.value = Failure(result.copy())
                }
            }
        }
    }

    fun reset() {
        _uiState.value = Initial
    }
}
