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
    modifier: Modifier = Modifier,
    onConfirmationRequested: ((ActionId) -> Unit)? = null,
    containerStyle: POActionsContainer.Style = POActionsContainer.default,
    confirmationDialogStyle: PODialog.Style = PODialog.default,
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
        Column(modifier = modifier) {
            HorizontalDivider(thickness = 1.dp, color = containerStyle.dividerColor)
            val padding = ProcessOutTheme.spacing.extraLarge
            val spacing = ProcessOutTheme.spacing.small
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
                        onConfirmationRequested = onConfirmationRequested,
                        primaryActionStyle = containerStyle.primary,
                        secondaryActionStyle = containerStyle.secondary,
                        confirmationDialogStyle = confirmationDialogStyle
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
                        onConfirmationRequested = onConfirmationRequested,
                        primaryActionStyle = containerStyle.primary,
                        secondaryActionStyle = containerStyle.secondary,
                        confirmationDialogStyle = confirmationDialogStyle,
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
    onConfirmationRequested: ((ActionId) -> Unit)?,
    primaryActionStyle: POButton.Style,
    secondaryActionStyle: POButton.Style,
    confirmationDialogStyle: PODialog.Style,
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
                        onConfirmationRequested?.invoke(id)
                    } else {
                        onClick(id)
                    }
                },
                modifier = modifier.fillMaxWidth(),
                style = if (primary) primaryActionStyle else secondaryActionStyle,
                enabled = enabled,
                loading = loading,
                iconResId = iconResId
            )
            if (requestConfirmation) {
                confirmation?.run {
                    PODialog(
                        title = title,
                        message = message,
                        confirmActionText = confirmActionText,
                        dismissActionText = dismissActionText,
                        onConfirm = {
                            onClick(id)
                            requestConfirmation = false
                        },
                        onDismiss = {
                            requestConfirmation = false
                        },
                        style = confirmationDialogStyle
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
                backgroundColor = colors.surface.default,
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
}
