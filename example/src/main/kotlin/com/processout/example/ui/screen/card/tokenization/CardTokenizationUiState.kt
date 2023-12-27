package com.processout.example.ui.screen.card.tokenization

import com.processout.example.ui.screen.card.InvoiceDetails
import com.processout.sdk.core.ProcessOutResult

sealed class CardTokenizationUiState {
    data object Initial : CardTokenizationUiState()
    data object Submitting : CardTokenizationUiState()
    data class Submitted(val uiModel: CardTokenizationUiModel) : CardTokenizationUiState()
    data class Tokenizing(val uiModel: CardTokenizationUiModel) : CardTokenizationUiState()
    data class Tokenized(val uiModel: CardTokenizationUiModel) : CardTokenizationUiState()
    data class Authorizing(val uiModel: CardTokenizationUiModel) : CardTokenizationUiState()
    data class Failure(val failure: ProcessOutResult.Failure) : CardTokenizationUiState()
}

data class CardTokenizationUiModel(
    val cardId: String,
    val invoiceId: String,
    val invoice: InvoiceDetails
)
