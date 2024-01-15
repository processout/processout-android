package com.processout.sdk.ui.core.state

import androidx.annotation.DrawableRes
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.filter.POInputFilter

/** @suppress */
@ProcessOutInternalApi
data class POFieldState(
    val key: String,
    val value: TextFieldValue = TextFieldValue(),
    val title: String? = null,
    val description: String? = null,
    val placeholder: String? = null,
    @DrawableRes
    val iconResId: Int? = null,
    val inputFilter: POInputFilter? = null,
    val visualTransformation: VisualTransformation? = null,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    val enabled: Boolean = true,
    val isError: Boolean = false,
    val forceTextDirectionLtr: Boolean = false
)
