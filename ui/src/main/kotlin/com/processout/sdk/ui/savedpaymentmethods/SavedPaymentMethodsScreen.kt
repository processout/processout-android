@file:Suppress("MayBeConstant")

package com.processout.sdk.ui.savedpaymentmethods

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.processout.sdk.api.model.response.POImageResource
import com.processout.sdk.ui.R
import com.processout.sdk.ui.core.component.*
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsEvent.Action
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsScreen.AnimationDurationMillis
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsScreen.EmptyContentImageSize
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsScreen.EmptyContentStyle
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsScreen.PaymentLogoSize
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsScreen.RowComponentSpacing
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsViewModelState.Content.*
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsViewModelState.PaymentMethod

@Composable
internal fun SavedPaymentMethodsScreen(
    state: SavedPaymentMethodsViewModelState,
    onEvent: (SavedPaymentMethodsEvent) -> Unit,
    style: SavedPaymentMethodsScreen.Style = SavedPaymentMethodsScreen.style(),
    isLightTheme: Boolean
) {
    Scaffold(
        modifier = Modifier
            .nestedScroll(rememberNestedScrollInteropConnection())
            .clip(shape = shapes.topRoundedCornersLarge),
        containerColor = style.backgroundColor,
        topBar = { Header(state, onEvent, style) }
    ) { scaffoldPadding ->
        AnimatedContent(
            targetState = state.content,
            contentKey = { it::class.java },
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = AnimationDurationMillis,
                        easing = LinearEasing
                    )
                ) togetherWith fadeOut(
                    animationSpec = tween(
                        durationMillis = AnimationDurationMillis,
                        easing = LinearEasing
                    )
                )
            }
        ) { content ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(spacing.extraLarge),
                verticalArrangement = if (content is Loaded) Arrangement.Top else Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (content) {
                    Loading -> POCircularProgressIndicator.Large(color = style.progressIndicatorColor)
                    is Loaded -> Content(
                        paymentMethods = content.paymentMethods,
                        onEvent = onEvent,
                        style = style,
                        isLightTheme = isLightTheme
                    )
                    is Empty -> Empty(
                        state = content,
                        style = style.emptyContentStyle
                    )
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
private fun Content(
    paymentMethods: POImmutableList<PaymentMethod>,
    onEvent: (SavedPaymentMethodsEvent) -> Unit,
    style: SavedPaymentMethodsScreen.Style,
    isLightTheme: Boolean
) {
    val borderWidth = style.paymentMethod.border.width
    val borderColor = style.paymentMethod.border.color
    val containerShape = style.paymentMethod.shape
    Column(
        modifier = Modifier
            .border(
                width = borderWidth,
                color = borderColor,
                shape = containerShape
            )
            .clip(shape = containerShape)
            .padding(borderWidth)
            .background(style.paymentMethod.backgroundColor)
    ) {
        paymentMethods.elements.forEachIndexed { index, paymentMethod ->
            PaymentMethod(
                paymentMethod = paymentMethod,
                onEvent = onEvent,
                style = style,
                isLightTheme = isLightTheme
            )
            if (index != paymentMethods.elements.lastIndex) {
                HorizontalDivider(
                    thickness = borderWidth,
                    color = borderColor
                )
            }
        }
    }
}

@Composable
private fun PaymentMethod(
    paymentMethod: PaymentMethod,
    onEvent: (SavedPaymentMethodsEvent) -> Unit,
    style: SavedPaymentMethodsScreen.Style,
    isLightTheme: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = spacing.extraLarge,
                end = spacing.medium,
                top = spacing.large,
                bottom = spacing.large
            ),
        horizontalArrangement = Arrangement.spacedBy(RowComponentSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PaymentLogo(
            logoResource = paymentMethod.logo,
            fallbackBoxColor = style.paymentMethod.description.color,
            isLightTheme = isLightTheme
        )
        POText(
            text = paymentMethod.description,
            modifier = Modifier.weight(1f),
            color = style.paymentMethod.description.color,
            style = style.paymentMethod.description.textStyle,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        paymentMethod.deleteAction?.let { action ->
            POButton(
                state = action,
                onClick = {
                    onEvent(
                        Action(
                            actionId = it,
                            paymentMethodId = paymentMethod.id
                        )
                    )
                },
                modifier = Modifier.requiredSizeIn(
                    minWidth = dimensions.buttonIconSizeSmall,
                    minHeight = dimensions.buttonIconSizeSmall
                ),
                style = style.paymentMethod.deleteButton,
                confirmationDialogStyle = style.dialog,
                iconSize = dimensions.iconSizeSmall,
                progressIndicatorSize = POButton.ProgressIndicatorSize.Small
            )
        }
    }
}

@Composable
private fun PaymentLogo(
    logoResource: POImageResource,
    fallbackBoxColor: Color,
    modifier: Modifier = Modifier,
    isLightTheme: Boolean
) {
    var showLogo by remember { mutableStateOf(true) }
    if (showLogo) {
        val logoUrl = with(logoResource) {
            if (isLightTheme) {
                lightUrl.raster
            } else {
                darkUrl?.raster ?: lightUrl.raster
            }
        }
        AsyncImage(
            model = logoUrl,
            contentDescription = null,
            modifier = modifier.requiredSize(PaymentLogoSize),
            onError = { showLogo = false }
        )
    } else {
        Box(
            modifier = modifier
                .requiredSize(PaymentLogoSize)
                .background(
                    color = fallbackBoxColor,
                    shape = shapes.roundedCornersSmall
                )
        )
    }
}

@Composable
private fun Empty(
    state: Empty,
    style: EmptyContentStyle
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(
            space = spacing.medium,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.po_card_credit),
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = spacing.small)
                .requiredSize(EmptyContentImageSize)
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

    val RowComponentSpacing = 10.dp

    val PaymentLogoSize = 24.dp
    val EmptyContentImageSize = 48.dp
}
