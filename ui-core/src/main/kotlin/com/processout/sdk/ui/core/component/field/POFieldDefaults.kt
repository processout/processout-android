package com.processout.sdk.ui.core.component.field

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POBorderStroke
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.style.input.POInputStateStyle
import com.processout.sdk.ui.core.style.input.POInputStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
object POFieldDefaults {

    @Composable
    fun colors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
        disabledBorderColor = Color.Transparent,
        errorBorderColor = Color.Transparent,
    )

    val default: POFieldStyle
        @Composable get() = with(ProcessOutTheme) {
            POFieldStyle(
                normal = POFieldStateStyle(
                    text = POText.Style(
                        color = colors.text.primary,
                        textStyle = typography.fixed.label
                    ),
                    hintTextColor = colors.text.muted,
                    backgroundColor = colors.surface.background,
                    controlsTintColor = colors.text.primary,
                    shape = shapes.roundedCornersSmall,
                    border = POBorderStroke(width = 1.dp, color = colors.border.default)
                ),
                error = POFieldStateStyle(
                    text = POText.Style(
                        color = colors.text.primary,
                        textStyle = typography.fixed.label
                    ),
                    hintTextColor = colors.text.muted,
                    backgroundColor = colors.surface.background,
                    controlsTintColor = colors.text.error,
                    shape = shapes.roundedCornersSmall,
                    border = POBorderStroke(width = 1.dp, color = colors.text.error)
                ),
                baseField = POBaseField.default
            )
        }

    @Composable
    fun custom(style: POInputStyle) = POFieldStyle(
        normal = style.normal.toStateStyle(),
        error = style.error.toStateStyle(),
        baseField = POBaseField.custom(style = style)
    )

    @Composable
    private fun POInputStateStyle.toStateStyle() = with(field) {
        POFieldStateStyle(
            text = POText.custom(style = text),
            hintTextColor = colorResource(id = hintTextColorResId),
            backgroundColor = colorResource(id = backgroundColorResId),
            controlsTintColor = colorResource(id = controlsTintColorResId),
            shape = RoundedCornerShape(size = border.radiusDp.dp),
            border = POBorderStroke(width = border.widthDp.dp, color = colorResource(id = border.colorResId))
        )
    }
}
