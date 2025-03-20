@file:Suppress("MayBeConstant")

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.processout.sdk.ui.card.scanner.CardScannerEvent.*
import com.processout.sdk.ui.card.scanner.CardScannerScreen.CardHeightToWidthRatio
import com.processout.sdk.ui.card.scanner.recognition.POScannedCard
import com.processout.sdk.ui.core.component.*
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography
import com.processout.sdk.ui.shared.extension.dpToPx
import com.processout.sdk.ui.shared.extension.drawWithLayer

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
            val cameraPreviewOffsetCorrelation = spacing.large
            Column(
                modifier = Modifier
                    .padding(
                        start = spacing.large,
                        end = spacing.large,
                        bottom = spacing.large
                    )
                    .onGloballyPositioned {
                        with(density) {
                            val height = (it.size.width * CardHeightToWidthRatio).toDp()
                            cameraPreviewHeight = height + cameraPreviewOffsetCorrelation
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
                    currentCard = state.currentCard,
                    isTorchEnabled = state.torchAction.checked,
                    onEvent = onEvent,
                    style = style,
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(cameraPreviewHeight)
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
    currentCard: POScannedCard?,
    isTorchEnabled: Boolean,
    onEvent: (CardScannerEvent) -> Unit,
    style: CardScannerScreen.Style,
    modifier: Modifier = Modifier,
    offsetSize: Dp = spacing.extraLarge,
    cornerRadiusSize: Dp = 8.dp
) {
    val cameraController = rememberLifecycleCameraController(
        onAnalyze = { imageProxy ->
            onEvent(ImageAnalysis(imageProxy))
        }
    )
    LaunchedEffect(isTorchEnabled) {
        cameraController.enableTorch(isTorchEnabled)
    }
    val shape = RoundedCornerShape(cornerRadiusSize)
    Box(
        modifier = modifier
            .clip(shape)
            .drawWithContent {
                val offsetSizePx = offsetSize.toPx()
                val size = androidx.compose.ui.geometry.Size(
                    width = size.width - offsetSizePx * 2,
                    height = size.height - offsetSizePx * 2
                )
                val topLeftOffset = Offset(offsetSizePx, offsetSizePx)
                val cornerRadiusSizePx = cornerRadiusSize.toPx()
                val cornerRadius = CornerRadius(cornerRadiusSizePx, cornerRadiusSizePx)
                drawContent()
                drawWithLayer {
                    drawRect(
                        color = Color.Black,
                        alpha = 0.4f
                    )
                    drawRoundRect(
                        size = size,
                        topLeft = topLeftOffset,
                        cornerRadius = cornerRadius,
                        color = Color.Transparent,
                        blendMode = BlendMode.SrcIn
                    )
                }
                drawRoundRect(
                    size = size,
                    topLeft = topLeftOffset,
                    cornerRadius = cornerRadius,
                    style = Stroke(width = 1.dp.toPx()),
                    color = Color.White
                )
            }
    ) {
        AndroidView(
            modifier = modifier.clip(shape),
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
        ScannedCard(
            card = currentCard,
            style = style.card
        )
    }
}

@Composable
private fun ScannedCard(
    card: POScannedCard?,
    style: CardScannerScreen.CardStyle
) {
    Column(
        modifier = Modifier
            .padding(
                top = 80.dp,
                start = 44.dp,
                end = 44.dp
            ),
        verticalArrangement = Arrangement.spacedBy(space = 10.dp)
    ) {
        POTextAutoSize(
            text = card?.number ?: String(),
            color = style.number.color,
            style = style.number.textStyle
        )
        Row {
            POText(
                text = card?.cardholderName ?: String(),
                modifier = Modifier.weight(1f),
                color = style.cardholderName.color,
                style = style.cardholderName.textStyle,
                maxLines = 3
            )
            POTextAutoSize(
                text = card?.expiration?.formatted ?: String(),
                modifier = Modifier.padding(horizontal = spacing.large),
                color = style.expiration.color,
                style = style.expiration.textStyle
            )
        }
    }
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
        val card: CardStyle,
        val backgroundColor: Color
    )

    @Immutable
    data class CardStyle(
        val number: POText.Style,
        val expiration: POText.Style,
        val cardholderName: POText.Style
    )

    private val defaultCard: CardStyle
        @Composable get() = CardStyle(
            number = POText.Style(
                color = Color.White,
                textStyle = typography.largeTitle.copy(lineHeight = 30.sp)
            ),
            expiration = POText.Style(
                color = Color.White,
                textStyle = typography.body3.copy(lineHeight = 20.sp)
            ),
            cardholderName = POText.Style(
                color = Color.White,
                textStyle = typography.body3.copy(lineHeight = 20.sp)
            )
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
        card = custom?.card?.custom() ?: defaultCard,
        backgroundColor = custom?.backgroundColorResId?.let {
            colorResource(id = it)
        } ?: colors.surface.default
    )

    @Composable
    private fun POCardScannerConfiguration.Card.custom() =
        CardStyle(
            number = POText.custom(style = number),
            expiration = POText.custom(style = expiration),
            cardholderName = POText.custom(style = cardholderName)
        )

    /** Height to width ratio of a card by ISO/IEC 7810 standard. */
    val CardHeightToWidthRatio = 0.63f
}
