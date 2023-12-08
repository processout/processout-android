package com.processout.sdk.ui.nativeapm

import android.view.View
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.shared.model.InputParameter
import com.processout.sdk.ui.shared.model.SecondaryActionUiModel
import com.processout.sdk.ui.shared.view.input.Input

internal sealed class NativeAlternativePaymentMethodUiState {
    data object Loading : NativeAlternativePaymentMethodUiState()

    data class Loaded(
        val uiModel: NativeAlternativePaymentMethodUiModel
    ) : NativeAlternativePaymentMethodUiState()

    data class UserInput(
        val uiModel: NativeAlternativePaymentMethodUiModel
    ) : NativeAlternativePaymentMethodUiState()

    data class Submitted(
        val uiModel: NativeAlternativePaymentMethodUiModel
    ) : NativeAlternativePaymentMethodUiState()

    data class Capture(
        val uiModel: NativeAlternativePaymentMethodUiModel
    ) : NativeAlternativePaymentMethodUiState()

    data class Success(
        val uiModel: NativeAlternativePaymentMethodUiModel
    ) : NativeAlternativePaymentMethodUiState()

    data class Failure(
        val failure: ProcessOutResult.Failure
    ) : NativeAlternativePaymentMethodUiState()
}

internal data class NativeAlternativePaymentMethodUiModel(
    val title: String?,
    val logoUrl: String?,
    val inputParameters: List<InputParameter>,
    val focusedInputId: Int = View.NO_ID,
    val successMessage: String,
    val customerActionMessageMarkdown: String?,
    val customerActionImageUrl: String?,
    val primaryActionText: String,
    val secondaryAction: SecondaryActionUiModel?,
    val paymentConfirmationSecondaryAction: SecondaryActionUiModel?,
    val isSubmitting: Boolean
) {
    fun isSubmitAllowed() = inputParameters.all { it.state is Input.State.Default }
    fun showCustomerAction() = customerActionMessageMarkdown.isNullOrBlank().not()
}

internal inline fun NativeAlternativePaymentMethodUiState.doWhenUserInput(
    crossinline block: (uiModel: NativeAlternativePaymentMethodUiModel) -> Unit
) {
    if (this is NativeAlternativePaymentMethodUiState.UserInput) {
        block(uiModel)
    }
}

internal inline fun NativeAlternativePaymentMethodUiState.doWhenCapture(
    crossinline block: (uiModel: NativeAlternativePaymentMethodUiModel) -> Unit
) {
    if (this is NativeAlternativePaymentMethodUiState.Capture) {
        block(uiModel)
    }
}
