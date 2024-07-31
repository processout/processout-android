package com.processout.sdk.ui.checkout.screen

import androidx.compose.runtime.Composable
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent

@Composable
internal fun CardTokenization(
    id: String,
    state: CardTokenizationViewModelState,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style
) {

}
