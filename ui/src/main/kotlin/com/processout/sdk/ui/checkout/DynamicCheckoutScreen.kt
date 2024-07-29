@file:Suppress("MayBeConstant")

package com.processout.sdk.ui.checkout

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.R
import com.processout.sdk.ui.checkout.DynamicCheckoutExtendedEvent.PaymentMethodSelected
import com.processout.sdk.ui.checkout.DynamicCheckoutScreen.FadeAnimationDurationMillis
import com.processout.sdk.ui.checkout.DynamicCheckoutScreen.InfoIconSize
import com.processout.sdk.ui.checkout.DynamicCheckoutScreen.ResizeAnimationDurationMillis
import com.processout.sdk.ui.checkout.DynamicCheckoutScreen.RowComponentSpacing
import com.processout.sdk.ui.checkout.DynamicCheckoutScreen.infoPaddingValues
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.RegularPayment
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.Started
import com.processout.sdk.ui.core.component.POActionsContainer
import com.processout.sdk.ui.core.component.POBorderStroke
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.radio.PORadioButton
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.shared.component.isImeVisibleAsState

@Composable
internal fun DynamicCheckoutScreen(
    state: DynamicCheckoutViewModelState,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style = DynamicCheckoutScreen.style()
) {
    Column {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.systemBars))
        Scaffold(
            modifier = Modifier.clip(shape = ProcessOutTheme.shapes.topRoundedCornersLarge),
            containerColor = style.backgroundColor,
            topBar = { Header() },
            bottomBar = { Footer() }
        ) { scaffoldPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                when (state) {
                    is Started -> Content(
                        state = state,
                        onEvent = onEvent,
                        style = style
                    )
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun Header() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TODO
    }
}

@Composable
private fun Content(
    state: Started,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style
) {
    Column(
        modifier = Modifier.padding(ProcessOutTheme.spacing.extraLarge)
    ) {
        RegularPayments(
            payments = state.regularPayments,
            onEvent = onEvent,
            style = style
        )
    }
}

@Composable
private fun RegularPayments(
    payments: POImmutableList<RegularPayment>,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style
) {
    val borderWidth = style.regularPayment.border.width
    val containerShape = style.regularPayment.shape
    Column(
        modifier = Modifier
            .border(
                width = borderWidth,
                color = style.regularPayment.border.color,
                shape = containerShape
            )
            .clip(shape = containerShape)
            .padding(borderWidth),
    ) {
        payments.elements.forEachIndexed { index, payment ->
            RegularPayment(
                payment = payment,
                onEvent = onEvent,
                style = style
            )
            RegularPaymentContent(
                payment = payment,
                onEvent = onEvent,
                style = style
            )
            if (index != payments.elements.lastIndex) {
                HorizontalDivider(
                    thickness = borderWidth,
                    color = style.regularPayment.border.color
                )
            }
        }
    }
}

@Composable
private fun RegularPayment(
    payment: RegularPayment,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style
) {
    Row(
        modifier = Modifier
            .clickable(
                onClick = { onEvent(PaymentMethodSelected(id = payment.id)) },
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .fillMaxWidth()
            .padding(
                horizontal = ProcessOutTheme.spacing.extraLarge,
                vertical = ProcessOutTheme.spacing.small
            ),
        horizontalArrangement = Arrangement.spacedBy(RowComponentSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TODO: logo
        Box(
            modifier = Modifier
                .requiredSize(24.dp)
                .background(Color.Black)
        )
        POText(
            text = payment.state.name,
            modifier = Modifier.weight(1f),
            color = style.regularPayment.title.color,
            style = style.regularPayment.title.textStyle,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        PORadioButton(
            selected = payment.state.selected,
            onClick = { onEvent(PaymentMethodSelected(id = payment.id)) }
        )
    }
}

@Composable
private fun RegularPaymentContent(
    payment: RegularPayment,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style
) {
    AnimatedVisibility(
        visible = payment.state.selected,
        enter = fadeIn(animationSpec = tween(durationMillis = FadeAnimationDurationMillis)) +
                expandVertically(animationSpec = tween(durationMillis = ResizeAnimationDurationMillis)),
        exit = fadeOut(animationSpec = tween(durationMillis = FadeAnimationDurationMillis)) +
                shrinkVertically(animationSpec = tween(durationMillis = ResizeAnimationDurationMillis))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = ProcessOutTheme.spacing.extraLarge,
                    end = ProcessOutTheme.spacing.extraLarge,
                    bottom = ProcessOutTheme.spacing.extraLarge
                )
        ) {
            payment.state.description?.let { description ->
                Info(
                    text = description,
                    style = style.regularPayment.description
                )
            }
        }
    }
}

@Composable
private fun Info(
    text: String,
    style: POText.Style
) {
    Row {
        val infoPaddingValues = infoPaddingValues(textStyle = style.textStyle)
        Image(
            painter = painterResource(id = R.drawable.po_info_icon),
            contentDescription = null,
            modifier = Modifier
                .padding(
                    top = infoPaddingValues.iconPaddingTop
                )
                .requiredSize(InfoIconSize),
            colorFilter = ColorFilter.tint(color = style.color)
        )
        POText(
            text = text,
            color = style.color,
            style = style.textStyle,
            modifier = Modifier.padding(
                top = infoPaddingValues.textPaddingTop,
                start = RowComponentSpacing
            )
        )
    }
}

@Composable
private fun Footer() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // TODO
        }
        val isImeVisible by isImeVisibleAsState()
        val imePaddingValues = WindowInsets.ime.asPaddingValues()
        val systemBarsPaddingValues = WindowInsets.systemBars.asPaddingValues()
        Spacer(
            Modifier.requiredHeight(
                if (isImeVisible) imePaddingValues.calculateBottomPadding()
                else systemBarsPaddingValues.calculateBottomPadding()
            )
        )
    }
}

internal object DynamicCheckoutScreen {

    @Immutable
    data class Style(
        val regularPayment: RegularPaymentStyle,
        val field: POField.Style,
        val actionsContainer: POActionsContainer.Style,
        val backgroundColor: Color
    )

    @Immutable
    data class RegularPaymentStyle(
        val title: POText.Style,
        val description: POText.Style,
        val shape: Shape,
        val border: POBorderStroke
    )

    @Composable
    fun style(custom: PODynamicCheckoutConfiguration.Style? = null) = Style(
        regularPayment = custom?.regularPayment?.custom() ?: defaultRegularPayment,
        field = custom?.field?.let {
            POField.custom(style = it)
        } ?: POField.default,
        actionsContainer = custom?.actionsContainer?.let {
            POActionsContainer.custom(style = it)
        } ?: POActionsContainer.default,
        backgroundColor = custom?.backgroundColorResId?.let {
            colorResource(id = it)
        } ?: ProcessOutTheme.colors.surface.default
    )

    private val defaultRegularPayment: RegularPaymentStyle
        @Composable get() = with(ProcessOutTheme) {
            RegularPaymentStyle(
                title = POText.subheading,
                description = POText.Style(
                    color = colors.text.muted,
                    textStyle = typography.body2
                ),
                shape = shapes.roundedCornersSmall,
                border = POBorderStroke(width = 1.dp, color = colors.border.subtle)
            )
        }

    @Composable
    private fun PODynamicCheckoutConfiguration.RegularPaymentStyle.custom() =
        RegularPaymentStyle(
            title = POText.custom(style = title),
            description = POText.custom(style = description),
            shape = RoundedCornerShape(size = border.radiusDp.dp),
            border = POBorderStroke(
                width = border.widthDp.dp,
                color = colorResource(id = border.colorResId)
            )
        )

    val FadeAnimationDurationMillis = 500
    val ResizeAnimationDurationMillis = 300

    val RowComponentSpacing = 10.dp

    val InfoIconSize = 14.dp

    @Immutable
    data class InfoPaddingValues(
        val iconPaddingTop: Dp,
        val textPaddingTop: Dp
    )

    @Composable
    fun infoPaddingValues(textStyle: TextStyle): InfoPaddingValues {
        val textMeasurer = rememberTextMeasurer()
        val singleLineTextMeasurement = remember(textStyle) {
            textMeasurer.measure(text = String(), style = textStyle)
        }
        val density = LocalDensity.current
        return remember(singleLineTextMeasurement) {
            with(density) {
                val infoIconCenterVertical = InfoIconSize / 2
                val singleLineTextCenterVertical = singleLineTextMeasurement.size.height.toDp() / 2
                val paddingTop = singleLineTextCenterVertical - infoIconCenterVertical
                InfoPaddingValues(
                    iconPaddingTop = if (paddingTop > 0.dp) paddingTop else 0.dp,
                    textPaddingTop = if (paddingTop < 0.dp) paddingTop.unaryMinus() else 0.dp
                )
            }
        }
    }
}
