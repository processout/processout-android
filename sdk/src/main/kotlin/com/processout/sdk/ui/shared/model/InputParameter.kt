package com.processout.sdk.ui.shared.model

import android.text.InputType
import android.view.View
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterType
import com.processout.sdk.ui.shared.view.input.Input

internal data class InputParameter(
    val viewId: Int = View.generateViewId(),
    val value: String = String(),
    val hint: String? = null,
    val state: Input.State = Input.State.Default,
    val parameter: PONativeAlternativePaymentMethodParameter
) {
    fun toInputType() = when (parameter.type) {
        ParameterType.numeric -> InputType.TYPE_CLASS_NUMBER
        ParameterType.text -> InputType.TYPE_CLASS_TEXT
        ParameterType.email -> InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        ParameterType.phone -> InputType.TYPE_CLASS_PHONE
    }
}
