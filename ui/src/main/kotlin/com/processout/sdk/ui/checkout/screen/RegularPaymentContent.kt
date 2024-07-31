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
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.RegularPayment
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.RegularPayment.Content.Card
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.RegularPayment.Content.NativeAlternativePayment
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.FadeAnimationDurationMillis
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.ResizeAnimationDurationMillis
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.RowComponentSpacing
import com.processout.sdk.ui.core.component.POTextWithIcon
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing

@Composable
internal fun RegularPaymentContent(
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
        }
    }
}
