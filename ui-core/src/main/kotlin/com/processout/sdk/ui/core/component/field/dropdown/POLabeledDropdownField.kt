package com.processout.sdk.ui.core.component.field.dropdown

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.field.LabeledFieldLayout
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.POFieldLabels
import com.processout.sdk.ui.core.state.POAvailableValue
import com.processout.sdk.ui.core.state.POImmutableList

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POLabeledDropdownField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    availableValues: POImmutableList<POAvailableValue>,
    title: String,
    description: String?,
    modifier: Modifier = Modifier,
    fieldStyle: POField.Style = POField.default,
    menuStyle: PODropdownField.MenuStyle = PODropdownField.defaultMenu,
    labelsStyle: POFieldLabels.Style = POFieldLabels.default,
    enabled: Boolean = true,
    isError: Boolean = false,
    placeholderText: String? = null
) {
    LabeledFieldLayout(
        title = title,
        description = description,
        style = labelsStyle
    ) {
        PODropdownField(
            value = value,
            onValueChange = onValueChange,
            availableValues = availableValues,
            modifier = modifier,
            fieldStyle = fieldStyle,
            menuStyle = menuStyle,
            enabled = enabled,
            isError = isError,
            placeholderText = placeholderText
        )
    }
}
