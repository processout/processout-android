package com.processout.sdk.ui.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.pay.button.ButtonTheme
import com.google.pay.button.ButtonType
import com.processout.sdk.ui.core.style.POGooglePayButtonStyle
import com.processout.sdk.ui.core.style.POGooglePayButtonStyle.Theme.*
import com.processout.sdk.ui.core.style.POGooglePayButtonStyle.Type.*
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions

internal object GooglePayButton {

    @Immutable
    data class Style(
        val type: ButtonType,
        val theme: ButtonTheme,
        val height: Dp,
        val borderRadius: Dp
    )

    @Composable
    fun default(isLightTheme: Boolean) = Style(
        type = ButtonType.Pay,
        theme = if (isLightTheme) ButtonTheme.Dark else ButtonTheme.Light,
        height = dimensions.interactiveComponentMinSize,
        borderRadius = 4.dp
    )

    @Composable
    fun custom(
        style: POGooglePayButtonStyle,
        isLightTheme: Boolean
    ) = with(style) {
        Style(
            type = type.map(),
            theme = theme.map(isLightTheme),
            height = if (heightDp.dp < dimensions.interactiveComponentMinSize)
                dimensions.interactiveComponentMinSize else heightDp.dp,
            borderRadius = borderRadiusDp.dp
        )
    }

    private fun POGooglePayButtonStyle.Type.map(): ButtonType =
        when (this) {
            BUY -> ButtonType.Buy
            BOOK -> ButtonType.Book
            CHECKOUT -> ButtonType.Checkout
            DONATE -> ButtonType.Donate
            ORDER -> ButtonType.Order
            PAY -> ButtonType.Pay
            SUBSCRIBE -> ButtonType.Subscribe
            PLAIN -> ButtonType.Plain
        }

    private fun POGooglePayButtonStyle.Theme.map(
        isLightTheme: Boolean
    ): ButtonTheme = when (this) {
        DARK -> ButtonTheme.Dark
        LIGHT -> ButtonTheme.Light
        AUTOMATIC -> if (isLightTheme) ButtonTheme.Dark else ButtonTheme.Light
    }
}
