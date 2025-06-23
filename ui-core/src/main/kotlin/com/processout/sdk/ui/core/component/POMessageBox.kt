package com.processout.sdk.ui.core.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.R
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.style.POMessageBoxStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POMessageBox(
    text: String?,
    modifier: Modifier = Modifier,
    style: POMessageBox.Style = POMessageBox.error,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(spacing.space10),
    enterAnimationDelayMillis: Int = 0
) {
    AnimatedVisibility(
        visible = text != null,
        modifier = Modifier.fillMaxWidth(),
        enter = fadeIn(animationSpec = tween(delayMillis = enterAnimationDelayMillis)) +
                expandIn(animationSpec = tween(delayMillis = enterAnimationDelayMillis))
    ) {
        Box(
            modifier = modifier
                .border(
                    width = style.border.width,
                    color = style.border.color,
                    shape = style.shape
                )
                .clip(style.shape)
                .background(color = style.backgroundColor)
                .padding(
                    horizontal = spacing.space12,
                    vertical = spacing.space8
                )
        ) {
            var currentText by remember { mutableStateOf(String()) }
            if (!text.isNullOrBlank()) {
                currentText = text
            }
            POTextWithIcon(
                text = currentText,
                style = style.textWithIcon,
                horizontalArrangement = horizontalArrangement
            )
        }
    }
}

/** @suppress */
@ProcessOutInternalApi
object POMessageBox {

    @Immutable
    data class Style(
        val textWithIcon: POTextWithIcon.Style,
        val shape: Shape,
        val border: POBorderStroke,
        val backgroundColor: Color
    )

    val error: Style
        @Composable get() = Style(
            textWithIcon = POTextWithIcon.default,
            shape = shapes.roundedCornersSmall,
            border = POBorderStroke(
                width = 1.dp,
                color = colors.input.borderError
            ),
            backgroundColor = colors.surface.error
        )

    @Composable
    fun custom(style: POMessageBoxStyle) = with(style) {
        val text = POText.custom(style = text)
        Style(
            textWithIcon = POTextWithIcon.Style(
                text = text,
                iconResId = iconResId ?: R.drawable.po_info_icon,
                iconColorFilter = if (iconResId != null) null else
                    ColorFilter.tint(color = text.color)
            ),
            shape = RoundedCornerShape(size = border.radiusDp.dp),
            border = POBorderStroke(
                width = border.widthDp.dp,
                color = colorResource(id = border.colorResId)
            ),
            backgroundColor = colorResource(id = backgroundColorResId)
        )
    }
}
