package com.processout.sdk.ui.savedpaymentmethods

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.core.component.*
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsEvent.Action
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsViewModelState.Content.*

@Composable
internal fun SavedPaymentMethodsScreen(
    state: SavedPaymentMethodsViewModelState,
    onEvent: (SavedPaymentMethodsEvent) -> Unit,
    style: SavedPaymentMethodsScreen.Style = SavedPaymentMethodsScreen.style()
) {
    Scaffold(
        modifier = Modifier
            .nestedScroll(rememberNestedScrollInteropConnection())
            .clip(shape = shapes.topRoundedCornersLarge),
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
            ) {
                state.cancelAction?.let { action ->
                    POButton(
                        state = action,
                        onClick = {
                            onEvent(
                                Action(
                                    actionId = it,
                                    paymentMethodId = null
                                )
                            )
                        },
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .requiredSizeIn(
                                minWidth = dimensions.buttonIconSizeMedium,
                                minHeight = dimensions.buttonIconSizeMedium
                            ),
                        style = style.cancelButton,
                        confirmationDialogStyle = style.dialog,
                        onConfirmationRequested = {}, // TODO
                        iconSize = dimensions.iconSizeMedium
                    )
                }
            }
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .verticalScroll(rememberScrollState())
                .padding(ProcessOutTheme.spacing.extraLarge),
            verticalArrangement = if (state.content is Starting) Arrangement.Center else Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state.content) {
                Starting -> POCircularProgressIndicator.Large(color = style.progressIndicatorColor)
                is Started -> {
                    // TODO
                }
                is Empty -> {
                    // TODO
                }
            }
        }
    }
}

internal object SavedPaymentMethodsScreen {

    @Immutable
    data class Style(
        val headerStyle: HeaderStyle,
        val paymentMethod: PaymentMethodStyle,
        val messageBox: POMessageBox.Style,
        val cancelButton: POButton.Style,
        val dialog: PODialog.Style,
        val backgroundColor: Color,
        val progressIndicatorColor: Color
    )

    @Immutable
    data class HeaderStyle(
        val title: POText.Style,
        val dividerColor: Color,
        val dragHandleColor: Color,
        val backgroundColor: Color
    )

    @Immutable
    data class PaymentMethodStyle(
        val description: POText.Style,
        val deleteButton: POButton.Style,
        val shape: Shape,
        val border: POBorderStroke
    )

    @Composable
    fun style(custom: POSavedPaymentMethodsConfiguration.Style? = null) = Style(
        headerStyle = custom?.header?.custom() ?: defaultHeader,
        paymentMethod = custom?.paymentMethod?.custom() ?: defaultPaymentMethod,
        messageBox = custom?.messageBox?.let {
            POMessageBox.custom(style = it)
        } ?: POMessageBox.error,
        cancelButton = custom?.cancelButton?.let {
            POButton.custom(style = it)
        } ?: POButton.ghostEqualPadding,
        dialog = custom?.dialog?.let {
            PODialog.custom(style = it)
        } ?: PODialog.default,
        backgroundColor = custom?.backgroundColorResId?.let {
            colorResource(id = it)
        } ?: colors.surface.default,
        progressIndicatorColor = custom?.progressIndicatorColorResId?.let {
            colorResource(id = it)
        } ?: colors.button.primaryBackgroundDefault
    )

    private val defaultHeader: HeaderStyle
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
            } ?: defaultHeader.dividerColor,
            dragHandleColor = dragHandleColorResId?.let {
                colorResource(id = it)
            } ?: defaultHeader.dragHandleColor,
            backgroundColor = backgroundColorResId?.let {
                colorResource(id = it)
            } ?: defaultHeader.backgroundColor
        )

    private val defaultPaymentMethod: PaymentMethodStyle
        @Composable get() = PaymentMethodStyle(
            description = POText.body1,
            deleteButton = POButton.ghostEqualPadding,
            shape = shapes.roundedCornersSmall,
            border = POBorderStroke(width = 1.dp, color = colors.border.subtle)
        )

    @Composable
    private fun POSavedPaymentMethodsConfiguration.PaymentMethodStyle.custom() =
        PaymentMethodStyle(
            description = POText.custom(style = description),
            deleteButton = POButton.custom(style = deleteButton),
            shape = RoundedCornerShape(size = border.radiusDp.dp),
            border = POBorderStroke(
                width = border.widthDp.dp,
                color = colorResource(id = border.colorResId)
            )
        )
}
