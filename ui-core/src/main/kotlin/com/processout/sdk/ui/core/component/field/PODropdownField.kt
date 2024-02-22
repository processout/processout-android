@file:OptIn(ExperimentalMaterial3Api::class)

package com.processout.sdk.ui.core.component.field

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.state.POAvailableValue
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
@Composable
fun PODropdownField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    availableValues: POImmutableList<POAvailableValue>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    placeholderText: String? = null,
) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(surface = Color.Transparent),
        shapes = MaterialTheme.shapes.copy(extraSmall = ProcessOutTheme.shapes.roundedCornersSmall)
    ) {
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            POTextField(
                value = availableValues.elements.find { it.value == value.text }
                    ?.let { TextFieldValue(it.text) } ?: TextFieldValue(),
                onValueChange = {},
                modifier = modifier.menuAnchor(),
                enabled = enabled,
                readOnly = true,
                isDropdown = true,
                isError = isError,
                placeholderText = placeholderText,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .exposedDropdownSize()
                    .requiredHeightIn(max = 324.dp)
                    .background(color = ProcessOutTheme.colors.surface.neutral),
                properties = PopupProperties(
                    focusable = true,
                    dismissOnClickOutside = true,
                    dismissOnBackPress = true
                )
            ) {
                availableValues.elements.forEach { availableValue ->
                    PODropdownMenuItem(
                        availableValue = availableValue,
                        onClick = {
                            expanded = false
                            onValueChange(TextFieldValue(it.value))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PODropdownMenuItem(
    availableValue: POAvailableValue,
    onClick: (POAvailableValue) -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Box(
        modifier = modifier
            .clickable(
                onClick = { onClick(availableValue) },
                interactionSource = interactionSource,
                indication = rememberRipple(color = ProcessOutTheme.colors.text.muted)
            )
            .fillMaxWidth()
            .requiredHeight(44.dp)
            .padding(horizontal = ProcessOutTheme.spacing.medium),
        contentAlignment = Alignment.CenterStart
    ) {
        POText(
            text = availableValue.text,
            color = ProcessOutTheme.colors.text.primary,
            style = ProcessOutTheme.typography.fixed.label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** @suppress */
@ProcessOutInternalApi
object PODropdownField {

}
