package com.processout.example.ui.screen.nativeapm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.example.shared.getOrNull
import com.processout.example.shared.onFailure
import com.processout.example.shared.onSuccess
import com.processout.example.ui.screen.nativeapm.PaymentUiState.Failure
import com.processout.example.ui.screen.nativeapm.PaymentUiState.Initial
import com.processout.example.ui.screen.nativeapm.PaymentUiState.Submitted
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POCreateCustomerRequest
import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.response.POCustomer
import com.processout.sdk.api.service.POCustomerTokensService
import com.processout.sdk.api.service.POInvoicesService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class PaymentViewModel(
    private val gatewayConfigurationId: String,
    private val invoices: POInvoicesService,
    private val customerTokens: POCustomerTokensService
) : ViewModel() {

    class Factory(
        private val gatewayConfigurationId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            with(ProcessOut.instance) {
                PaymentViewModel(
                    gatewayConfigurationId,
                    invoices,
                    customerTokens
                ) as T
            }
    }

    private val _uiState = MutableStateFlow<PaymentUiState>(Initial)
    val uiState = _uiState.asStateFlow()

    fun createInvoice(amount: String, currency: String) {
        _uiState.value = PaymentUiState.Submitting
        viewModelScope.launch {
            val request = POCreateInvoiceRequest(
                name = UUID.randomUUID().toString(),
                amount = amount,
                currency = currency,
                customerId = createCustomer()?.id
            )
            invoices.createInvoice(request)
                .onSuccess {
                    _uiState.value = Submitted(PaymentUiModel(gatewayConfigurationId, it.id))
                }
                .onFailure { _uiState.value = Failure(it) }
        }
    }

    private suspend fun createCustomer(): POCustomer? =
        customerTokens.createCustomer(
            POCreateCustomerRequest(
                firstName = "John",
                lastName = "Doe",
                email = "test@email.com"
            )
        ).getOrNull()

    fun reset() {
        _uiState.value = Initial
    }
}
