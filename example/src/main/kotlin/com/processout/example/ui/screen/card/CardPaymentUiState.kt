package com.processout.example.ui.screen.card

import com.processout.sdk.core.ProcessOutResult

sealed class CardPaymentUiState {
    data object Initial : CardPaymentUiState()
    data object Submitting : CardPaymentUiState()
    data class Submitted(val uiModel: CardPaymentUiModel) : CardPaymentUiState()
    data object Authorizing : CardPaymentUiState()
    data class Failure(val failure: ProcessOutResult.Failure) : CardPaymentUiState()
}

data class CardPaymentUiModel(
    val cardId: String,
    val invoiceId: String,
    val details: CardPaymentDetails
)

data class CardPaymentDetails(
    val card: CardDetails,
    val invoice: InvoiceDetails
)

data class CardDetails(
    val number: String,
    val expMonth: String,
    val expYear: String,
    val cvc: String
)

data class InvoiceDetails(
    val amount: String,
    val currency: String
)
