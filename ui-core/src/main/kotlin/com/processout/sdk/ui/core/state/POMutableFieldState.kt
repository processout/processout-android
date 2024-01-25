package com.processout.sdk.ui.core.state

import androidx.annotation.DrawableRes
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.filter.POInputFilter

/** @suppress */
@ProcessOutInternalApi
@Stable
class POMutableFieldState(
    val key: String,
    val title: String? = null,
    val placeholder: String? = null,
    val forceTextDirectionLtr: Boolean = false,
    value: TextFieldValue = TextFieldValue(),
    enabled: Boolean = true,
    isError: Boolean = false,
    description: String? = null,
    @DrawableRes iconResId: Int? = null,
    inputFilter: POInputFilter? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActionKey: String? = null
) {
    var value by mutableStateOf(value)
    var enabled by mutableStateOf(enabled)
    var isError by mutableStateOf(isError)
    var description by mutableStateOf(description)
    var iconResId by mutableStateOf(iconResId)
    var inputFilter by mutableStateOf(inputFilter)
    var visualTransformation by mutableStateOf(visualTransformation)
    var keyboardOptions by mutableStateOf(keyboardOptions)
    var keyboardActionKey by mutableStateOf(keyboardActionKey)
}
