package com.processout.sdk.ui.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.component.PODialog.cardColors
import com.processout.sdk.ui.core.style.PODialogStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing

/** @suppress */
@ProcessOutInternalApi
@Composable
fun PODialog(
    title: String,
    message: String?,
    confirmActionText: String,
    dismissActionText: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    style: PODialog.Style = PODialog.default
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = PODialog.ScrimColor),
            contentAlignment = Alignment.Center
        ) {
            with(ProcessOutTheme) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.extraLarge),
                    shape = shapes.roundedCornersLarge,
                    colors = cardColors(style.backgroundColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.extraLarge)
                    ) {
                        POText(
                            text = title,
                            color = style.title.color,
                            style = style.title.textStyle
                        )
                        if (!message.isNullOrBlank()) {
                            POText(
                                text = message,
                                modifier = Modifier.padding(top = spacing.large),
                                color = style.message.color,
                                style = style.message.textStyle
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = spacing.extraLarge),
                            horizontalArrangement = Arrangement.spacedBy(
                                space = spacing.small,
                                alignment = Alignment.End
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!dismissActionText.isNullOrBlank()) {
                                POButton(
                                    text = dismissActionText,
                                    onClick = onDismiss,
                                    modifier = Modifier.requiredHeightIn(min = dimensions.interactiveComponentMinSize),
                                    style = style.dismissButton
                                )
                            }
                            POButton(
                                text = confirmActionText,
                                onClick = onConfirm,
                                modifier = Modifier.requiredHeightIn(min = dimensions.interactiveComponentMinSize),
                                style = style.confirmButton
                            )
                        }
                    }
                }
            }
        }
    }
}

/** @suppress */
@ProcessOutInternalApi
object PODialog {

    @Immutable
    data class Style(
        val title: POText.Style,
        val message: POText.Style,
        val confirmButton: POButton.Style,
        val dismissButton: POButton.Style,
        val backgroundColor: Color
    )

    val default: Style
        @Composable get() = Style(
            title = POText.title,
            message = POText.body2,
            confirmButton = defaultButton,
            dismissButton = defaultButton,
            backgroundColor = colors.surface.default
        )

    private val defaultButton: POButton.Style
        @Composable get() = with(POButton.ghost) {
            copy(
                normal = normal.copy(paddingHorizontal = spacing.large),
                disabled = disabled.copy(paddingHorizontal = spacing.large)
            )
        }

    @Composable
    fun custom(style: PODialogStyle) = with(style) {
        Style(
            title = POText.custom(style = title),
            message = POText.custom(style = message),
            confirmButton = POButton.custom(style = confirmButton),
            dismissButton = POButton.custom(style = dismissButton),
            backgroundColor = colorResource(id = backgroundColorResId)
        )
    }

    internal val ScrimColor = Color.Black.copy(alpha = 0.32f)

    internal fun cardColors(backgroundColor: Color) = CardColors(
        containerColor = backgroundColor,
        contentColor = Color.Unspecified,
        disabledContainerColor = Color.Unspecified,
        disabledContentColor = Color.Unspecified
    )
}
