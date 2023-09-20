package com.processout.example.ui.screen.nativeapm

import com.processout.sdk.core.ProcessOutResult

sealed class NativeApmUiState {
    data object Initial : NativeApmUiState()
    data object Submitting : NativeApmUiState()
    data class Submitted(val uiModel: NativeApmUiModel) : NativeApmUiState()
    data class Failure(val failure: ProcessOutResult.Failure) : NativeApmUiState()
}

data class NativeApmUiModel(
    val gatewayConfigurationId: String,
    val invoiceId: String
)
