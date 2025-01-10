package com.processout.sdk.ui.savedpaymentmethods

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.processout.sdk.ui.core.component.POText

@Composable
internal fun SavedPaymentMethodsScreen(
    state: SavedPaymentMethodsViewModelState,
    onEvent: (SavedPaymentMethodsEvent) -> Unit,
    style: SavedPaymentMethodsScreen.Style = SavedPaymentMethodsScreen.style()
) {
    POText(text = "SavedPaymentMethodsScreen")
}

internal object SavedPaymentMethodsScreen {

    @Immutable
    data class Style(
        val title: POText.Style
    )

    @Composable
    fun style(custom: POSavedPaymentMethodsConfiguration.Style? = null) = Style(
        title = custom?.title?.let {
            POText.custom(style = it)
        } ?: POText.title
    )
}
