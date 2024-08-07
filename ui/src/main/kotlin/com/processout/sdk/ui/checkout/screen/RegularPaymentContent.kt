package com.processout.sdk.ui.checkout.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent.Action
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.RegularPayment
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.RegularPayment.Content.Card
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.RegularPayment.Content.NativeAlternativePayment
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.LongAnimationDurationMillis
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.RowComponentSpacing
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.ShortAnimationDurationMillis
import com.processout.sdk.ui.core.component.POButton
import com.processout.sdk.ui.core.component.POTextWithIcon
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.UserInput

@Composable
internal fun RegularPaymentContent(
    payment: RegularPayment,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style
) {
    AnimatedVisibility(
        visible = payment.state.selected && !payment.state.loading,
        enter = fadeIn(animationSpec = tween(durationMillis = LongAnimationDurationMillis)) +
                expandVertically(animationSpec = tween(durationMillis = ShortAnimationDurationMillis)),
        exit = fadeOut(animationSpec = tween(durationMillis = LongAnimationDurationMillis)) +
                shrinkVertically(animationSpec = tween(durationMillis = ShortAnimationDurationMillis))
    ) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .fillMaxWidth()
                .padding(
                    start = spacing.extraLarge,
                    end = spacing.extraLarge,
                    bottom = spacing.extraLarge
                )
        ) {
            payment.state.description?.let { description ->
                with(style.regularPayment.description) {
                    POTextWithIcon(
                        text = description,
                        iconPainter = painterResource(id = iconResId),
                        style = text.textStyle,
                        textColor = text.color,
                        iconColorFilter = iconColorFilter,
                        horizontalArrangement = Arrangement.spacedBy(RowComponentSpacing)
                    )
                }
            }
            var action = payment.action
            when (payment.content) {
                is Card -> {
                    val state = payment.content.state
                    CardTokenization(
                        id = payment.id,
                        state = state,
                        onEvent = onEvent,
                        style = style
                    )
                    action = state.primaryAction
                }
                is NativeAlternativePayment -> {
                    val state = payment.content.state
                    NativeAlternativePayment(
                        id = payment.id,
                        state = state,
                        onEvent = onEvent,
                        style = style
                    )
                    action = when (state) {
                        is UserInput -> state.primaryAction
                        else -> null
                    }
                }
                null -> {}
            }
            action?.let {
                with(it) {
                    POButton(
                        text = text,
                        onClick = {
                            onEvent(
                                Action(
                                    paymentMethodId = payment.id,
                                    actionId = id
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = spacing.extraLarge),
                        style = style.actionsContainer.primary,
                        enabled = enabled,
                        loading = loading
                    )
                }
            }
        }
    }
}
