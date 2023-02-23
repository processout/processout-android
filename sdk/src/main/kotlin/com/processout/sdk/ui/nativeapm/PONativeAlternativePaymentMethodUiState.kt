package com.processout.sdk.ui.nativeapm

import android.view.View
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.shared.model.InputParameter

internal sealed class PONativeAlternativePaymentMethodUiState {
    object Loading : PONativeAlternativePaymentMethodUiState()

    data class Loaded(
        val uiModel: PONativeAlternativePaymentMethodUiModel
    ) : PONativeAlternativePaymentMethodUiState()

    data class UserInput(
        val uiModel: PONativeAlternativePaymentMethodUiModel
    ) : PONativeAlternativePaymentMethodUiState()

    data class Submitted(
        val uiModel: PONativeAlternativePaymentMethodUiModel
    ) : PONativeAlternativePaymentMethodUiState()

    data class Capture(
        val uiModel: PONativeAlternativePaymentMethodUiModel
    ) : PONativeAlternativePaymentMethodUiState()

    data class Success(
        val uiModel: PONativeAlternativePaymentMethodUiModel
    ) : PONativeAlternativePaymentMethodUiState()

    data class Failure(
        val failure: ProcessOutResult.Failure
    ) : PONativeAlternativePaymentMethodUiState()
}

internal data class PONativeAlternativePaymentMethodUiModel(
    val title: String,
    val logoUrl: String,
    val inputParameters: List<InputParameter>,
    val focusedInputId: Int = View.NO_ID,
    val successMessage: String,
    val failureMessage: String? = null,
    val customerActionMessage: String?,
    val customerActionImageUrl: String?,
    val showCustomerAction: Boolean = customerActionMessage.isNullOrBlank().not(),
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
