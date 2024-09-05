package com.processout.example.ui.screen.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.example.shared.Constants
import com.processout.example.ui.screen.checkout.DynamicCheckoutUiState.*
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POCreateCustomerRequest
import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.response.POCustomer
import com.processout.sdk.api.service.POCustomerTokensService
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.getOrNull
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class DynamicCheckoutViewModel(
    private val invoices: POInvoicesService,
    private val customerTokens: POCustomerTokensService
) : ViewModel() {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            with(ProcessOut.instance) {
                DynamicCheckoutViewModel(
                    invoices = invoices,
                    customerTokens = customerTokens
                ) as T
            }
    }

    private val _uiState = MutableStateFlow<DynamicCheckoutUiState>(Initial)
    val uiState = _uiState.asStateFlow()

    private var customerId: String? = null

    fun submit(details: InvoiceDetails) {
        _uiState.value = Submitting
        viewModelScope.launch {
            if (customerId == null) {
                customerId = createCustomer()?.id
                createInvoice(details)
            } else {
                createInvoice(details)
            }
        }
    }

    private suspend fun createInvoice(details: InvoiceDetails) {
        invoices.createInvoice(
            POCreateInvoiceRequest(
                name = UUID.randomUUID().toString(),
                amount = details.amount,
                currency = details.currency,
                customerId = customerId,
                returnUrl = Constants.RETURN_URL
            )
        ).onSuccess { invoice ->
            _uiState.value = Submitted(
                DynamicCheckoutUiModel(
                    invoiceId = invoice.id,
                    clientSecret = invoice.clientSecret,
                    customerId = customerId
                )
            )
        }.onFailure { _uiState.value = Failure(it) }
    }

    private suspend fun createCustomer(): POCustomer? =
        customerTokens.createCustomer(
            POCreateCustomerRequest(
                firstName = "John",
                lastName = "Doe",
                email = "test@email.com"
            )
        ).getOrNull()

    fun onLaunched() {
        val uiState = _uiState.value
        if (uiState is Submitted) {
            _uiState.value = Launched(uiState.uiModel)
        }
    }

    fun reset() {
        _uiState.value = Initial
    }
}
