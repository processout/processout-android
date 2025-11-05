package com.processout.sdk.ui.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.style.POGroupedContentStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POGroupedContent(
    items: POImmutableList<@Composable () -> Unit>,
    modifier: Modifier = Modifier,
    style: POGroupedContent.Style = POGroupedContent.default,
    title: String? = null
) {
    Column(modifier = modifier) {
        if (!title.isNullOrBlank()) {
            POText(
                text = title,
                modifier = Modifier.padding(bottom = spacing.space12),
                color = style.title.color,
                style = style.title.textStyle
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = style.border.width,
                    color = style.border.color,
                    shape = style.shape
                )
                .clip(shape = style.shape)
                .background(style.backgroundColor)
                .padding(style.border.width)
        ) {
            items.elements.forEachIndexed { index, item ->
                Box(modifier = Modifier.padding(spacing.space16)) {
                    item()
                }
                if (index != items.elements.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = spacing.space4),
                        thickness = 1.dp,
                        color = style.dividerColor
                    )
                }
            }
        }
    }
}

/** @suppress */
@ProcessOutInternalApi
object POGroupedContent {

    @Immutable
    data class Style(
        val title: POText.Style,
        val shape: Shape,
        val border: POBorderStroke,
        val dividerColor: Color,
        val backgroundColor: Color
    )

    val default: Style
        @Composable get() = Style(
            title = POText.Style(
                color = colors.text.primary,
                textStyle = typography.s16(FontWeight.Medium)
            ),
            shape = shapes.roundedCorners6,
            border = POBorderStroke(width = 1.5.dp, color = colors.input.borderDefault),
            dividerColor = colors.border.border1,
            backgroundColor = colors.surface.default
        )

    @Composable
    fun custom(style: POGroupedContentStyle) = Style(
        title = POText.custom(style = style.title),
        shape = RoundedCornerShape(size = style.border.radiusDp.dp),
        border = POBorderStroke(
            width = style.border.widthDp.dp,
            color = colorResource(id = style.border.colorResId)
        ),
        dividerColor = colorResource(id = style.dividerColorResId),
        backgroundColor = colorResource(id = style.backgroundColorResId)
    )
}
