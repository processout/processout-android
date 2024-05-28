@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("MayBeConstant")

package com.processout.sdk.ui.core.component.field.dropdown

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.processout.sdk.ui.core.R
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POBorderStroke
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.text.POTextField
import com.processout.sdk.ui.core.state.POAvailableValue
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.style.PODropdownMenuStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
@Composable
fun PODropdownField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    availableValues: POImmutableList<POAvailableValue>,
    modifier: Modifier = Modifier,
    fieldStyle: POField.Style = POField.default,
    menuStyle: PODropdownField.MenuStyle = PODropdownField.defaultMenu,
    isError: Boolean = false,
    placeholderText: String? = null
) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(surface = Color.Transparent),
        shapes = MaterialTheme.shapes.copy(extraSmall = menuStyle.shape)
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
                style = fieldStyle,
                enabled = true,
                readOnly = true,
                isDropdown = true,
                isError = isError,
                placeholderText = placeholderText,
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.po_dropdown_arrow),
                        contentDescription = null,
                        modifier = Modifier.rotate(if (expanded) 180f else 0f),
                        tint = if (isError) fieldStyle.error.controlsTintColor else fieldStyle.normal.controlsTintColor
                    )
                }
            )
            val menuItemHeight = ProcessOutTheme.dimensions.formComponentHeight
            val menuVerticalPaddings = ProcessOutTheme.spacing.large
            val maxMenuHeight = remember { menuItemHeight * PODropdownField.MaxVisibleMenuItems + menuVerticalPaddings }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .exposedDropdownSize()
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
                indication = rememberRipple(color = style.rippleColor)
            )
            .fillMaxWidth()
            .padding(horizontal = ProcessOutTheme.spacing.medium),
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

/** @suppress */
@ProcessOutInternalApi
object PODropdownField {

    @Immutable
    data class MenuStyle(
        val text: POText.Style,
        val backgroundColor: Color,
        val rippleColor: Color,
        val shape: CornerBasedShape,
        val border: POBorderStroke
    )

    val defaultMenu: MenuStyle
        @Composable get() = with(ProcessOutTheme) {
            MenuStyle(
                text = POText.Style(
                    color = colors.text.primary,
                    textStyle = typography.fixed.label
                ),
                backgroundColor = colors.surface.neutral,
                rippleColor = colors.text.muted,
                shape = shapes.roundedCornersSmall,
                border = POBorderStroke(width = 0.dp, color = Color.Transparent)
            )
        }

    @Composable
    fun custom(style: PODropdownMenuStyle) = with(style) {
        MenuStyle(
            text = POText.custom(style = text),
            backgroundColor = colorResource(id = backgroundColorResId),
            rippleColor = colorResource(id = rippleColorResId),
            shape = RoundedCornerShape(size = border.radiusDp.dp),
            border = POBorderStroke(
                width = border.widthDp.dp,
                color = colorResource(id = border.colorResId)
            )
        )
    }

    internal val MaxVisibleMenuItems = 7
}
