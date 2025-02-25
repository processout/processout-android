package com.processout.sdk.ui.card.scanner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.component.POButton
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.shared.component.CameraPreview
import com.processout.sdk.ui.shared.extension.dpToPx

@Composable
internal fun CardScannerScreen(
    state: CardScannerViewModelState,
    onEvent: (CardScannerEvent) -> Unit,
    onContentHeightChanged: (Int) -> Unit,
    style: CardScannerScreen.Style?
) {
    Scaffold(
        modifier = Modifier.clip(shape = shapes.topRoundedCornersLarge),
        containerColor = colors.surface.default
    ) { scaffoldPadding ->
        val density = LocalDensity.current
        var cameraPreviewHeight by remember { mutableStateOf(0.dp) }
        val verticalSpacingPx = spacing.extraLarge.dpToPx()
        Column(
            modifier = Modifier
                .verticalScroll(
                    state = rememberScrollState(),
                    enabled = false
                )
                .onGloballyPositioned {
                    val contentHeight = it.size.height + verticalSpacingPx
                    onContentHeightChanged(contentHeight)
                }
                .padding(scaffoldPadding)
                .padding(spacing.extraLarge)
                .onGloballyPositioned {
                    with(density) {
                        cameraPreviewHeight = (it.size.width * 0.63f).toDp()
                    }
                }
        ) {
            CameraPreview(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(cameraPreviewHeight)
                    .clip(shapes.roundedCornersMedium)
            )
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
