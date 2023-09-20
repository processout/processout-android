package com.processout.example.ui.screen.card

import com.processout.sdk.core.ProcessOutResult

sealed class CardPaymentUiState {
    data object Initial : CardPaymentUiState()
    data object Submitting : CardPaymentUiState()
    data class Submitted(val uiModel: CardPaymentUiModel) : CardPaymentUiState()
    data class Failure(val failure: ProcessOutResult.Failure) : CardPaymentUiState()
}

data object CardPaymentUiModel
