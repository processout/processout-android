package com.processout.sdk.ui.savedpaymentmethods

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import com.processout.sdk.ui.core.component.POHeader
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors

@Composable
internal fun SavedPaymentMethodsScreen(
    state: SavedPaymentMethodsViewModelState,
    onEvent: (SavedPaymentMethodsEvent) -> Unit,
    style: SavedPaymentMethodsScreen.Style = SavedPaymentMethodsScreen.style()
) {
    Scaffold(
        modifier = Modifier
            .nestedScroll(rememberNestedScrollInteropConnection())
            .clip(shape = ProcessOutTheme.shapes.topRoundedCornersLarge),
        containerColor = style.backgroundColor,
        topBar = {
            POHeader(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .background(color = style.headerStyle.backgroundColor),
                title = state.title,
                style = style.headerStyle.title,
                dividerColor = style.headerStyle.dividerColor,
                dragHandleColor = style.headerStyle.dragHandleColor,
                withDragHandle = state.draggable
            )
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .verticalScroll(rememberScrollState())
                .padding(ProcessOutTheme.spacing.extraLarge)
        ) {
            // TODO
        }
    }
}

internal object SavedPaymentMethodsScreen {

    @Immutable
    data class Style(
        val headerStyle: HeaderStyle,
        val backgroundColor: Color
    )

    @Immutable
    data class HeaderStyle(
        val title: POText.Style,
        val dividerColor: Color,
        val dragHandleColor: Color,
        val backgroundColor: Color
    )

    @Composable
    fun style(custom: POSavedPaymentMethodsConfiguration.Style? = null) = Style(
        headerStyle = custom?.headerStyle?.custom() ?: defaultHeaderStyle,
        backgroundColor = custom?.backgroundColorResId?.let {
            colorResource(id = it)
        } ?: colors.surface.default
    )

    private val defaultHeaderStyle: HeaderStyle
        @Composable get() = HeaderStyle(
            title = POText.title,
            dividerColor = colors.border.subtle,
            dragHandleColor = colors.border.subtle,
            backgroundColor = colors.surface.default
        )

    @Composable
    private fun POSavedPaymentMethodsConfiguration.HeaderStyle.custom() =
        HeaderStyle(
            title = POText.custom(style = title),
            dividerColor = dividerColorResId?.let {
                colorResource(id = it)
            } ?: defaultHeaderStyle.dividerColor,
            dragHandleColor = dragHandleColorResId?.let {
                colorResource(id = it)
            } ?: defaultHeaderStyle.dragHandleColor,
            backgroundColor = backgroundColorResId?.let {
                colorResource(id = it)
            } ?: defaultHeaderStyle.backgroundColor
        )
}
