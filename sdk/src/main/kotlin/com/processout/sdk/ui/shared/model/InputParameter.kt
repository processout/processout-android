package com.processout.sdk.ui.shared.model

import android.text.InputType
import android.view.View
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterType

internal data class InputParameter(
    val id: Int = View.generateViewId(),
    var value: String = String(),
    val hint: String?,
    val parameter: PONativeAlternativePaymentMethodParameter
) {
    fun toInputType() = when (parameter.type) {
        ParameterType.numeric -> InputType.TYPE_CLASS_NUMBER
        ParameterType.text -> InputType.TYPE_CLASS_TEXT
        ParameterType.email -> InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        ParameterType.phone -> InputType.TYPE_CLASS_PHONE
    }
}
