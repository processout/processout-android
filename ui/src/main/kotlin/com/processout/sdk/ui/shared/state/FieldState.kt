package com.processout.sdk.ui.shared.state

import androidx.annotation.DrawableRes
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.processout.sdk.ui.core.state.POAvailableValue
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.state.POInputFilter

@Immutable
internal data class FieldState(
    val id: String,
    val value: TextFieldValue = TextFieldValue(),
    val availableValues: POImmutableList<POAvailableValue>? = null,
    val length: Int? = null,
    val label: String? = null,
    val placeholder: String? = null,
    val description: String? = null,
    val contentDescription: String? = null,
    @DrawableRes val iconResId: Int? = null,
    val enabled: Boolean = true,
    val isError: Boolean = false,
    val forceTextDirectionLtr: Boolean = false,
    val inputFilter: POInputFilter? = null,
    val visualTransformation: VisualTransformation = VisualTransformation.None,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    val keyboardActionId: String? = null
)
