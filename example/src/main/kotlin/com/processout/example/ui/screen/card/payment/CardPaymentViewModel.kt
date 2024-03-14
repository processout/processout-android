package com.processout.example.ui.screen.card.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.example.shared.Constants
import com.processout.example.ui.screen.card.payment.CardPaymentUiState.*
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CardPaymentViewModel(
    private val invoices: POInvoicesService
) : ViewModel() {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CardPaymentViewModel(
                ProcessOut.instance.invoices
            ) as T
    }

    private val _uiState = MutableStateFlow<CardPaymentUiState>(Initial)
    val uiState = _uiState.asStateFlow()

    fun submit(details: InvoiceDetails) {
        _uiState.value = Submitting
        viewModelScope.launch {
            createInvoice(details).let { result ->
                result
                    .onFailure { _uiState.value = Failure(it) }
                    .onSuccess {
                        _uiState.value = Submitted(
                            CardPaymentUiModel(
                                invoiceId = it.id,
                                cardId = String()
                            )
                        )
                    }
            }
        }
    }

    private suspend fun createInvoice(details: InvoiceDetails) =
        invoices.createInvoice(
            POCreateInvoiceRequest(
                name = UUID.randomUUID().toString(),
                amount = details.amount,
                currency = details.currency,
                returnUrl = Constants.RETURN_URL
            )
        )

    fun onTokenizing() {
        val uiState = _uiState.value
        if (uiState is Submitted) {
            _uiState.value = Tokenizing(uiState.uiModel.copy())
        }
    }

    fun onTokenized(card: POCard) {
        val uiState = _uiState.value
        if (uiState is Tokenizing) {
            _uiState.value = Tokenized(
                uiState.uiModel.copy(
                    cardId = card.id
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
