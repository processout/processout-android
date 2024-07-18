@file:Suppress("MayBeConstant")

package com.processout.sdk.ui.checkout

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.checkout.DynamicCheckoutExtendedEvent.PaymentMethodSelected
import com.processout.sdk.ui.checkout.DynamicCheckoutScreen.FadeAnimationDurationMillis
import com.processout.sdk.ui.checkout.DynamicCheckoutScreen.ResizeAnimationDurationMillis
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.RegularPayment
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.Started
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
            containerColor = ProcessOutTheme.colors.surface.default,
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
                        onEvent = onEvent
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
    onEvent: (DynamicCheckoutEvent) -> Unit
) {
    Column(
        modifier = Modifier.padding(ProcessOutTheme.spacing.extraLarge)
    ) {
        RegularPayments(
            payments = state.regularPayments,
            onEvent = onEvent
        )
    }
}

@Composable
private fun RegularPayments(
    payments: POImmutableList<RegularPayment>,
    onEvent: (DynamicCheckoutEvent) -> Unit
) {
    val borderWidth = 1.dp
    val containerShape = ProcessOutTheme.shapes.roundedCornersSmall
    Column(
        modifier = Modifier
            .border(
                width = borderWidth,
                color = ProcessOutTheme.colors.border.subtle,
                shape = containerShape
            )
            .clip(shape = containerShape)
            .padding(borderWidth),
    ) {
        payments.elements.forEachIndexed { index, payment ->
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
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .requiredSize(24.dp)
                        .background(Color.Black)
                )
                POText(
                    text = payment.state.name,
                    modifier = Modifier.weight(1f),
                    style = ProcessOutTheme.typography.subheading,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                PORadioButton(
                    selected = payment.state.selected,
                    onClick = { onEvent(PaymentMethodSelected(id = payment.id)) }
                )
            }
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
                            top = 0.dp,
                            bottom = ProcessOutTheme.spacing.extraLarge
                        )
                ) {
                    // TODO
                }
            }
            if (index != payments.elements.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(borderWidth)
                        .background(color = ProcessOutTheme.colors.border.subtle)
                )
            }
        }
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
        val field: POField.Style
    )

    @Composable
    fun style(custom: PODynamicCheckoutConfiguration.Style? = null) = Style(
        field = custom?.field?.let {
            POField.custom(style = it)
        } ?: POField.default
    )

    val FadeAnimationDurationMillis = 500
    val ResizeAnimationDurationMillis = 300
}
