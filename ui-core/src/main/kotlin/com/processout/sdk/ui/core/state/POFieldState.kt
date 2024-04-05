package com.processout.sdk.ui.core.state

import androidx.annotation.DrawableRes
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.filter.POInputFilter

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class POFieldState(
    val id: String,
    val value: TextFieldValue = TextFieldValue(),
    val availableValues: POImmutableList<POAvailableValue>? = null,
    val title: String? = null,
    val description: String? = null,
    val placeholder: String? = null,
    @DrawableRes val iconResId: Int? = null,
    val enabled: Boolean = true,
    val isError: Boolean = false,
    val forceTextDirectionLtr: Boolean = false,
    val inputFilter: POInputFilter? = null,
    val visualTransformation: VisualTransformation = VisualTransformation.None,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    val keyboardActionId: String? = null
)
