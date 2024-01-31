package com.processout.example.ui.screen.card.tokenization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.example.shared.Constants
import com.processout.example.ui.screen.card.InvoiceDetails
import com.processout.example.ui.screen.card.tokenization.CardTokenizationUiState.*
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import com.processout.sdk.ui.card.tokenization.POCardTokenizationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CardTokenizationViewModel(
    private val invoices: POInvoicesService
) : ViewModel() {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CardTokenizationViewModel(
                ProcessOut.instance.invoices
            ) as T
    }

    private val _uiState = MutableStateFlow<CardTokenizationUiState>(Initial)
    val uiState = _uiState.asStateFlow()

    fun submit(details: InvoiceDetails) {
        _uiState.value = Submitting
        viewModelScope.launch {
            createInvoice(details).let { result ->
                result.onFailure { _uiState.value = Failure(it) }
                    .onSuccess {
                        _uiState.value = Submitted(
                            CardTokenizationUiModel(
                                cardId = String(),
                                invoiceId = it.id,
                                invoice = details
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

    fun onTokenized(data: POCardTokenizationData) {
        val uiState = _uiState.value
        if (uiState is Tokenizing) {
            _uiState.value = Tokenized(
                uiState.uiModel.copy(
                    cardId = data.card.id
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
