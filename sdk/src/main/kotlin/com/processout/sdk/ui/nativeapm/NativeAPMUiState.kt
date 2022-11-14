package com.processout.sdk.ui.nativeapm

import android.text.InputType
import android.view.View
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterType.*

internal sealed class NativeAPMUiState {
    object Initial : NativeAPMUiState()
    object Loading : NativeAPMUiState()

    data class UserInput(val uiModel: NativeAPMUiModel) : NativeAPMUiState() {
        override fun equals(other: Any?): Boolean {
            return uiModel == other
        }

        override fun hashCode(): Int {
            return uiModel.hashCode()
        }
    }

    object Success : NativeAPMUiState()
    object Failure : NativeAPMUiState()
}

internal data class NativeAPMUiModel(
    val logoUrl: String?,
    val promptMessage: String? = null,
    val failureMessage: String? = null,
    val inputParameters: List<InputParameter>,
    val isSubmitAllowed: Boolean,
    val isSubmitting: Boolean
)

internal data class InputParameter(
    val id: Int = View.generateViewId(),
    var value: String = String(),
    val hint: String?,
    val parameter: PONativeAlternativePaymentMethodParameter
) {
    fun toInputType() = when (parameter.type) {
        numeric -> InputType.TYPE_CLASS_NUMBER
        text -> InputType.TYPE_CLASS_TEXT
        email -> InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        phone -> InputType.TYPE_CLASS_PHONE
    }
}

internal inline fun NativeAPMUiState.doWhenUserInput(
    crossinline block: (uiModel: NativeAPMUiModel) -> Unit
) {
    if (this is NativeAPMUiState.UserInput) {
        block(uiModel)
    }
}
