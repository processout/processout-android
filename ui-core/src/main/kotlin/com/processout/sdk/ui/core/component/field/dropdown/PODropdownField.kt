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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.processout.sdk.ui.core.R
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POBorderStroke
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.POField.stateStyle
import com.processout.sdk.ui.core.component.field.text.POTextField
import com.processout.sdk.ui.core.state.POAvailableValue
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.style.PODropdownMenuStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography

/** @suppress */
@ProcessOutInternalApi
@Composable
fun PODropdownField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    availableValues: POImmutableList<POAvailableValue>,
    modifier: Modifier = Modifier,
    fieldContentPadding: PaddingValues = POField.contentPadding,
    fieldStyle: POField.Style = POField.default,
    menuStyle: PODropdownField.MenuStyle = PODropdownField.defaultMenu,
    menuMatchesTextFieldWidth: Boolean = true,
    preferFormattedTextSelection: Boolean = false,
    enabled: Boolean = true,
    isError: Boolean = false,
    placeholder: String? = null
) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(surface = Color.Transparent),
        shapes = MaterialTheme.shapes.copy(extraSmall = menuStyle.shape)
    ) {
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                if (enabled) {
                    expanded = it
                }
            }
        ) {
            var isFocused by remember { mutableStateOf(false) }
            val fieldStateStyle = fieldStyle.stateStyle(isError = isError, isFocused = isFocused)
            POTextField(
                value = availableValues.elements.find { it.value == value.text }
                    ?.let {
                        TextFieldValue(
                            text = if (preferFormattedTextSelection && it.formattedText != null)
                                it.formattedText else it.text
                        )
                    } ?: TextFieldValue(),
                onValueChange = {},
                modifier = modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .onFocusChanged {
                        isFocused = it.isFocused
                    },
                contentPadding = fieldContentPadding,
                style = fieldStyle,
                enabled = enabled,
                readOnly = true,
                isDropdown = true,
                isError = isError,
                placeholder = placeholder,
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.po_dropdown_arrow),
                        contentDescription = null,
                        modifier = Modifier.rotate(if (expanded) 180f else 0f),
                        tint = fieldStateStyle.text.color
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
        @Composable get() = MenuStyle(
            text = POText.body2,
            backgroundColor = colors.surface.neutral,
            rippleColor = colors.text.muted,
            shape = shapes.roundedCornersSmall,
            border = POBorderStroke(width = 0.dp, color = Color.Transparent)
        )

    val defaultMenu2: MenuStyle
        @Composable get() = MenuStyle(
            text = POText.Style(
                color = colors.text.primary,
                textStyle = typography.s15(FontWeight.Medium)
            ),
            backgroundColor = colors.surface.neutral,
            rippleColor = colors.text.muted,
            shape = shapes.roundedCorners6,
            border = POBorderStroke(width = 0.dp, color = Color.Transparent)
        )

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

    internal val MaxVisibleMenuItems = 6
}
