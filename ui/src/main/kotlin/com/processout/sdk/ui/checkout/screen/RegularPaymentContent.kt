package com.processout.sdk.ui.checkout.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
            payment.state.description?.let {
                POTextWithIcon(
                    text = it,
                    style = style.regularPayment.description,
                    horizontalArrangement = Arrangement.spacedBy(RowComponentSpacing)
                )
            }
            when (payment.content) {
                is Card -> CardTokenization(
                    id = payment.id,
                    state = payment.content.state,
                    onEvent = onEvent,
                    style = style
                )
                is NativeAlternativePayment -> NativeAlternativePayment(
                    id = payment.id,
                    state = payment.content.state,
                    onEvent = onEvent,
                    style = style
                )
                null -> {}
            }
            payment.submitAction?.let {
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
