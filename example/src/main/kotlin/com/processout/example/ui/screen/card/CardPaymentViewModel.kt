package com.processout.example.ui.screen.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.example.shared.getOrNull
import com.processout.example.shared.onFailure
import com.processout.example.ui.screen.card.CardPaymentUiState.*
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POCardTokenizationRequest
import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.api.service.POInvoicesService
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CardPaymentViewModel(
    private val cards: POCardsRepository,
    private val invoices: POInvoicesService
) : ViewModel() {

    class Factory() : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            with(ProcessOut.instance) {
                CardPaymentViewModel(
                    cards,
                    invoices
                ) as T
            }
    }

    private val _uiState = MutableStateFlow<CardPaymentUiState>(Initial)
    val uiState = _uiState.asStateFlow()

    fun submit(details: CardPaymentDetails, returnUrl: String) {
        _uiState.value = Submitting
        viewModelScope.launch {
            val cardDeferred = async { tokenize(details.card) }
            val invoiceDeferred = async { createInvoice(details.invoice, returnUrl) }
            val cardResult = cardDeferred.await()
            val invoiceResult = invoiceDeferred.await()

            cardResult.onFailure { _uiState.value = Failure(it) }
            invoiceResult.onFailure { _uiState.value = Failure(it) }

            val cardId = cardResult.getOrNull()?.id
            val invoiceId = invoiceResult.getOrNull()?.id
            if (cardId != null && invoiceId != null) {
                _uiState.value = Submitted(
                    CardPaymentUiModel(
                        cardId = cardId,
                        invoiceId = invoiceId,
                        details = details
                    )
                )
            }
        }
    }

    private suspend fun tokenize(details: CardDetails) =
        cards.tokenize(
            POCardTokenizationRequest(
                name = "John Doe",
                number = details.number,
                expMonth = details.expMonth.toInt(),
                expYear = details.expYear.toInt(),
                cvc = details.cvc
            )
        )

    private suspend fun createInvoice(details: InvoiceDetails, returnUrl: String) =
        invoices.createInvoice(
            POCreateInvoiceRequest(
                name = UUID.randomUUID().toString(),
                amount = details.amount,
                currency = details.currency,
                returnUrl = returnUrl
            )
        )

    fun onAuthorizing() {
        val uiState = _uiState.value
        if (uiState is Submitted) {
            _uiState.value = Authorizing(uiState.uiModel)
        }
    }

    fun reset() {
        _uiState.value = Initial
    }
}
