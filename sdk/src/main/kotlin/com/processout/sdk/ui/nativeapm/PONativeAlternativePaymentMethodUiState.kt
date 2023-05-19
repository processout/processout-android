package com.processout.sdk.ui.nativeapm

import android.view.View
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.shared.model.InputParameter
import com.processout.sdk.ui.shared.model.SecondaryActionUiModel
import com.processout.sdk.ui.shared.view.input.Input

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
    val customerActionMessage: String?,
    val customerActionImageUrl: String?,
    val primaryActionText: String,
    val secondaryAction: SecondaryActionUiModel?,
    val paymentConfirmationSecondaryAction: SecondaryActionUiModel?,
    val isSubmitting: Boolean
) {
    fun isSubmitAllowed() = inputParameters.all { it.state is Input.State.Default }
    fun showCustomerAction() = customerActionMessage.isNullOrBlank().not()
}

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
