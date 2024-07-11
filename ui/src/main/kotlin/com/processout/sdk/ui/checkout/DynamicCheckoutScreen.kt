package com.processout.sdk.ui.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent.PaymentMethodSelected
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.Started
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup
import com.processout.sdk.ui.core.state.POAvailableValue
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
    // TODO
    val selectedPaymentId = state.regularPayments.elements
        .find { it.state.selected }?.id ?: String()
    val regularPayments = state.regularPayments.elements
        .map { regularPayment ->
            POAvailableValue(
                value = regularPayment.id,
                text = regularPayment.state.name
            )
        }
    PORadioGroup(
        value = selectedPaymentId,
        onValueChange = {
            onEvent(PaymentMethodSelected(id = it))
        },
        availableValues = POImmutableList(regularPayments)
    )
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
}
