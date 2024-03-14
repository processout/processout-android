package com.processout.example.ui.screen.card.payment

import com.processout.sdk.core.ProcessOutResult

sealed class CardPaymentUiState {
    data object Initial : CardPaymentUiState()
    data object Submitting : CardPaymentUiState()
    data class Submitted(val uiModel: CardPaymentUiModel) : CardPaymentUiState()
    data class Tokenizing(val uiModel: CardPaymentUiModel) : CardPaymentUiState()
    data class Tokenized(val uiModel: CardPaymentUiModel) : CardPaymentUiState()
    data class Authorizing(val uiModel: CardPaymentUiModel) : CardPaymentUiState()
    data class Failure(val failure: ProcessOutResult.Failure) : CardPaymentUiState()
}

data class CardPaymentUiModel(
    val cardId: String,
    val invoiceId: String,
    val invoice: InvoiceDetails
)

data class InvoiceDetails(
    val amount: String,
    val currency: String
)
