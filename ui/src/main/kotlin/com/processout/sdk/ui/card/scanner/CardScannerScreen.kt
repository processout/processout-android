package com.processout.sdk.ui.card.scanner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.processout.sdk.ui.core.component.POButton
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing

@Composable
internal fun CardScannerScreen(
    state: CardScannerViewModelState,
    onEvent: (CardScannerEvent) -> Unit,
    style: CardScannerScreen.Style?
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = shapes.topRoundedCornersLarge,
        color = colors.surface.default,
        contentColor = Color.Unspecified
    ) {
        Column(
            modifier = Modifier.padding(spacing.extraLarge)
        ) {
            POText(text = "111")
            POText(text = "222")
            POText(text = "333")
        }
    }
}

internal object CardScannerScreen {

    @Immutable
    data class Style(
        val title: POText.Style,
        val description: POText.Style,
        val cancelButton: POButton.Style
    )
}
