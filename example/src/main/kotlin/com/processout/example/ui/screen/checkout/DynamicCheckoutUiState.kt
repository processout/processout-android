package com.processout.example.ui.screen.checkout

import com.processout.sdk.core.ProcessOutResult

sealed class DynamicCheckoutUiState {
    data object Initial : DynamicCheckoutUiState()
    data object Submitting : DynamicCheckoutUiState()
    data class Submitted(val uiModel: DynamicCheckoutUiModel) : DynamicCheckoutUiState()
    data class Launched(val uiModel: DynamicCheckoutUiModel) : DynamicCheckoutUiState()
    data class Failure(val failure: ProcessOutResult.Failure) : DynamicCheckoutUiState()
}

data class DynamicCheckoutUiModel(
    val invoiceId: String,
    val clientSecret: String?,
    val customerId: String?
)

data class InvoiceDetails(
    val amount: String,
    val currency: String
)
