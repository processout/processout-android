package com.processout.sdk.ui.core.component.field.phone

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.state.POAvailableValue
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.state.POInputFilter

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class POPhoneNumberFieldState(
    val id: String,
    val dialingCode: TextFieldValue,
    val dialingCodes: POImmutableList<POAvailableValue>,
    val dialingCodePlaceholder: String?,
    val number: TextFieldValue,
    val numberPlaceholder: String?,
    val title: String? = null,
    val description: String? = null,
    val enabled: Boolean = true,
    val isError: Boolean = false,
    val forceTextDirectionLtr: Boolean = false,
    val inputFilter: POInputFilter? = null,
    val visualTransformation: VisualTransformation = VisualTransformation.None,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    val keyboardActionId: String? = null
)
