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
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Shape
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
import com.processout.sdk.ui.card.scanner.CardScannerScreen.AnimationDurationMillis
import com.processout.sdk.ui.card.scanner.CardScannerScreen.CameraPreviewStyle
import com.processout.sdk.ui.card.scanner.CardScannerScreen.CardHeightToWidthRatio
import com.processout.sdk.ui.card.scanner.CardScannerScreen.CardStyle
import com.processout.sdk.ui.card.scanner.recognition.POScannedCard
import com.processout.sdk.ui.core.component.*
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography
import com.processout.sdk.ui.shared.extension.conditional
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
                    isTorchEnabled = state.torchAction.checked,
                    currentCard = state.currentCard,
                    onEvent = onEvent,
                    cameraPreviewStyle = style.cameraPreview,
                    cardStyle = style.card,
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
    isTorchEnabled: Boolean,
    currentCard: POScannedCard?,
    onEvent: (CardScannerEvent) -> Unit,
    cameraPreviewStyle: CameraPreviewStyle,
    cardStyle: CardStyle,
    modifier: Modifier = Modifier,
    offsetSize: Dp = spacing.extraLarge
) {
    val cameraController = rememberLifecycleCameraController(
        onAnalyze = { imageProxy ->
            onEvent(ImageAnalysis(imageProxy))
        }
    )
    LaunchedEffect(isTorchEnabled) {
        cameraController.enableTorch(isTorchEnabled)
    }
    Box(
        modifier = modifier
            .border(
                width = cameraPreviewStyle.border.width,
                color = cameraPreviewStyle.border.color,
                shape = cameraPreviewStyle.shape
            )
            .clip(cameraPreviewStyle.shape)
            .drawWithContent {
                val offsetSizePx = offsetSize.toPx()
                val cardSize = androidx.compose.ui.geometry.Size(
                    width = size.width - offsetSizePx * 2,
                    height = size.height - offsetSizePx * 2
                )
                val topLeftOffset = Offset(offsetSizePx, offsetSizePx)
                val cornerRadiusSizePx = cardStyle.borderRadius.toPx()
                val cornerRadius = CornerRadius(cornerRadiusSizePx, cornerRadiusSizePx)
                drawContent()
                drawWithLayer {
                    drawRect(color = cameraPreviewStyle.overlayColor)
                    drawRoundRect(
                        size = cardSize,
                        topLeft = topLeftOffset,
                        cornerRadius = cornerRadius,
                        color = Color.Transparent,
                        blendMode = BlendMode.SrcIn
                    )
                }
                drawRoundRect(
                    size = cardSize,
                    topLeft = topLeftOffset,
                    cornerRadius = cornerRadius,
                    style = Stroke(width = cardStyle.border.width.toPx()),
                    color = cardStyle.border.color
                )
            }
    ) {
        AndroidView(
            modifier = modifier.clip(cameraPreviewStyle.shape),
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
            style = cardStyle
        )
    }
}

@Composable
private fun ScannedCard(
    card: POScannedCard?,
    style: CardStyle
) {
    AnimatedVisibility(
        visible = card != null,
        enter = fadeIn(animationSpec = tween(durationMillis = AnimationDurationMillis)),
        exit = fadeOut(animationSpec = tween(durationMillis = AnimationDurationMillis))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 44.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.requiredHeightIn(min = 90.dp),
                verticalArrangement = Arrangement.spacedBy(space = 10.dp)
            ) {
                AnimatedContent(
                    targetState = card?.number,
                    transitionSpec = {
                        fadeIn(
                            animationSpec = tween(durationMillis = AnimationDurationMillis)
                        ) togetherWith fadeOut(
                            animationSpec = tween(durationMillis = AnimationDurationMillis)
                        )
                    }
                ) { number ->
                    POTextAutoSize(
                        text = number ?: String(),
                        modifier = Modifier.fillMaxWidth(),
                        color = style.number.color,
                        style = style.number.textStyle
                    )
                }
                Row {
                    POText(
                        text = card?.cardholderName ?: String(),
                        modifier = Modifier.weight(1f),
                        color = style.cardholderName.color,
                        style = style.cardholderName.textStyle,
                        maxLines = 2
                    )
                    val expiration = card?.expiration?.formatted ?: String()
                    POTextAutoSize(
                        text = expiration,
                        modifier = Modifier.conditional(
                            condition = expiration.isBlank(),
                            whenTrue = { requiredWidthIn(min = 88.dp) },
                            whenFalse = { padding(horizontal = spacing.large) }
                        ),
                        color = style.expiration.color,
                        style = style.expiration.textStyle
                    )
                }
            }
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
        val cameraPreview: CameraPreviewStyle,
        val card: CardStyle,
        val torchToggle: POButton.Style,
        val cancelButton: POButton.Style,
        val dialog: PODialog.Style,
        val backgroundColor: Color
    )

    @Immutable
    data class CameraPreviewStyle(
        val shape: Shape,
        val border: POBorderStroke,
        val overlayColor: Color
    )

    @Immutable
    data class CardStyle(
        val number: POText.Style,
        val expiration: POText.Style,
        val cardholderName: POText.Style,
        val border: POBorderStroke,
        val borderRadius: Dp
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
        cameraPreview = custom?.cameraPreview?.custom() ?: defaultCameraPreview,
        card = custom?.card?.custom() ?: defaultCard,
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

    private val defaultCameraPreview: CameraPreviewStyle
        @Composable get() = CameraPreviewStyle(
            shape = shapes.roundedCornersMedium,
            border = POBorderStroke(width = 0.dp, color = Color.Transparent),
            overlayColor = Color.Black.copy(alpha = 0.4f)
        )

    @Composable
    private fun POCardScannerConfiguration.CameraPreviewStyle.custom() =
        CameraPreviewStyle(
            shape = RoundedCornerShape(size = border.radiusDp.dp),
            border = POBorderStroke(
                width = border.widthDp.dp,
                color = colorResource(id = border.colorResId)
            ),
            overlayColor = colorResource(id = overlayColorResId)
        )

    private val defaultCard: CardStyle
        @Composable get() = CardStyle(
            number = POText.Style(
                color = Color.White,
                textStyle = typography.largeTitle.copy(lineHeight = 28.sp)
            ),
            expiration = POText.Style(
                color = Color.White,
                textStyle = typography.body3.copy(lineHeight = 20.sp)
            ),
            cardholderName = POText.Style(
                color = Color.White,
                textStyle = typography.body3.copy(lineHeight = 20.sp)
            ),
            border = POBorderStroke(width = 1.dp, color = Color.White),
            borderRadius = 8.dp
        )

    @Composable
    private fun POCardScannerConfiguration.CardStyle.custom() =
        CardStyle(
            number = POText.custom(style = number),
            expiration = POText.custom(style = expiration),
            cardholderName = POText.custom(style = cardholderName),
            border = POBorderStroke(
                width = border.widthDp.dp,
                color = colorResource(id = border.colorResId)
            ),
            borderRadius = border.radiusDp.dp
        )

    /** Height to width ratio of a card by ISO/IEC 7810 standard. */
    val CardHeightToWidthRatio = 0.63f

    val AnimationDurationMillis = 250
}
