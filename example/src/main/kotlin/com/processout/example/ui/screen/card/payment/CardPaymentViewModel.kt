package com.processout.example.ui.screen.card.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.example.shared.Constants
import com.processout.example.ui.screen.card.payment.CardPaymentViewModelEvent.LaunchTokenization
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POCreateCustomerRequest
import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.response.POCustomer
import com.processout.sdk.api.model.response.POInvoice
import com.processout.sdk.api.service.POCustomerTokensService
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.getOrNull
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class CardPaymentViewModel(
    private val invoices: POInvoicesService,
    private val customerTokens: POCustomerTokensService
) : ViewModel() {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            with(ProcessOut.instance) {
                CardPaymentViewModel(
                    invoices = invoices,
                    customerTokens = customerTokens
                ) as T
            }
    }

    private val _state = MutableStateFlow(CardPaymentViewModelState())
    val state = _state.asStateFlow()

    private val _events = Channel<CardPaymentViewModelEvent>()
    val events = _events.receiveAsFlow()

    fun submit(amount: String, currency: String) {
        _state.update {
            it.copy(
                amount = amount,
                currency = currency
            )
        }
        viewModelScope.launch {
            _events.send(LaunchTokenization)
        }
    }

    suspend fun createInvoice(): POInvoice? {
        val state = _state.value
        if (state.customerId == null) {
            _state.update { it.copy(customerId = createCustomer()?.id) }
        }
        val invoice = invoices.createInvoice(
            POCreateInvoiceRequest(
                name = UUID.randomUUID().toString(),
                amount = state.amount,
                currency = state.currency,
                customerId = state.customerId,
                returnUrl = Constants.RETURN_URL
            )
        ).getOrNull()
        _state.update { it.copy(invoiceId = invoice?.id) }
        return invoice
    }

    private suspend fun createCustomer(): POCustomer? =
        customerTokens.createCustomer(
            POCreateCustomerRequest(
                firstName = "John",
                lastName = "Doe",
                email = "test@email.com"
            )
        ).getOrNull()
}
