@file:Suppress("MayBeConstant")

package com.processout.sdk.ui.checkout.screen

import android.view.Gravity
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.processout.sdk.api.model.response.POImageResource
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent.*
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.*
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.RegularPayment.Content.Card
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.RegularPayment.Content.NativeAlternativePayment
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.LongAnimationDurationMillis
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.PaymentLogoSize
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.RowComponentSpacing
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.ShortAnimationDurationMillis
import com.processout.sdk.ui.core.R
import com.processout.sdk.ui.core.component.*
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.checkbox.POCheckbox
import com.processout.sdk.ui.core.component.field.code.POCodeField
import com.processout.sdk.ui.core.component.field.dropdown.PODropdownField
import com.processout.sdk.ui.core.component.field.radio.PORadioButton
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup.toRadioButtonStyle
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.style.POBrandButtonStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography
import com.processout.sdk.ui.shared.component.TextAndroidView
import com.processout.sdk.ui.shared.component.isImeVisibleAsState

@Composable
internal fun DynamicCheckoutScreen(
    state: DynamicCheckoutViewModelState,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style = DynamicCheckoutScreen.style()
) {
    Column {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        Scaffold(
            modifier = Modifier
                .consumeWindowInsets(WindowInsets.statusBars)
                .clip(shape = shapes.topRoundedCornersLarge),
            containerColor = style.backgroundColor,
            bottomBar = {
                Footer(
                    state = state,
                    onEvent = onEvent,
                    containerStyle = style.actionsContainer,
                    dialogStyle = style.dialog
                )
            }
        ) { scaffoldPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = if (state is Starting) Arrangement.Center else Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (state) {
                    is Starting -> Loading(progressIndicatorColor = style.progressIndicatorColor)
                    is Started -> Content(
                        state = state,
                        onEvent = onEvent,
                        style = style
                    )
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun Loading(progressIndicatorColor: Color) {
    AnimatedVisibility {
        POCircularProgressIndicator.Large(color = progressIndicatorColor)
    }
}

@Composable
private fun Content(
    state: Started,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style
) {
    AnimatedVisibility {
        Column(
            modifier = Modifier.padding(spacing.extraLarge)
        ) {
            POMessageBox(
                text = state.errorMessage,
                style = style.messageBox,
                modifier = Modifier.padding(bottom = spacing.extraLarge),
                horizontalArrangement = Arrangement.spacedBy(RowComponentSpacing)
            )
            if (state.expressPayments.elements.isNotEmpty()) {
                ExpressPayments(
                    payments = state.expressPayments,
                    onEvent = onEvent,
                    style = style
                )
            }
            if (state.regularPayments.elements.isNotEmpty()) {
                RegularPayments(
                    payments = state.regularPayments,
                    onEvent = onEvent,
                    style = style
                )
            }
        }
    }
}

@Composable
private fun ExpressPayments(
    payments: POImmutableList<ExpressPayment>,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style
) {
    Column(
        modifier = Modifier.padding(bottom = spacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        payments.elements.forEach { payment ->
            when (payment) {
                is ExpressPayment.GooglePay -> {
                    // TODO
                }
                is ExpressPayment.Express -> ExpressPayment(
                    payment = payment,
                    onEvent = onEvent,
                    style = style
                )
            }
        }
    }
}

@Composable
private fun ExpressPayment(
    payment: ExpressPayment.Express,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style
) {
    POButton(
        text = payment.name,
        onClick = {
            onEvent(
                Action(
                    actionId = payment.submitActionId,
                    paymentMethodId = payment.id
                )
            )
        },
        modifier = Modifier.fillMaxWidth(),
        leadingContent = {
            PaymentLogo(
                logoResource = payment.logoResource,
                fallbackBoxColor = Color.Transparent,
                modifier = Modifier.padding(end = spacing.small)
            )
        }
    )
}

@Composable
private fun RegularPayments(
    payments: POImmutableList<RegularPayment>,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style
) {
    val borderWidth = style.regularPayment.border.width
    val containerShape = style.regularPayment.shape
    Column(
        modifier = Modifier
            .border(
                width = borderWidth,
                color = style.regularPayment.border.color,
                shape = containerShape
            )
            .clip(shape = containerShape)
            .padding(borderWidth),
    ) {
        payments.elements.forEachIndexed { index, payment ->
            RegularPayment(
                payment = payment,
                onEvent = onEvent,
                style = style
            )
            RegularPaymentContent(
                payment = payment,
                onEvent = onEvent,
                style = style
            )
            if (index != payments.elements.lastIndex) {
                HorizontalDivider(
                    thickness = borderWidth,
                    color = style.regularPayment.border.color
                )
            }
        }
    }
}

@Composable
private fun RegularPayment(
    payment: RegularPayment,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style
) {
    Row(
        modifier = Modifier
            .clickable(
                onClick = { onEvent(PaymentMethodSelected(id = payment.id)) },
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .fillMaxWidth()
            .padding(
                horizontal = spacing.extraLarge,
                vertical = spacing.small
            ),
        horizontalArrangement = Arrangement.spacedBy(RowComponentSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PaymentLogo(
            logoResource = payment.state.logoResource,
            fallbackBoxColor = style.regularPayment.title.color
        )
        POText(
            text = payment.state.name,
            modifier = Modifier.weight(1f),
            color = style.regularPayment.title.color,
            style = style.regularPayment.title.textStyle,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        with(payment.state) {
            if (selected && loading) {
                POCircularProgressIndicator.Small(color = style.progressIndicatorColor)
            }
        }
        PORadioButton(
            selected = payment.state.selected,
            onClick = { onEvent(PaymentMethodSelected(id = payment.id)) },
            style = style.radioGroup.toRadioButtonStyle()
        )
    }
}

@Composable
private fun RegularPaymentContent(
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
                                    actionId = id,
                                    paymentMethodId = payment.id
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

@Composable
private fun PaymentLogo(
    logoResource: POImageResource,
    fallbackBoxColor: Color,
    modifier: Modifier = Modifier
) {
    var showLogo by remember { mutableStateOf(true) }
    if (showLogo) {
        val logoUrl = with(logoResource) {
            if (isSystemInDarkTheme()) {
                darkUrl?.raster ?: lightUrl.raster
            } else {
                lightUrl.raster
            }
        }
        AsyncImage(
            model = logoUrl,
            contentDescription = null,
            modifier = modifier.requiredSize(PaymentLogoSize),
            onError = { showLogo = false }
        )
    } else {
        Box(
            modifier = modifier
                .requiredSize(PaymentLogoSize)
                .background(
                    color = fallbackBoxColor,
                    shape = shapes.roundedCornersSmall
                )
        )
    }
}

@Composable
private fun Footer(
    state: DynamicCheckoutViewModelState,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    containerStyle: POActionsContainer.Style,
    dialogStyle: PODialog.Style
) {
    Column {
        var cancelAction: POActionState? = null
        when (state) {
            is Starting -> cancelAction = state.cancelAction
            is Started -> cancelAction = state.cancelAction
            else -> {}
        }
        if (cancelAction != null) {
            POActionsContainer(
                actions = POImmutableList(listOf(cancelAction)),
                onClick = {
                    onEvent(
                        Action(
                            actionId = it,
                            paymentMethodId = null
                        )
                    )
                },
                onConfirmationRequested = { onEvent(ActionConfirmationRequested(id = it)) },
                containerStyle = containerStyle,
                dialogStyle = dialogStyle
            )
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

@Composable
private fun AnimatedVisibility(
    visibleState: MutableTransitionState<Boolean> = remember {
        MutableTransitionState(initialState = false)
            .apply { targetState = true }
    },
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(animationSpec = tween(durationMillis = ShortAnimationDurationMillis)),
        exit = fadeOut(animationSpec = tween(durationMillis = ShortAnimationDurationMillis))
    ) {
        content()
    }
}

internal object DynamicCheckoutScreen {

    @Immutable
    data class Style(
        val expressPaymentButton: POBrandButtonStyle?,
        val regularPayment: RegularPaymentStyle,
        val label: POText.Style,
        val field: POField.Style,
        val codeField: POField.Style,
        val radioGroup: PORadioGroup.Style,
        val checkbox: POCheckbox.Style,
        val dropdownMenu: PODropdownField.MenuStyle,
        val bodyText: TextAndroidView.Style,
        val errorText: POText.Style,
        val messageBox: POMessageBox.Style,
        val actionsContainer: POActionsContainer.Style,
        val dialog: PODialog.Style,
        val backgroundColor: Color,
        val progressIndicatorColor: Color
    )

    @Immutable
    data class RegularPaymentStyle(
        val title: POText.Style,
        val shape: Shape,
        val border: POBorderStroke,
        val description: POTextWithIcon.Style
    )

    @Composable
    fun style(custom: PODynamicCheckoutConfiguration.Style? = null) = Style(
        expressPaymentButton = custom?.expressPaymentButton,
        regularPayment = custom?.regularPayment?.custom() ?: defaultRegularPayment,
        label = custom?.label?.let {
            POText.custom(style = it)
        } ?: POText.label1,
        field = custom?.field?.let {
            POField.custom(style = it)
        } ?: POField.default,
        codeField = custom?.codeField?.let {
            POField.custom(style = it)
        } ?: POCodeField.default,
        radioGroup = custom?.radioButton?.let {
            PORadioGroup.custom(style = it)
        } ?: PORadioGroup.default,
        checkbox = custom?.checkbox?.let {
            POCheckbox.custom(style = it)
        } ?: POCheckbox.default,
        dropdownMenu = custom?.dropdownMenu?.let {
            PODropdownField.custom(style = it)
        } ?: PODropdownField.defaultMenu,
        bodyText = custom?.bodyText?.let { style ->
            val controlsTintColor = custom.controlsTintColorResId?.let { colorResource(id = it) }
            TextAndroidView.custom(
                style = style,
                controlsTintColor = controlsTintColor ?: colors.text.primary
            )
        } ?: TextAndroidView.default,
        errorText = custom?.errorText?.let {
            POText.custom(style = it)
        } ?: POText.errorLabel,
        messageBox = custom?.messageBox?.let {
            POMessageBox.custom(style = it)
        } ?: POMessageBox.error,
        actionsContainer = custom?.actionsContainer?.let {
            POActionsContainer.custom(style = it)
        } ?: POActionsContainer.default,
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

    private val defaultRegularPayment: RegularPaymentStyle
        @Composable get() {
            val description = POText.Style(
                color = colors.text.muted,
                textStyle = typography.body2
            )
            return RegularPaymentStyle(
                title = POText.subheading,
                shape = shapes.roundedCornersSmall,
                border = POBorderStroke(width = 1.dp, color = colors.border.subtle),
                description = POTextWithIcon.Style(
                    text = description,
                    iconResId = R.drawable.po_info_icon,
                    iconColorFilter = ColorFilter.tint(color = description.color)
                )
            )
        }

    @Composable
    private fun PODynamicCheckoutConfiguration.RegularPaymentStyle.custom(): RegularPaymentStyle {
        val description = POText.custom(style = description)
        return RegularPaymentStyle(
            title = POText.custom(style = title),
            shape = RoundedCornerShape(size = border.radiusDp.dp),
            border = POBorderStroke(
                width = border.widthDp.dp,
                color = colorResource(id = border.colorResId)
            ),
            description = POTextWithIcon.Style(
                text = description,
                iconResId = descriptionIconResId ?: R.drawable.po_info_icon,
                iconColorFilter = if (descriptionIconResId != null) null else
                    ColorFilter.tint(color = description.color)
            )
        )
    }

    val ShortAnimationDurationMillis = 300
    val LongAnimationDurationMillis = 600

    val RowComponentSpacing = 10.dp

    val PaymentLogoSize = 24.dp
    val CaptureLogoHeight = 34.dp
    val CaptureImageWidth = 110.dp
    val CaptureImageHeight = 140.dp

    private val ShortMessageMaxLength = 150

    fun isMessageShort(text: String) = text.length <= ShortMessageMaxLength

    fun messageGravity(text: String): Int =
        if (isMessageShort(text))
            Gravity.CENTER_HORIZONTAL
        else Gravity.START
}
