@file:Suppress("MayBeConstant", "MemberVisibilityCanBePrivate")

package com.processout.sdk.ui.checkout

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.pay.button.PayButton
import com.processout.sdk.api.model.response.POColor
import com.processout.sdk.api.model.response.POImageResource
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent
import com.processout.sdk.ui.card.tokenization.screen.CardTokenizationContent
import com.processout.sdk.ui.card.tokenization.screen.CardTokenizationScreen
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent.*
import com.processout.sdk.ui.checkout.DynamicCheckoutScreen.LongAnimationDurationMillis
import com.processout.sdk.ui.checkout.DynamicCheckoutScreen.PaymentLogoSize
import com.processout.sdk.ui.checkout.DynamicCheckoutScreen.PaymentSuccessStyle
import com.processout.sdk.ui.checkout.DynamicCheckoutScreen.RowComponentSpacing
import com.processout.sdk.ui.checkout.DynamicCheckoutScreen.SectionHeaderStyle
import com.processout.sdk.ui.checkout.DynamicCheckoutScreen.ShortAnimationDurationMillis
import com.processout.sdk.ui.checkout.DynamicCheckoutScreen.SuccessImageHeight
import com.processout.sdk.ui.checkout.DynamicCheckoutScreen.SuccessImageWidth
import com.processout.sdk.ui.checkout.DynamicCheckoutScreen.animatedBackgroundColor
import com.processout.sdk.ui.checkout.DynamicCheckoutScreen.cardTokenizationStyle
import com.processout.sdk.ui.checkout.DynamicCheckoutScreen.nativeAlternativePaymentStyle
import com.processout.sdk.ui.checkout.DynamicCheckoutScreen.toButtonStyle
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.*
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.Field.CheckboxField
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.RegularPayment.Content.*
import com.processout.sdk.ui.core.R
import com.processout.sdk.ui.core.component.*
import com.processout.sdk.ui.core.component.POButton.HighlightedStyle
import com.processout.sdk.ui.core.component.POButton.StateStyle
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.checkbox.POCheckbox
import com.processout.sdk.ui.core.component.field.code.POCodeField
import com.processout.sdk.ui.core.component.field.dropdown.PODropdownField
import com.processout.sdk.ui.core.component.field.radio.PORadioButton
import com.processout.sdk.ui.core.component.field.radio.PORadioField
import com.processout.sdk.ui.core.component.field.radio.PORadioField.radioButtonStyle
import com.processout.sdk.ui.core.component.stepper.POStepper
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.style.POBrandButtonStateStyle
import com.processout.sdk.ui.core.style.POBrandButtonStyle
import com.processout.sdk.ui.core.theme.PODarkColorPalette
import com.processout.sdk.ui.core.theme.POLightColorPalette
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.Stage
import com.processout.sdk.ui.napm.screen.NativeAlternativePaymentContent
import com.processout.sdk.ui.napm.screen.NativeAlternativePaymentScreen
import com.processout.sdk.ui.shared.component.AndroidTextView
import com.processout.sdk.ui.shared.component.GooglePayButton
import com.processout.sdk.ui.shared.extension.*
import com.processout.sdk.ui.shared.state.FieldState
import com.processout.sdk.ui.shared.state.FieldValue

@Composable
internal fun DynamicCheckoutScreen(
    state: DynamicCheckoutViewModelState,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style,
    isLightTheme: Boolean
) {
    Column {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        Scaffold(
            modifier = Modifier
                .consumeWindowInsets(WindowInsets.safeDrawing)
                .clip(shape = shapes.topRoundedCornersLarge),
            containerColor = animatedBackgroundColor(
                state = state,
                normalColor = style.backgroundColor,
                successColor = style.paymentSuccess.backgroundColor
            ),
            bottomBar = {
                Actions(
                    state = state,
                    onEvent = onEvent,
                    containerStyle = style.actionsContainer,
                    dialogStyle = style.dialog
                )
            }
        ) { scaffoldPadding ->
            AnimatedContent(
                targetState = state,
                contentKey = { it::class.java },
                transitionSpec = {
                    fadeIn(
                        animationSpec = tween(
                            durationMillis = ShortAnimationDurationMillis,
                            easing = LinearEasing
                        )
                    ) togetherWith fadeOut(
                        animationSpec = tween(
                            durationMillis = ShortAnimationDurationMillis,
                            easing = LinearEasing
                        )
                    )
                }
            ) { state ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(scaffoldPadding)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = if (state is Loading) Arrangement.Center else Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (state) {
                        is Loading -> POCircularProgressIndicator.Large(color = style.progressIndicatorColor)
                        is Loaded -> Content(
                            state = state,
                            onEvent = onEvent,
                            style = style,
                            isLightTheme = isLightTheme
                        )
                        is Success -> Success(
                            message = state.message,
                            style = style.paymentSuccess
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Content(
    state: Loaded,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style,
    isLightTheme: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.extraLarge)
    ) {
        POMessageBox(
            text = state.errorMessage,
            style = style.errorMessageBox,
            modifier = Modifier.padding(bottom = spacing.large),
            horizontalArrangement = Arrangement.spacedBy(RowComponentSpacing),
            enterAnimationDelayMillis = ShortAnimationDurationMillis
        )
        state.expressCheckout?.let {
            ExpressCheckout(
                state = it,
                onEvent = onEvent,
                style = style,
                isLightTheme = isLightTheme
            )
        }
        if (state.regularPayments.elements.isNotEmpty()) {
            RegularPayments(
                payments = state.regularPayments,
                onEvent = onEvent,
                style = style,
                isLightTheme = isLightTheme
            )
        }
    }
}

@Composable
private fun ExpressCheckout(
    state: ExpressCheckout,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style,
    isLightTheme: Boolean
) {
    ExpressCheckoutHeader(
        state = state.header,
        onEvent = onEvent,
        style = style.sectionHeader
    )
    ExpressPayments(
        payments = state.expressPayments,
        onEvent = onEvent,
        style = style,
        isLightTheme = isLightTheme
    )
}

@Composable
private fun ExpressCheckoutHeader(
    state: SectionHeader,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: SectionHeaderStyle,
) {
    Row(
        modifier = Modifier
            .padding(bottom = spacing.large)
            .fillMaxWidth()
            .requiredHeightIn(min = dimensions.buttonIconSizeSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        POText(
            text = state.title,
            modifier = Modifier.weight(1f, fill = false),
            color = style.title.color,
            style = style.title.textStyle
        )
        state.action?.let { action ->
            POButton(
                text = action.text,
                onClick = {
                    onEvent(
                        Action(
                            actionId = action.id,
                            paymentMethodId = null
                        )
                    )
                },
                modifier = Modifier
                    .padding(start = spacing.small)
                    .requiredSizeIn(
                        minWidth = dimensions.buttonIconSizeSmall,
                        minHeight = dimensions.buttonIconSizeSmall
                    ),
                style = style.trailingButton,
                icon = action.icon,
                iconSize = dimensions.iconSizeSmall
            )
        }
    }
}

@Composable
private fun ExpressPayments(
    payments: POImmutableList<ExpressPayment>,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style,
    isLightTheme: Boolean
) {
    Column(
        modifier = Modifier.padding(bottom = spacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        payments.elements.forEach { payment ->
            when (payment) {
                is ExpressPayment.GooglePay -> GooglePay(
                    payment = payment,
                    onEvent = onEvent,
                    style = style.googlePayButton
                )
                is ExpressPayment.Express -> ExpressPayment(
                    payment = payment,
                    onEvent = onEvent,
                    style = style.expressPaymentButton,
                    isLightTheme = isLightTheme
                )
            }
        }
    }
}

@Composable
private fun GooglePay(
    payment: ExpressPayment.GooglePay,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: GooglePayButton.Style
) {
    with(payment.submitAction) {
        PayButton(
            onClick = {
                if (enabled) {
                    onEvent(
                        Action(
                            actionId = id,
                            paymentMethodId = payment.id
                        )
                    )
                }
            },
            allowedPaymentMethods = payment.allowedPaymentMethods,
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(style.height),
            theme = style.theme,
            type = style.type,
            radius = style.borderRadius
        )
    }
}

@Composable
private fun ExpressPayment(
    payment: ExpressPayment.Express,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: POBrandButtonStyle?,
    isLightTheme: Boolean
) {
    with(payment.submitAction) {
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
                .requiredHeightIn(min = dimensions.interactiveComponentMinSize),
            style = style.toButtonStyle(
                brandColor = payment.brandColor,
                isLightTheme = isLightTheme
            ),
            enabled = enabled,
            loading = loading,
            leadingContent = {
                PaymentLogo(
                    logoResource = payment.logoResource,
                    fallbackBoxColor = Color.Transparent,
                    modifier = Modifier.padding(end = spacing.small),
                    isLightTheme = isLightTheme
                )
            }
        )
    }
}

@Composable
private fun RegularPayments(
    payments: POImmutableList<RegularPayment>,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style,
    isLightTheme: Boolean
) {
    val borderWidth = style.regularPayment.border.width
    val borderColor = style.regularPayment.border.color
    val containerShape = style.regularPayment.shape
    Column(
        modifier = Modifier
            .border(
                width = borderWidth,
                color = borderColor,
                shape = containerShape
            )
            .clip(shape = containerShape)
            .padding(borderWidth)
            .background(style.regularPayment.backgroundColor)
    ) {
        payments.elements.forEachIndexed { index, payment ->
            RegularPayment(
                payment = payment,
                onEvent = onEvent,
                style = style,
                isLightTheme = isLightTheme
            )
            RegularPaymentContent(
                payment = payment,
                onEvent = onEvent,
                style = style,
                isLightTheme = isLightTheme
            )
            if (index != payments.elements.lastIndex) {
                HorizontalDivider(
                    thickness = borderWidth,
                    color = borderColor
                )
            }
        }
    }
}

@Composable
private fun RegularPayment(
    payment: RegularPayment,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style,
    isLightTheme: Boolean
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
            fallbackBoxColor = style.regularPayment.title.color,
            isLightTheme = isLightTheme
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
                POCircularProgressIndicator.Medium(color = style.progressIndicatorColor)
            }
        }
        PORadioButton(
            selected = payment.state.selected,
            onClick = { onEvent(PaymentMethodSelected(id = payment.id)) },
            style = style.radioField.radioButtonStyle()
        )
    }
}

@Composable
private fun RegularPaymentContent(
    payment: RegularPayment,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style,
    isLightTheme: Boolean
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
                is Card -> CardTokenizationContent(
                    state = payment.content.state,
                    onEvent = {
                        it.map(paymentMethodId = payment.id)?.let { event ->
                            onEvent(event)
                        }
                    },
                    style = style.cardTokenizationStyle(),
                    withActionsContainer = false
                )
                is NativeAlternativePayment -> when (val state = payment.content.state) {
                    is NativeAlternativePaymentViewModelState.Loaded -> {
                        when (state.content.stage) {
                            is Stage.Pending,
                            is Stage.Completed -> LocalFocusManager.current.clearFocus(force = true)
                            else -> {}
                        }
                        NativeAlternativePaymentContent(
                            content = state.content,
                            onEvent = {
                                it.map(paymentMethodId = payment.id)?.let { event ->
                                    onEvent(event)
                                }
                            },
                            style = style.nativeAlternativePaymentStyle(),
                            isPrimaryActionEnabled = state.primaryAction?.let { it.enabled && !it.loading } ?: false,
                            isLightTheme = isLightTheme
                        )
                    }
                    else -> {}
                }
                is AlternativePayment -> AlternativePayment(
                    id = payment.id,
                    state = payment.content,
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
                            .padding(top = spacing.extraLarge)
                            .fillMaxWidth()
                            .requiredHeightIn(min = dimensions.interactiveComponentMinSize),
                        style = style.actionsContainer.primary,
                        enabled = enabled,
                        loading = loading,
                        icon = icon
                    )
                }
            }
        }
    }
}

@Composable
private fun AlternativePayment(
    id: String,
    state: AlternativePayment,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style
) {
    Column(
        modifier = Modifier.padding(top = spacing.small)
    ) {
        when (state.savePaymentMethodField) {
            is CheckboxField -> CheckboxField(
                id = id,
                state = state.savePaymentMethodField.state,
                onEvent = onEvent,
                style = style.checkbox
            )
        }
    }
}

@Composable
private fun CheckboxField(
    id: String,
    state: FieldState,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: POCheckbox.Style,
    modifier: Modifier = Modifier
) {
    POCheckbox(
        text = state.label ?: String(),
        checked = state.value.text.toBooleanStrictOrNull() ?: false,
        onCheckedChange = {
            onEvent(
                FieldValueChanged(
                    paymentMethodId = id,
                    fieldId = state.id,
                    value = FieldValue.Text(value = TextFieldValue(text = it.toString()))
                )
            )
        },
        modifier = modifier,
        style = style,
        enabled = state.enabled,
        isError = state.isError
    )
}

@Composable
private fun PaymentLogo(
    logoResource: POImageResource,
    fallbackBoxColor: Color,
    modifier: Modifier = Modifier,
    isLightTheme: Boolean
) {
    var showLogo by remember { mutableStateOf(true) }
    if (showLogo) {
        val logoUrl = with(logoResource) {
            if (isLightTheme) {
                lightUrl.raster
            } else {
                darkUrl?.raster ?: lightUrl.raster
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
private fun Actions(
    state: DynamicCheckoutViewModelState,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    containerStyle: POActionsContainer.Style,
    dialogStyle: PODialog.Style
) {
    val actions = mutableListOf<POActionState>()
    val cancelAction: POActionState? = when (state) {
        is Loading -> state.cancelAction
        is Loaded -> state.cancelAction
        else -> null
    }
    cancelAction?.let { actions.add(it) }
    POActionsContainer(
        actions = POImmutableList(actions),
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
        confirmationDialogStyle = dialogStyle,
        animationDurationMillis = ShortAnimationDurationMillis
    )
}

@Composable
private fun Success(
    message: String,
    style: PaymentSuccessStyle
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = spacing.extraLarge * 2),
        verticalArrangement = Arrangement.spacedBy(spacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        POText(
            text = message,
            color = style.message.color,
            style = style.message.textStyle,
            textAlign = TextAlign.Center
        )
        Image(
            painter = painterResource(id = style.successImageResId),
            contentDescription = null,
            modifier = Modifier.requiredSize(
                width = SuccessImageWidth,
                height = SuccessImageHeight
            ),
            alignment = Alignment.Center,
            contentScale = ContentScale.Fit
        )
    }
}

private fun CardTokenizationEvent.map(
    paymentMethodId: String
): DynamicCheckoutEvent? = when (this) {
    is CardTokenizationEvent.FieldValueChanged -> FieldValueChanged(
        paymentMethodId = paymentMethodId,
        fieldId = id,
        value = FieldValue.Text(value = value)
    )
    is CardTokenizationEvent.FieldFocusChanged -> FieldFocusChanged(
        paymentMethodId = paymentMethodId,
        fieldId = id,
        isFocused = isFocused
    )
    is CardTokenizationEvent.Action -> Action(
        actionId = id,
        paymentMethodId = paymentMethodId
    )
    is CardTokenizationEvent.Dismiss -> Dismiss(failure = failure)
    is CardTokenizationEvent.CardScannerResult -> null // Ignore, handled by dynamic checkout events.
}

private fun NativeAlternativePaymentEvent.map(
    paymentMethodId: String
): DynamicCheckoutEvent? = when (this) {
    is NativeAlternativePaymentEvent.FieldValueChanged -> FieldValueChanged(
        paymentMethodId = paymentMethodId,
        fieldId = id,
        value = value
    )
    is NativeAlternativePaymentEvent.FieldFocusChanged -> FieldFocusChanged(
        paymentMethodId = paymentMethodId,
        fieldId = id,
        isFocused = isFocused
    )
    is NativeAlternativePaymentEvent.Action -> Action(
        actionId = id,
        paymentMethodId = paymentMethodId
    )
    is NativeAlternativePaymentEvent.DialogAction -> DialogAction(
        actionId = id,
        paymentMethodId = paymentMethodId,
        isConfirmed = isConfirmed
    )
    is NativeAlternativePaymentEvent.ActionConfirmationRequested -> ActionConfirmationRequested(id = id)
    is NativeAlternativePaymentEvent.Dismiss -> Dismiss(failure = failure)
    is NativeAlternativePaymentEvent.PermissionRequestResult,
    is NativeAlternativePaymentEvent.RedirectResult -> null // Ignore, handled by dynamic checkout events.
}

internal object DynamicCheckoutScreen {

    @Immutable
    data class Style(
        val sectionHeader: SectionHeaderStyle,
        val googlePayButton: GooglePayButton.Style,
        val expressPaymentButton: POBrandButtonStyle?,
        val regularPayment: RegularPaymentStyle,
        val label: POText.Style,
        val field: POField.Style,
        val codeField: POField.Style,
        val radioField: PORadioField.Style,
        val checkbox: POCheckbox.Style,
        val dropdownMenu: PODropdownField.MenuStyle,
        val bodyText: AndroidTextView.Style,
        val errorText: POText.Style,
        val errorMessageBox: POMessageBox.Style,
        val message: POText.Style,
        val dialog: PODialog.Style,
        val stepper: POStepper.Style,
        val scanCardButton: POButton.Style,
        val actionsContainer: POActionsContainer.Style,
        val backgroundColor: Color,
        val progressIndicatorColor: Color,
        val paymentSuccess: PaymentSuccessStyle
    )

    @Immutable
    data class SectionHeaderStyle(
        val title: POText.Style,
        val trailingButton: POButton.Style
    )

    @Immutable
    data class RegularPaymentStyle(
        val title: POText.Style,
        val description: POTextWithIcon.Style,
        val shape: Shape,
        val border: POBorderStroke,
        val backgroundColor: Color
    )

    @Immutable
    data class PaymentSuccessStyle(
        val message: POText.Style,
        @DrawableRes val successImageResId: Int,
        val backgroundColor: Color
    )

    @Composable
    fun style(
        custom: PODynamicCheckoutConfiguration.Style?,
        isLightTheme: Boolean
    ) = Style(
        sectionHeader = custom?.sectionHeader?.custom() ?: defaultSectionHeader,
        googlePayButton = custom?.googlePayButton?.let {
            GooglePayButton.custom(style = it, isLightTheme)
        } ?: GooglePayButton.default(isLightTheme),
        expressPaymentButton = custom?.expressPaymentButton,
        regularPayment = custom?.regularPayment?.custom() ?: defaultRegularPayment,
        label = custom?.label?.let {
            POText.custom(style = it)
        } ?: POText.Style(
            color = colors.text.primary,
            textStyle = typography.s14(FontWeight.Medium)
        ),
        field = custom?.field?.let {
            POField.custom(style = it)
        } ?: POField.default2,
        codeField = custom?.codeField?.let {
            POField.custom(style = it)
        } ?: POCodeField.default,
        radioField = custom?.radioField?.let {
            PORadioField.custom(style = it)
        } ?: PORadioField.default,
        checkbox = custom?.checkbox?.let {
            POCheckbox.custom(style = it)
        } ?: POCheckbox.default2,
        dropdownMenu = custom?.dropdownMenu?.let {
            PODropdownField.custom(style = it)
        } ?: PODropdownField.defaultMenu2,
        bodyText = custom?.bodyText?.let { style ->
            val controlsTintColor = custom.controlsTintColorResId?.let { colorResource(id = it) }
            AndroidTextView.custom(
                style = style,
                controlsTintColor = controlsTintColor ?: colors.text.primary
            )
        } ?: AndroidTextView.default,
        errorText = custom?.errorText?.let {
            POText.custom(style = it)
        } ?: POText.Style(
            color = colors.text.error,
            textStyle = typography.s14()
        ),
        errorMessageBox = custom?.errorMessageBox?.let {
            POMessageBox.custom(style = it)
        } ?: POMessageBox.error2,
        message = custom?.message?.let {
            POText.custom(style = it)
        } ?: POText.Style(
            color = colors.text.secondary,
            textStyle = typography.s15()
        ),
        dialog = custom?.dialog?.let {
            PODialog.custom(style = it)
        } ?: PODialog.default,
        stepper = custom?.stepper?.let {
            POStepper.custom(style = it)
        } ?: POStepper.default,
        scanCardButton = custom?.scanCardButton?.let {
            POButton.custom(style = it)
        } ?: CardTokenizationScreen.defaultScanButton,
        actionsContainer = custom?.actionsContainer?.let {
            POActionsContainer.custom(style = it)
        } ?: POActionsContainer.default2,
        backgroundColor = custom?.backgroundColorResId?.let {
            colorResource(id = it)
        } ?: colors.surface.default,
        progressIndicatorColor = custom?.progressIndicatorColorResId?.let {
            colorResource(id = it)
        } ?: colors.button.primaryBackgroundDefault,
        paymentSuccess = custom?.paymentSuccess?.custom() ?: defaultPaymentSuccess
    )

    private val defaultSectionHeader: SectionHeaderStyle
        @Composable get() = SectionHeaderStyle(
            title = POText.subheading,
            trailingButton = POButton.ghostEqualPadding
        )

    @Composable
    private fun PODynamicCheckoutConfiguration.SectionHeaderStyle.custom() =
        SectionHeaderStyle(
            title = POText.custom(style = title),
            trailingButton = POButton.custom(style = trailingButton)
        )

    private val defaultRegularPayment: RegularPaymentStyle
        @Composable get() {
            val description = POText.Style(
                color = colors.text.muted,
                textStyle = typography.body2
            )
            return RegularPaymentStyle(
                title = POText.body1,
                description = POTextWithIcon.Style(
                    text = description,
                    iconResId = R.drawable.po_icon_info,
                    iconColorFilter = ColorFilter.tint(color = description.color)
                ),
                shape = shapes.roundedCornersSmall,
                border = POBorderStroke(width = 1.dp, color = colors.border.border4),
                backgroundColor = colors.surface.default
            )
        }

    @Composable
    private fun PODynamicCheckoutConfiguration.RegularPaymentStyle.custom(): RegularPaymentStyle {
        val description = POText.custom(style = description)
        return RegularPaymentStyle(
            title = POText.custom(style = title),
            description = POTextWithIcon.Style(
                text = description,
                iconResId = descriptionIconResId ?: defaultRegularPayment.description.iconResId,
                iconColorFilter = if (descriptionIconResId != null) null else
                    ColorFilter.tint(color = description.color)
            ),
            shape = border?.let {
                RoundedCornerShape(size = it.radiusDp.dp)
            } ?: defaultRegularPayment.shape,
            border = border?.let {
                POBorderStroke(
                    width = it.widthDp.dp,
                    color = colorResource(id = it.colorResId)
                )
            } ?: defaultRegularPayment.border,
            backgroundColor = backgroundColorResId?.let {
                colorResource(id = it)
            } ?: defaultRegularPayment.backgroundColor
        )
    }

    private val defaultPaymentSuccess: PaymentSuccessStyle
        @Composable get() = PaymentSuccessStyle(
            message = POText.Style(
                color = colors.text.success,
                textStyle = typography.body1
            ),
            successImageResId = com.processout.sdk.ui.R.drawable.po_success_image,
            backgroundColor = colors.surface.success
        )

    @Composable
    private fun PODynamicCheckoutConfiguration.PaymentSuccessStyle.custom() =
        PaymentSuccessStyle(
            message = POText.custom(style = message),
            successImageResId = successImageResId ?: defaultPaymentSuccess.successImageResId,
            backgroundColor = backgroundColorResId?.let {
                colorResource(id = it)
            } ?: defaultPaymentSuccess.backgroundColor
        )

    @Composable
    fun POBrandButtonStyle?.toButtonStyle(
        brandColor: POColor,
        isLightTheme: Boolean
    ): POButton.Style {
        val resolvedBrandColor = this?.normal?.backgroundColorResId?.let { colorResource(id = it) }
            ?: (if (isLightTheme) brandColor.lightColor else brandColor.darkColor)
            ?: colors.button.primaryBackgroundDefault
        val isLightBrandColor = resolvedBrandColor.isLight()
        val textColor = this?.normal?.text?.color?.resolve(isLightTheme = isLightBrandColor)
            ?: if (isLightBrandColor) POLightColorPalette.text.primary else PODarkColorPalette.text.primary
        val borderColor = this?.normal?.border?.color?.resolve(isLightTheme = isLightBrandColor)
            ?: Color.Transparent
        val stateStyle = this?.normal?.toStateStyle(
            textColor = textColor,
            borderColor = borderColor,
            backgroundColor = resolvedBrandColor
        ) ?: with(POButton.primary.normal) {
            copy(
                text = text.copy(color = textColor),
                border = border.copy(color = borderColor),
                backgroundColor = resolvedBrandColor
            )
        }
        return POButton.Style(
            normal = stateStyle,
            disabled = stateStyle,
            highlighted = HighlightedStyle(
                textColor = textColor,
                borderColor = borderColor,
                backgroundColor = this?.highlighted?.backgroundColorResId?.let { colorResource(id = it) }
                    ?: if (isLightBrandColor)
                        resolvedBrandColor.darker(factor = 0.08f)
                    else resolvedBrandColor.lighter(factor = 0.15f)
            ),
            progressIndicatorColor = textColor
        )
    }

    @Composable
    private fun com.processout.sdk.ui.core.style.POColor.resolve(
        isLightTheme: Boolean
    ): Color = if (isLightTheme)
        colorResource(id = lightColorResId)
    else colorResource(id = darkColorResId)

    @Composable
    private fun POBrandButtonStateStyle.toStateStyle(
        textColor: Color,
        borderColor: Color,
        backgroundColor: Color
    ) = StateStyle(
        text = POText.Style(
            color = textColor,
            textStyle = POText.custom(type = text.type)
        ),
        shape = RoundedCornerShape(size = border.radiusDp.dp),
        border = POBorderStroke(width = border.widthDp.dp, color = borderColor),
        backgroundColor = backgroundColor,
        elevation = elevationDp.dp,
        paddingHorizontal = paddingHorizontalDp.dp,
        paddingVertical = paddingVerticalDp.dp
    )

    @Composable
    fun animatedBackgroundColor(
        state: DynamicCheckoutViewModelState,
        normalColor: Color,
        successColor: Color
    ): Color = animateColorAsState(
        targetValue = when (state) {
            is Success -> successColor
            else -> normalColor
        },
        animationSpec = tween(
            durationMillis = CrossfadeAnimationDurationMillis,
            easing = LinearEasing
        )
    ).value

    fun Style.cardTokenizationStyle() = CardTokenizationScreen.Style(
        title = regularPayment.title,
        sectionTitle = label,
        field = field,
        radioField = radioField,
        dropdownMenu = dropdownMenu,
        checkbox = checkbox,
        dialog = dialog,
        errorMessage = errorText,
        scanButton = scanCardButton,
        actionsContainer = actionsContainer,
        backgroundColor = Color.Unspecified,
        dividerColor = Color.Unspecified,
        dragHandleColor = Color.Unspecified
    )

    @Composable
    fun Style.nativeAlternativePaymentStyle() = NativeAlternativePaymentScreen.Style(
        title = regularPayment.title,
        bodyText = bodyText,
        message = message,
        labeledContent = null,
        groupedContent = null,
        field = field,
        codeField = codeField,
        radioField = radioField,
        dropdownMenu = dropdownMenu,
        checkbox = checkbox,
        dialog = dialog,
        stepper = stepper,
        success = NativeAlternativePaymentScreen.defaultSuccess,
        errorMessageBox = errorMessageBox,
        actionsContainer = actionsContainer,
        backgroundColor = Color.Unspecified,
        progressIndicatorColor = Color.Unspecified,
        dividerColor = Color.Unspecified,
        dragHandleColor = Color.Unspecified
    )

    val ShortAnimationDurationMillis = 300
    val LongAnimationDurationMillis = 600
    val CrossfadeAnimationDurationMillis = 400

    val RowComponentSpacing = 10.dp

    val PaymentLogoSize = 24.dp

    val SuccessImageWidth = 220.dp
    val SuccessImageHeight = 280.dp
}
