package com.processout.sdk.ui.nativeapm

import com.processout.sdk.ui.shared.model.InputParameter

internal sealed class PONativeAlternativePaymentMethodUiState {
    object Loading : PONativeAlternativePaymentMethodUiState()

    data class UserInput(
        val uiModel: PONativeAlternativePaymentMethodUiModel
    ) : PONativeAlternativePaymentMethodUiState()

    data class Capture(
        val uiModel: PONativeAlternativePaymentMethodUiModel
    ) : PONativeAlternativePaymentMethodUiState()

    data class Success(
        val uiModel: PONativeAlternativePaymentMethodUiModel
    ) : PONativeAlternativePaymentMethodUiState()

    data class Failure(
        val message: String,
        val cause: Exception? = null
    ) : PONativeAlternativePaymentMethodUiState()
}

internal data class PONativeAlternativePaymentMethodUiModel(
    val displayName: String,
    val logoUrl: String,
    val customerActionMessage: String?,
    val customerActionImageUrl: String?,
    val inputParameters: List<InputParameter>,
    val failureMessage: String? = null,
    val submitButtonText: String,
    val isSubmitAllowed: Boolean,
    val isSubmitting: Boolean
)

internal inline fun PONativeAlternativePaymentMethodUiState.doWhenUserInput(
    crossinline block: (uiModel: PONativeAlternativePaymentMethodUiModel) -> Unit
) {
    if (this is PONativeAlternativePaymentMethodUiState.UserInput) {
        block(uiModel)
    }
}

internal inline fun PONativeAlternativePaymentMethodUiState.doWhenCapture(
    crossinline block: (uiModel: PONativeAlternativePaymentMethodUiModel) -> Unit
) {
    if (this is PONativeAlternativePaymentMethodUiState.Capture) {
        block(uiModel)
    }
}
