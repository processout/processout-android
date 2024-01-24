package com.processout.sdk.ui.core.state

import androidx.annotation.DrawableRes
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.filter.POInputFilter

/** @suppress */
@ProcessOutInternalApi
@Stable
data class POMutableFieldState(
    val key: String,
    val value: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue()),
    val title: String? = null,
    val description: String? = null,
    val placeholder: String? = null,
    @DrawableRes
    val iconResId: MutableState<Int?> = mutableStateOf(null),
    val inputFilter: POInputFilter? = null,
    val visualTransformation: VisualTransformation = VisualTransformation.None,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    val enabled: Boolean = true,
    val isError: Boolean = false,
    val forceTextDirectionLtr: Boolean = false
)
