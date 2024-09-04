package com.processout.example.ui.screen.card.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.example.shared.Constants
import com.processout.example.ui.screen.card.payment.CardPaymentUiState.*
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POCardTokenizationProcessingRequest
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

    private val _uiState = MutableStateFlow<CardPaymentUiState>(Initial)
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

    private suspend fun createInvoice(details: InvoiceDetails) =
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
                CardPaymentUiModel(
                    invoiceId = invoice.id,
                    cardId = String(),
                    saveCard = false
                )
            )
        }.onFailure { _uiState.value = Failure(it) }

    private suspend fun createCustomer(): POCustomer? =
        customerTokens.createCustomer(
            POCreateCustomerRequest(
                firstName = "John",
                lastName = "Doe",
                email = "test@email.com"
            )
        ).getOrNull()

    fun onTokenizing() {
        val uiState = _uiState.value
        if (uiState is Submitted) {
            _uiState.value = Tokenizing(uiState.uiModel.copy())
        }
    }

    fun onTokenized(request: POCardTokenizationProcessingRequest) {
        val uiState = _uiState.value
        if (uiState is Tokenizing) {
            _uiState.value = Tokenized(
                uiState.uiModel.copy(
                    cardId = request.card.id,
                    saveCard = request.saveCard
                )
            )
        }
        if (uiState is Authorizing) {
            _uiState.value = Tokenized(
                uiState.uiModel.copy(
                    cardId = request.card.id,
                    saveCard = request.saveCard
                )
            )
        }
    }

    fun onAuthorizing() {
        val uiState = _uiState.value
        if (uiState is Tokenized) {
            _uiState.value = Authorizing(uiState.uiModel.copy())
        }
    }

    fun reset() {
        _uiState.value = Initial
    }
}
