package com.processout.example.ui.screen.nativeapm

import com.processout.sdk.core.ProcessOutResult

sealed class NativeApmUiState {
    data object Initial : NativeApmUiState()
    data object Submitting : NativeApmUiState()
    data class Submitted(val uiModel: NativeApmUiModel) : NativeApmUiState()
    data object Launched : NativeApmUiState()
    data class Failure(val failure: ProcessOutResult.Failure) : NativeApmUiState()
}

data class NativeApmUiModel(
    val invoiceId: String,
    val gatewayConfigurationId: String,
    val customerId: String,
    val customerTokenId: String,
    val flow: NativeApmFlow
)

enum class NativeApmFlow {
    AUTHORIZE,
    AUTHORIZE_CUSTOMER_TOKEN,
    AUTHORIZE_LEGACY,
    TOKENIZE
}
