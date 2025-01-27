@file:Suppress("MayBeConstant")

package com.processout.sdk.ui.savedpaymentmethods

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.R
import com.processout.sdk.ui.core.component.*
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsEvent.Action
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsScreen.AnimationDurationMillis
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsScreen.CrossfadeAnimationDurationMillis
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsScreen.EmptyContentImageSize
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsScreen.EmptyContentStyle
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsViewModelState.Content.*

@Composable
@SuppressLint("UnusedCrossfadeTargetStateParameter")
internal fun SavedPaymentMethodsScreen(
    state: SavedPaymentMethodsViewModelState,
    onEvent: (SavedPaymentMethodsEvent) -> Unit,
    style: SavedPaymentMethodsScreen.Style = SavedPaymentMethodsScreen.style()
) {
    Scaffold(
        modifier = Modifier
            .nestedScroll(rememberNestedScrollInteropConnection())
            .clip(shape = shapes.topRoundedCornersLarge),
        containerColor = style.backgroundColor,
        topBar = { Header(state, onEvent, style) }
    ) { scaffoldPadding ->
        Crossfade(
            targetState = state.content is Empty,
            animationSpec = tween(
                durationMillis = CrossfadeAnimationDurationMillis,
                easing = LinearEasing
            )
        ) {
            if (state.content is Empty) {
                Empty(
                    state = state.content,
                    style = style.emptyContentStyle
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(scaffoldPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(spacing.extraLarge),
                    verticalArrangement = if (state.content is Loaded) Arrangement.Top else Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (state.content) {
                        Loading -> Loading(progressIndicatorColor = style.progressIndicatorColor)
                        is Loaded -> {
                            // TODO
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(
    state: SavedPaymentMethodsViewModelState,
    onEvent: (SavedPaymentMethodsEvent) -> Unit,
    style: SavedPaymentMethodsScreen.Style = SavedPaymentMethodsScreen.style()
) {
    POHeader(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(color = style.header.backgroundColor),
        title = state.title,
        style = style.header.title,
        dividerColor = style.header.dividerColor,
        dragHandleColor = style.header.dragHandleColor,
        withDragHandle = state.draggable
    ) {
        state.cancelAction?.let { action ->
            POButton(
                state = action,
                onClick = {
                    onEvent(
                        Action(
                            actionId = it,
                            paymentMethodId = null
                        )
                    )
                },
                modifier = Modifier
                    .padding(end = 10.dp)
                    .requiredSizeIn(
                        minWidth = dimensions.buttonIconSizeMedium,
                        minHeight = dimensions.buttonIconSizeMedium
                    ),
                style = style.cancelButton,
                confirmationDialogStyle = style.dialog,
                iconSize = dimensions.iconSizeMedium
            )
        }
    }
}

@Composable
private fun Loading(progressIndicatorColor: Color) {
    AnimatedVisibility {
        POCircularProgressIndicator.Large(color = progressIndicatorColor)
    }
}

@Composable
private fun Empty(
    state: Empty,
    style: EmptyContentStyle
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(
            space = spacing.large,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.po_card_credit),
            contentDescription = null,
            modifier = Modifier.requiredSize(EmptyContentImageSize)
        )
        POText(
            text = state.message,
            color = style.message.color,
            style = style.message.textStyle,
            textAlign = TextAlign.Center
        )
        POText(
            text = state.description,
            color = style.description.color,
            style = style.description.textStyle,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AnimatedVisibility(
    visibleState: MutableTransitionState<Boolean> = remember {
        MutableTransitionState(initialState = false)
            .apply { targetState = true }
    },
    content: @Composable () -> Unit
) {
    androidx.compose.animation.AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(animationSpec = tween(durationMillis = AnimationDurationMillis)),
        exit = fadeOut(animationSpec = tween(durationMillis = AnimationDurationMillis))
    ) {
        content()
    }
}

internal object SavedPaymentMethodsScreen {

    @Immutable
    data class Style(
        val header: HeaderStyle,
        val paymentMethod: PaymentMethodStyle,
        val messageBox: POMessageBox.Style,
        val dialog: PODialog.Style,
        val cancelButton: POButton.Style,
        val progressIndicatorColor: Color,
        val emptyContentStyle: EmptyContentStyle,
        val backgroundColor: Color
    )

    @Immutable
    data class HeaderStyle(
        val title: POText.Style,
        val dragHandleColor: Color,
        val dividerColor: Color,
        val backgroundColor: Color
    )

    @Immutable
    data class PaymentMethodStyle(
        val description: POText.Style,
        val deleteButton: POButton.Style,
        val shape: Shape,
        val border: POBorderStroke,
        val backgroundColor: Color
    )

    @Immutable
    data class EmptyContentStyle(
        val message: POText.Style,
        val description: POText.Style
    )

    @Composable
    fun style(custom: POSavedPaymentMethodsConfiguration.Style? = null) = Style(
        header = custom?.header?.custom() ?: defaultHeader,
        paymentMethod = custom?.paymentMethod?.custom() ?: defaultPaymentMethod,
        messageBox = custom?.messageBox?.let {
            POMessageBox.custom(style = it)
        } ?: POMessageBox.error,
        dialog = custom?.dialog?.let {
            PODialog.custom(style = it)
        } ?: PODialog.default,
        cancelButton = custom?.cancelButton?.let {
            POButton.custom(style = it)
        } ?: POButton.ghostEqualPadding,
        progressIndicatorColor = custom?.progressIndicatorColorResId?.let {
            colorResource(id = it)
        } ?: colors.button.primaryBackgroundDefault,
        emptyContentStyle = EmptyContentStyle(
            message = POText.body1,
            description = POText.Style(
                color = colors.text.muted,
                textStyle = typography.body2
            )
        ),
        backgroundColor = custom?.backgroundColorResId?.let {
            colorResource(id = it)
        } ?: colors.surface.default
    )

    private val defaultHeader: HeaderStyle
        @Composable get() = HeaderStyle(
            title = POText.title,
            dragHandleColor = colors.border.subtle,
            dividerColor = colors.border.subtle,
            backgroundColor = colors.surface.default
        )

    @Composable
    private fun POSavedPaymentMethodsConfiguration.HeaderStyle.custom() =
        HeaderStyle(
            title = POText.custom(style = title),
            dragHandleColor = dragHandleColorResId?.let {
                colorResource(id = it)
            } ?: defaultHeader.dragHandleColor,
            dividerColor = dividerColorResId?.let {
                colorResource(id = it)
            } ?: defaultHeader.dividerColor,
            backgroundColor = backgroundColorResId?.let {
                colorResource(id = it)
            } ?: defaultHeader.backgroundColor
        )

    private val defaultPaymentMethod: PaymentMethodStyle
        @Composable get() = PaymentMethodStyle(
            description = POText.body1,
            deleteButton = POButton.ghostEqualPadding,
            shape = shapes.roundedCornersSmall,
            border = POBorderStroke(width = 1.dp, color = colors.border.subtle),
            backgroundColor = colors.surface.default
        )

    @Composable
    private fun POSavedPaymentMethodsConfiguration.PaymentMethodStyle.custom() =
        PaymentMethodStyle(
            description = POText.custom(style = description),
            deleteButton = POButton.custom(style = deleteButton),
            shape = border?.let {
                RoundedCornerShape(size = it.radiusDp.dp)
            } ?: defaultPaymentMethod.shape,
            border = border?.let {
                POBorderStroke(
                    width = it.widthDp.dp,
                    color = colorResource(id = it.colorResId)
                )
            } ?: defaultPaymentMethod.border,
            backgroundColor = backgroundColorResId?.let {
                colorResource(id = it)
            } ?: defaultPaymentMethod.backgroundColor
        )

    val AnimationDurationMillis = 300
    val CrossfadeAnimationDurationMillis = 400

    val EmptyContentImageSize = 48.dp
}
