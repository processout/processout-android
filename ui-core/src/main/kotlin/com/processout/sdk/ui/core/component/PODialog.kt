package com.processout.sdk.ui.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.processout.sdk.ui.core.component.PODialog.ScrimColor
import com.processout.sdk.ui.core.component.PODialog.cardColors
import com.processout.sdk.ui.core.style.PODialogStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme

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
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = ScrimColor),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ProcessOutTheme.spacing.extraLarge),
                shape = ProcessOutTheme.shapes.roundedCornersLarge,
                colors = cardColors(style.backgroundColor)
            ) {

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
            message = POText.body,
            confirmButton = POButton.primary,
            dismissButton = POButton.secondary,
            backgroundColor = ProcessOutTheme.colors.surface.level1
        )

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
