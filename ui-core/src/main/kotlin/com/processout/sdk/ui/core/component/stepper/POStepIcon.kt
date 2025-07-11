package com.processout.sdk.ui.core.component.stepper

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.POBorderStroke
import com.processout.sdk.ui.core.style.POStepperStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POStepIcon(
    iconSize: Dp = POStepIcon.DefaultIconSize,
    padding: Dp = POStepIcon.DefaultPadding,
    style: POStepIcon.Style = POStepIcon.active
) {
    val density = LocalDensity.current
    val iconRadiusPx = with(density) { iconSize.toPx() / 2 }
    val borderWidth = style.border?.width ?: 0.dp
    val borderWidthPx = with(density) { borderWidth.toPx() }
    val haloWidth = style.halo?.width ?: 0.dp
    val haloWidthPx = with(density) { haloWidth.toPx() }
    val checkmarkWidth = style.checkmark?.width ?: 0.dp
    val checkmarkWidthPx = with(density) { checkmarkWidth.toPx() }

    val infiniteTransition = rememberInfiniteTransition()
    val animatedHaloWidthPx by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = haloWidthPx,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Canvas(
        modifier = Modifier
            .padding(padding)
            .requiredSize(size = iconSize)
    ) {
        val center = Offset(x = size.width / 2, y = size.height / 2)
        // Halo
        if (style.halo != null) {
            drawCircle(
                color = style.halo.color,
                radius = iconRadiusPx + animatedHaloWidthPx,
                center = center
            )
        }
        // Icon
        drawCircle(
            color = style.backgroundColor,
            radius = iconRadiusPx,
            center = center
        )
        // Border
        if (style.border != null) {
            drawCircle(
                color = style.border.color,
                radius = iconRadiusPx - borderWidthPx / 2,
                center = center,
                style = Stroke(width = borderWidthPx)
            )
        }
        // Checkmark
        if (style.checkmark != null) {
            val checkmarkPath = Path().apply {
                val scale = 1.3f
                val start = Offset(
                    x = center.x - size.minDimension * 0.15f * scale,
                    y = center.y
                )
                val mid = Offset(
                    x = center.x - size.minDimension * 0.05f * scale,
                    y = center.y + size.minDimension * 0.15f * scale
                )
                val end = Offset(
                    x = center.x + size.minDimension * 0.2f * scale,
                    y = center.y - size.minDimension * 0.15f * scale
                )
                moveTo(start.x, start.y)
                lineTo(mid.x, mid.y)
                lineTo(end.x, end.y)
            }
            drawPath(
                path = checkmarkPath,
                color = style.checkmark.color,
                style = Stroke(
                    width = checkmarkWidthPx,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

/** @suppress */
@ProcessOutInternalApi
object POStepIcon {

    data class Style(
        val backgroundColor: Color,
        val border: POBorderStroke?,
        val halo: Halo?,
        val checkmark: Checkmark?
    )

    data class Halo(
        val width: Dp,
        val color: Color
    )

    data class Checkmark(
        val width: Dp,
        val color: Color
    )

    internal val DefaultIconSize: Dp = 24.dp
    internal val DefaultPadding: Dp = 6.dp

    val pending: Style
        @Composable get() = Style(
            backgroundColor = colors.surface.default,
            border = POBorderStroke(
                width = 1.5.dp,
                color = colors.icon.disabled
            ),
            halo = null,
            checkmark = null
        )

    val active: Style
        @Composable get() = Style(
            backgroundColor = colors.surface.default,
            border = POBorderStroke(
                width = 1.5.dp,
                color = colors.icon.tertiary
            ),
            halo = Halo(
                width = 6.dp,
                color = colors.border.border2
            ),
            checkmark = null
        )

    val completed: Style
        @Composable get() = Style(
            backgroundColor = colors.surface.backgroundSuccess,
            border = null,
            halo = null,
            checkmark = Checkmark(
                width = 2.dp,
                color = colors.icon.inverse
            )
        )

    @Composable
    fun custom(style: POStepperStyle.IconStyle) = Style(
        backgroundColor = colorResource(id = style.backgroundColorResId),
        border = style.border?.let {
            POBorderStroke(
                width = it.widthDp.dp,
                color = colorResource(id = it.colorResId)
            )
        },
        halo = style.halo?.let {
            Halo(
                width = it.widthDp.dp,
                color = colorResource(id = it.colorResId)
            )
        },
        checkmark = style.checkmark?.let {
            Checkmark(
                width = it.widthDp.dp,
                color = colorResource(id = it.colorResId)
            )
        }
    )
}
