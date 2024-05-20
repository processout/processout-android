package com.processout.sdk.ui.core.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.style.PODialogStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme

/** @suppress */
@ProcessOutInternalApi
@Composable
fun PODialog(
    style: PODialog.Style = PODialog.default
) {

}

/** @suppress */
@ProcessOutInternalApi
object PODialog {

    @Immutable
    data class Style(
        val title: POText.Style,
        val message: POText.Style,
        val positiveButton: POButton.Style,
        val negativeButton: POButton.Style,
        val backgroundColor: Color
    )

    val default: Style
        @Composable get() = Style(
            title = POText.title,
            message = POText.body,
            positiveButton = POButton.primary,
            negativeButton = POButton.secondary,
            backgroundColor = ProcessOutTheme.colors.surface.level1
        )

    @Composable
    fun custom(style: PODialogStyle) = with(style) {
        Style(
            title = POText.custom(style = title),
            message = POText.custom(style = message),
            positiveButton = POButton.custom(style = positiveButton),
            negativeButton = POButton.custom(style = negativeButton),
            backgroundColor = colorResource(id = backgroundColorResId)
        )
    }
}
