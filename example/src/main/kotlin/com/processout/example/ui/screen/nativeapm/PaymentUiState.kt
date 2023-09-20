package com.processout.example.ui.screen.nativeapm

import com.processout.sdk.core.ProcessOutResult

sealed class PaymentUiState {
    data object Initial : PaymentUiState()
    data object Submitting : PaymentUiState()
    data class Submitted(val uiModel: PaymentUiModel) : PaymentUiState()
    data class Failure(val failure: ProcessOutResult.Failure) : PaymentUiState()
}

data class PaymentUiModel(
    val gatewayConfigurationId: String,
    val invoiceId: String
)
