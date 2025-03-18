package com.processout.sdk.ui.card.scanner

import android.content.Context
import android.util.Size
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.core.ImageProxy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionSelector.PREFER_CAPTURE_RATE_OVER_HIGHER_RESOLUTION
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.core.resolutionselector.ResolutionStrategy.FALLBACK_RULE_CLOSEST_LOWER
import androidx.camera.view.CameraController.IMAGE_ANALYSIS
import androidx.camera.view.CameraController.IMAGE_CAPTURE
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.processout.sdk.ui.card.scanner.CardScannerEvent.*
import com.processout.sdk.ui.core.component.POButton
import com.processout.sdk.ui.core.component.POButtonToggle
import com.processout.sdk.ui.core.component.PODialog
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography
import com.processout.sdk.ui.shared.extension.dpToPx

@Composable
internal fun CardScannerScreen(
    state: CardScannerViewModelState,
    onEvent: (CardScannerEvent) -> Unit,
    onContentHeightChanged: (Int) -> Unit,
    style: CardScannerScreen.Style = CardScannerScreen.style()
) {
    Scaffold(
        modifier = Modifier.clip(shape = shapes.topRoundedCornersLarge),
        containerColor = style.backgroundColor
    ) { scaffoldPadding ->
        val verticalSpacingPx = (spacing.large * 2).dpToPx()
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
        ) {
            POButtonToggle(
                checked = state.torchAction.checked,
                onCheckedChange = { onEvent(TorchToggle(isEnabled = it)) },
                modifier = Modifier
                    .padding(
                        top = spacing.medium,
                        start = spacing.medium
                    )
                    .requiredSizeIn(
                        minWidth = dimensions.buttonIconSizeSmall,
                        minHeight = dimensions.buttonIconSizeSmall
                    ),
                style = style.torchToggle,
                icon = state.torchAction.icon,
                iconSize = dimensions.iconSizeSmall
            )
            val density = LocalDensity.current
            var cameraPreviewHeight by remember { mutableStateOf(0.dp) }
            Column(
                modifier = Modifier
                    .padding(
                        start = spacing.large,
                        end = spacing.large,
                        bottom = spacing.large
                    )
                    .onGloballyPositioned {
                        with(density) {
                            cameraPreviewHeight = (it.size.width * 0.63f).toDp() // ISO/IEC 7810
                        }
                    },
                verticalArrangement = Arrangement.spacedBy(spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                POText(
                    text = state.title,
                    color = style.title.color,
                    style = style.title.textStyle
                )
                POText(
                    text = state.description,
                    color = style.description.color,
                    style = style.description.textStyle
                )
                CameraPreview(
                    onEvent = onEvent,
                    isTorchEnabled = state.torchAction.checked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(cameraPreviewHeight)
                        .clip(shapes.roundedCornersMedium)
                )
                state.cancelAction?.let {
                    POButton(
                        state = it,
                        onClick = { onEvent(Cancel) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeightIn(min = dimensions.buttonIconSizeSmall),
                        style = style.cancelButton,
                        confirmationDialogStyle = style.dialog,
                        iconSize = dimensions.iconSizeSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraPreview(
    onEvent: (CardScannerEvent) -> Unit,
    isTorchEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val cameraController = rememberLifecycleCameraController(
        onAnalyze = { imageProxy ->
            onEvent(ImageAnalysis(imageProxy))
        }
    )
    LaunchedEffect(isTorchEnabled) {
        cameraController.enableTorch(isTorchEnabled)
    }
    AndroidView(
        modifier = modifier,
        factory = {
            PreviewView(it).apply {
                controller = cameraController
                clipToOutline = true
                scaleType = PreviewView.ScaleType.FILL_START
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        onRelease = { cameraController.unbind() }
    )
}

@Composable
private fun rememberLifecycleCameraController(
    onAnalyze: (ImageProxy) -> Unit,
    context: Context = LocalContext.current,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
): LifecycleCameraController = remember {
    val executor = ContextCompat.getMainExecutor(context)
    LifecycleCameraController(context).apply {
        cameraSelector = DEFAULT_BACK_CAMERA
        initializationFuture.addListener(
            {
                setEnabledUseCases(IMAGE_CAPTURE or IMAGE_ANALYSIS)
                imageCaptureMode = CAPTURE_MODE_MAXIMIZE_QUALITY
                imageAnalysisOutputImageFormat = OUTPUT_IMAGE_FORMAT_YUV_420_888
                imageAnalysisBackpressureStrategy = STRATEGY_KEEP_ONLY_LATEST
                imageAnalysisResolutionSelector = ResolutionSelector.Builder()
                    .setAllowedResolutionMode(PREFER_CAPTURE_RATE_OVER_HIGHER_RESOLUTION)
                    .setResolutionStrategy(
                        ResolutionStrategy(
                            Size(1280, 960),
                            FALLBACK_RULE_CLOSEST_LOWER
                        )
                    ).build()
                setImageAnalysisAnalyzer(executor) { imageProxy ->
                    onAnalyze(imageProxy)
                }
                bindToLifecycle(lifecycleOwner)
            },
            executor
        )
    }
}

internal object CardScannerScreen {

    @Immutable
    data class Style(
        val title: POText.Style,
        val description: POText.Style,
        val torchToggle: POButton.Style,
        val cancelButton: POButton.Style,
        val dialog: PODialog.Style,
        val backgroundColor: Color
    )

    @Composable
    fun style(custom: POCardScannerConfiguration.Style? = null) = Style(
        title = custom?.title?.let {
            POText.custom(style = it)
        } ?: POText.body1,
        description = custom?.description?.let {
            POText.custom(style = it)
        } ?: POText.Style(
            color = colors.text.muted,
            textStyle = typography.body2
        ),
        torchToggle = custom?.torchToggle?.let {
            POButton.custom(style = it)
        } ?: POButton.ghostEqualPadding,
        cancelButton = custom?.cancelButton?.let {
            POButton.custom(style = it)
        } ?: POButton.secondary,
        dialog = custom?.dialog?.let {
            PODialog.custom(style = it)
        } ?: PODialog.default,
        backgroundColor = custom?.backgroundColorResId?.let {
            colorResource(id = it)
        } ?: colors.surface.default
    )
}
