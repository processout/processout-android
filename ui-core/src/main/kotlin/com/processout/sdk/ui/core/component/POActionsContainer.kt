package com.processout.sdk.ui.core.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.style.POActionsContainerStyle
import com.processout.sdk.ui.core.style.POAxis
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POActionsContainer(
    actions: POImmutableList<POActionState>,
    onClick: (ActionId) -> Unit,
    style: POActionsContainer.Style = POActionsContainer.default,
    animationDurationMillis: Int = 0
) {
    var currentActions by remember {
        mutableStateOf<POImmutableList<POActionState>>(POImmutableList(emptyList()))
    }
    if (actions.elements.isNotEmpty()) {
        currentActions = actions
    }
    AnimatedVisibility(
        visible = actions.elements.isNotEmpty(),
        enter = fadeIn(animationSpec = tween(durationMillis = animationDurationMillis)),
        exit = fadeOut(animationSpec = tween(durationMillis = animationDurationMillis)),
    ) {
        Column {
            HorizontalDivider(thickness = 1.dp, color = style.dividerColor)

            val padding = POActionsContainer.containerPadding
            val spacing = POActionsContainer.actionSpacing

            when (style.axis) {
                POAxis.Vertical -> Column(
                    modifier = Modifier
                        .background(color = style.backgroundColor)
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    Actions(
                        actions = currentActions,
                        onClick = onClick,
                        primaryStyle = style.primary,
                        secondaryStyle = style.secondary
                    )
                }
                POAxis.Horizontal -> Row(
                    modifier = Modifier
                        .background(color = style.backgroundColor)
                        .padding(padding),
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    Actions(
                        actions = currentActions,
                        onClick = onClick,
                        modifier = Modifier.weight(1f),
                        primaryStyle = style.primary,
                        secondaryStyle = style.secondary
                    )
                }
            }
        }
    }
}

@Composable
private fun Actions(
    actions: POImmutableList<POActionState>,
    onClick: (ActionId) -> Unit,
    modifier: Modifier = Modifier,
    primaryStyle: POButton.Style = POButton.primary,
    secondaryStyle: POButton.Style = POButton.secondary
) {
    actions.elements.forEach {
        with(it) {
            POButton(
                text = text,
                onClick = { onClick(id) },
                modifier = modifier.fillMaxWidth(),
                style = if (primary) primaryStyle else secondaryStyle,
                enabled = enabled,
                loading = loading
            )
        }
    }
}

/** @suppress */
@ProcessOutInternalApi
object POActionsContainer {

    @Immutable
    data class Style(
        val primary: POButton.Style,
        val secondary: POButton.Style,
        val dividerColor: Color,
        val backgroundColor: Color,
        val axis: POAxis
    )

    val default: Style
        @Composable get() = with(ProcessOutTheme) {
            Style(
                primary = POButton.primary,
                secondary = POButton.secondary,
                dividerColor = colors.border.subtle,
                backgroundColor = colors.surface.level1,
                axis = POAxis.Vertical
            )
        }

    @Composable
    fun custom(style: POActionsContainerStyle) = with(style) {
        Style(
            primary = POButton.custom(style = primary),
            secondary = POButton.custom(style = secondary),
            dividerColor = colorResource(id = dividerColorResId),
            backgroundColor = colorResource(id = backgroundColorResId),
            axis = axis
        )
    }

    internal val containerPadding: PaddingValues
        @Composable get() = PaddingValues(
            horizontal = ProcessOutTheme.spacing.extraLarge,
            vertical = ProcessOutTheme.spacing.large
        )

    internal val actionSpacing: Dp
        @Composable get() = ProcessOutTheme.spacing.large
}
