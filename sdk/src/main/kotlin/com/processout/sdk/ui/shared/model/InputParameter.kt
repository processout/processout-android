package com.processout.sdk.ui.shared.model

import android.text.InputType
import android.view.View
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterType
import com.processout.sdk.ui.shared.view.input.Input

internal data class InputParameter(
    val viewId: Int = View.generateViewId(),
    val focusableViewId: Int = View.generateViewId(),
    val value: String = String(),
    val hint: String? = null,
    val state: Input.State = Input.State.Default(),
    val keyboardAction: KeyboardAction? = null,
    val parameter: PONativeAlternativePaymentMethodParameter
) {

    fun plainValue() = when (parameter.type) {
        ParameterType.numeric -> value.replace(" ", String())
        ParameterType.phone -> value.replace(Regex("[-() ]"), String())
        else -> value
    }

    fun toInputType() = when (parameter.type) {
        ParameterType.numeric -> InputType.TYPE_CLASS_NUMBER
        ParameterType.text -> InputType.TYPE_CLASS_TEXT
        ParameterType.email -> InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        ParameterType.phone -> InputType.TYPE_CLASS_PHONE
    }

    data class KeyboardAction(
        val imeOptions: Int,
        val nextFocusForwardId: Int = View.NO_ID
    )
}
