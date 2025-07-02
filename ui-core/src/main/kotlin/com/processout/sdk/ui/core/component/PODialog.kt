package com.processout.sdk.ui.core.component

import android.os.Build
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.style.PODialogStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography

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
            usePlatformDefaultWidth = true, // needs to be 'true', otherwise breaks edge-to-edge insets
            decorFitsSystemWindows = false
        )
    ) {
        EnableEdgeToEdge()
        var scrimAlpha by remember { mutableFloatStateOf(0f) }
        LaunchedEffect(Unit) { scrimAlpha = 0.32f }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color.Black.copy(
                        alpha = animateFloatAsState(
                            targetValue = scrimAlpha,
                            animationSpec = tween(
                                durationMillis = 200,
                                easing = FastOutLinearInEasing
                            )
                        ).value
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visibleState = remember {
                    MutableTransitionState(initialState = false)
                        .apply { targetState = true }
                },
                enter = fadeIn() + scaleIn(initialScale = 0.9f),
                exit = fadeOut()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.extraLarge),
                    shape = shapes.roundedCornersLarge,
                    color = style.backgroundColor,
                    contentColor = Color.Unspecified,
                    shadowElevation = 3.dp
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

@Composable
private fun EnableEdgeToEdge() {
    with((LocalView.current.parent as DialogWindowProvider).window) {
        setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        setWindowAnimations(-1)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            attributes.fitInsetsTypes = 0
            attributes.fitInsetsSides = 0
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
            title = POText.Style(
                color = colors.text.primary,
                textStyle = typography.s20(FontWeight.SemiBold)
            ),
            message = POText.Style(
                color = colors.text.secondary,
                textStyle = typography.paragraph.s16()
            ),
            confirmButton = defaultButton,
            dismissButton = defaultButton,
            backgroundColor = colors.surface.default
        )

    private val defaultButton: POButton.Style
        @Composable get() = with(POButton.ghost) {
            copy(
                normal = normal.copy(
                    text = POText.Style(
                        color = colors.text.primary,
                        textStyle = typography.s15(FontWeight.Medium)
                    ),
                    shape = shapes.roundedCorners6,
                    paddingHorizontal = spacing.space16
                ),
                disabled = disabled.copy(
                    text = POText.Style(
                        color = colors.text.onButtonDisabled,
                        textStyle = typography.s15(FontWeight.Medium)
                    ),
                    shape = shapes.roundedCorners6,
                    paddingHorizontal = spacing.space16
                )
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
}
