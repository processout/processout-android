package com.processout.sdk.ui.nativeapm

import com.processout.sdk.ui.shared.model.InputParameter

internal sealed class PONativeAlternativePaymentMethodUiState {
    object Initial : PONativeAlternativePaymentMethodUiState()
    object Loading : PONativeAlternativePaymentMethodUiState()

    data class UserInput(val uiModel: PONativeAlternativePaymentMethodUiModel) :
        PONativeAlternativePaymentMethodUiState() {
        override fun equals(other: Any?): Boolean {
            return uiModel == other
        }

        override fun hashCode(): Int {
            return uiModel.hashCode()
        }
    }

    object Success : PONativeAlternativePaymentMethodUiState()
    object Failure : PONativeAlternativePaymentMethodUiState()
}

internal data class PONativeAlternativePaymentMethodUiModel(
    val logoUrl: String?,
    val promptMessage: String? = null,
    val failureMessage: String? = null,
    val inputParameters: List<InputParameter>,
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
