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
    containerStyle: POActionsContainer.Style = POActionsContainer.default,
    dialogStyle: PODialog.Style = PODialog.default,
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
            HorizontalDivider(thickness = 1.dp, color = containerStyle.dividerColor)

            val padding = POActionsContainer.containerPadding
            val spacing = POActionsContainer.actionSpacing

            when (containerStyle.axis) {
                POAxis.Vertical -> Column(
                    modifier = Modifier
                        .background(color = containerStyle.backgroundColor)
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    Actions(
                        actions = currentActions,
                        onClick = onClick,
                        primaryActionStyle = containerStyle.primary,
                        secondaryActionStyle = containerStyle.secondary,
                        dialogStyle = dialogStyle
                    )
                }
                POAxis.Horizontal -> Row(
                    modifier = Modifier
                        .background(color = containerStyle.backgroundColor)
                        .padding(padding),
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    Actions(
                        actions = currentActions,
                        onClick = onClick,
                        primaryActionStyle = containerStyle.primary,
                        secondaryActionStyle = containerStyle.secondary,
                        dialogStyle = dialogStyle,
                        modifier = Modifier.weight(1f)
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
    primaryActionStyle: POButton.Style,
    secondaryActionStyle: POButton.Style,
    dialogStyle: PODialog.Style,
    modifier: Modifier = Modifier
) {
    actions.elements.forEach {
        with(it) {
            var requestConfirmation by remember { mutableStateOf(false) }
            POButton(
                text = text,
                onClick = {
                    if (confirmation != null) {
                        requestConfirmation = true
                    } else {
                        onClick(id)
                    }
                },
                modifier = modifier.fillMaxWidth(),
                style = if (primary) primaryActionStyle else secondaryActionStyle,
                enabled = enabled,
                loading = loading
            )
            if (requestConfirmation) {
                confirmation?.run {
                    PODialog(
                        title = title,
                        message = message,
                        confirmActionText = confirmActionText,
                        dismissActionText = dismissActionText,
                        onConfirm = {
                            requestConfirmation = false
                            onClick(id)
                        },
                        onDismiss = {
                            requestConfirmation = false
                        },
                        style = dialogStyle
                    )
                }
            }
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
