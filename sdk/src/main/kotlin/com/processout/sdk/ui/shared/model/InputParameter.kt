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

    fun plainValue() = when (parameter.type()) {
        ParameterType.NUMERIC -> value.replace(" ", String())
        ParameterType.PHONE -> value.replace(Regex("[-() ]"), String())
        else -> value
    }

    fun type() = parameter.type()

    fun toInputType() = when (type()) {
        ParameterType.NUMERIC -> InputType.TYPE_CLASS_NUMBER
        ParameterType.TEXT -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        ParameterType.EMAIL -> InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        ParameterType.PHONE -> InputType.TYPE_CLASS_PHONE
        else -> InputType.TYPE_NULL
    }

    data class KeyboardAction(
        val imeOptions: Int,
        val nextFocusForwardId: Int = View.NO_ID
    )
}
