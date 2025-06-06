package com.processout.sdk.ui.core.state

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.transformation.POPhoneNumberVisualTransformation

/** @suppress */
@ProcessOutInternalApi
@Immutable
data class POPhoneNumberFieldState(
    val id: String,
    val regionCode: TextFieldValue,
    val regionCodes: POImmutableList<POAvailableValue>,
    val regionCodePlaceholder: String?,
    val number: TextFieldValue,
    val numberPlaceholder: String?,
    val title: String? = null,
    val description: String? = null,
    val enabled: Boolean = true,
    val isError: Boolean = false,
    val forceTextDirectionLtr: Boolean = false,
    val inputFilter: POInputFilter? = null,
    val visualTransformation: POPhoneNumberVisualTransformation? = null,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    val keyboardActionId: String? = null
)
