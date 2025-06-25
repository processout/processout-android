@file:OptIn(ExperimentalMaterial3Api::class)

package com.processout.sdk.ui.core.component.field.dropdown

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.PopupProperties
import com.processout.sdk.ui.core.R
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POIme.isImeVisibleAsState
import com.processout.sdk.ui.core.component.POMessageBox
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.POField.stateStyle
import com.processout.sdk.ui.core.component.field.text.POTextField2
import com.processout.sdk.ui.core.state.POAvailableValue
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing

/** @suppress */
@ProcessOutInternalApi
@Composable
fun PODropdownField2(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    availableValues: POImmutableList<POAvailableValue>,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    contentPadding: PaddingValues = POField.contentPadding2,
    fieldStyle: POField.Style = POField.default2,
    menuStyle: PODropdownField.MenuStyle = PODropdownField.defaultMenu2,
    descriptionStyle: POMessageBox.Style = POMessageBox.error2,
    menuMatchesTextFieldWidth: Boolean = true,
    preferFormattedTextSelection: Boolean = false,
    enabled: Boolean = true,
    isError: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    description: String? = null
) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(surface = Color.Transparent),
        shapes = MaterialTheme.shapes.copy(extraSmall = menuStyle.shape)
    ) {
        var expanded by remember { mutableStateOf(false) }
        var expanding by remember { mutableStateOf(false) }
        val isImeVisible by isImeVisibleAsState(policy = structuralEqualityPolicy())
        if (expanding && isImeVisible) {
            LocalFocusManager.current.clearFocus(force = true)
        }
        LaunchedEffect(expanding, isImeVisible) {
            if (expanding && !isImeVisible) {
                expanding = false
                expanded = true
            }
        }
        ExposedDropdownMenuBox(
            modifier = modifier,
            expanded = expanded,
            onExpandedChange = {
                if (enabled) {
                    when (it) {
                        true -> expanding = true
                        false -> expanded = false
                    }
                }
            }
        ) {
            var isFocused by remember { mutableStateOf(false) }
            val fieldStateStyle = fieldStyle.stateStyle(isError = isError, isFocused = isFocused)
            POTextField2(
                value = availableValues.elements.find { it.value == value.text }
                    ?.let {
                        TextFieldValue(
                            text = if (preferFormattedTextSelection && it.formattedText != null)
                                it.formattedText else it.text
                        )
                    } ?: TextFieldValue(),
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                textFieldModifier = textFieldModifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .onFocusChanged {
                        isFocused = it.isFocused
                    },
                contentPadding = contentPadding,
                fieldStyle = fieldStyle,
                descriptionStyle = descriptionStyle,
                enabled = enabled,
                readOnly = true,
                isDropdown = true,
                isError = isError,
                label = label,
                placeholder = placeholder,
                description = description,
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.po_chevron_down),
                        contentDescription = null,
                        modifier = Modifier
                            .scale(1.1f)
                            .rotate(if (expanded) 180f else 0f),
                        tint = fieldStateStyle.labelTextColor
                    )
                }
            )
            val menuItemHeight = dimensions.formComponentMinHeight
            val menuVerticalPaddings = spacing.large
            val maxMenuHeight = remember { menuItemHeight * PODropdownField.MaxVisibleMenuItems + menuVerticalPaddings }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .exposedDropdownSize(matchTextFieldWidth = menuMatchesTextFieldWidth)
                    .heightIn(max = maxMenuHeight)
                    .border(
                        width = menuStyle.border.width,
                        color = menuStyle.border.color,
                        shape = menuStyle.shape
                    )
                    .background(color = menuStyle.backgroundColor),
                properties = PopupProperties(
                    focusable = true,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                availableValues.elements.forEach { availableValue ->
                    MenuItem(
                        availableValue = availableValue,
                        onClick = {
                            expanded = false
                            onValueChange(TextFieldValue(it.value))
                        },
                        modifier = Modifier.requiredHeight(menuItemHeight),
                        style = menuStyle
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuItem(
    availableValue: POAvailableValue,
    onClick: (POAvailableValue) -> Unit,
    modifier: Modifier = Modifier,
    style: PODropdownField.MenuStyle,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Box(
        modifier = modifier
            .clickable(
                onClick = { onClick(availableValue) },
                interactionSource = interactionSource,
                indication = ripple(color = style.rippleColor)
            )
            .fillMaxWidth()
            .padding(horizontal = spacing.large),
        contentAlignment = Alignment.CenterStart
    ) {
        POText(
            text = availableValue.text,
            color = style.text.color,
            style = style.text.textStyle,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}
