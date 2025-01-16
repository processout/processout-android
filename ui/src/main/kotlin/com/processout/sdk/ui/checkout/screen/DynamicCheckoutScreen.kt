@file:Suppress("MayBeConstant", "MemberVisibilityCanBePrivate", "AnimateAsStateLabel", "CrossfadeLabel")

package com.processout.sdk.ui.checkout.screen

import android.view.Gravity
import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.pay.button.PayButton
import com.processout.sdk.api.model.response.POColor
import com.processout.sdk.api.model.response.POImageResource
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent.*
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.*
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.Field.CheckboxField
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.RegularPayment.Content.*
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.CrossfadeAnimationDurationMillis
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.LongAnimationDurationMillis
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.PaymentLogoSize
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.PaymentSuccessStyle
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.RowComponentSpacing
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.ShortAnimationDurationMillis
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.SuccessImageHeight
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.SuccessImageWidth
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.animatedBackgroundColor
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.toButtonStyle
import com.processout.sdk.ui.core.R
import com.processout.sdk.ui.core.component.*
import com.processout.sdk.ui.core.component.POButton.HighlightedStyle
import com.processout.sdk.ui.core.component.POButton.StateStyle
import com.processout.sdk.ui.core.component.POText.Style
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.checkbox.POCheckbox
import com.processout.sdk.ui.core.component.field.code.POCodeField
import com.processout.sdk.ui.core.component.field.dropdown.PODropdownField
import com.processout.sdk.ui.core.component.field.radio.PORadioButton
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup.toRadioButtonStyle
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
import com.processout.sdk.ui.shared.component.DynamicFooter
import com.processout.sdk.ui.shared.component.GooglePayButton
import com.processout.sdk.ui.shared.component.TextAndroidView
import com.processout.sdk.ui.shared.extension.*
import com.processout.sdk.ui.shared.state.FieldState

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
                DynamicFooter {
                    Actions(
                        state = state,
                        onEvent = onEvent,
                        containerStyle = style.actionsContainer,
                        dialogStyle = style.dialog
                    )
                }
            }
        ) { scaffoldPadding ->
            Crossfade(
                targetState = when (state) {
                    is Started -> state.successMessage != null
                    else -> false
                },
                animationSpec = tween(
                    durationMillis = CrossfadeAnimationDurationMillis,
                    easing = LinearEasing
                )
            ) { isSuccess ->
                if (isSuccess && state is Started) {
                    Success(
                        message = state.successMessage ?: String(),
                        style = style.paymentSuccess
                    )
                } else {
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
                                style = style,
                                isLightTheme = isLightTheme
                            )
                        }
                    }
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
    style: DynamicCheckoutScreen.Style,
    isLightTheme: Boolean
) {
    AnimatedVisibility {
        Column(
            modifier = Modifier.padding(spacing.extraLarge)
        ) {
            POMessageBox(
                text = state.errorMessage,
                style = style.messageBox,
                modifier = Modifier.padding(bottom = spacing.extraLarge),
                horizontalArrangement = Arrangement.spacedBy(RowComponentSpacing),
                enterAnimationDelayMillis = ShortAnimationDurationMillis
            )
            if (state.expressPayments.elements.isNotEmpty()) {
                ExpressPayments(
                    payments = state.expressPayments,
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
                style = style,
                isLightTheme = isLightTheme
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
        text = state.title ?: String(),
        checked = state.value.text.toBooleanStrictOrNull() ?: false,
        onCheckedChange = {
            onEvent(
                FieldValueChanged(
                    paymentMethodId = id,
                    fieldId = state.id,
                    value = TextFieldValue(text = it.toString())
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
        is Starting -> state.cancelAction
        is Started -> state.cancelAction
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
        animationDurationMillis = CrossfadeAnimationDurationMillis
    )
}

@Composable
private fun Success(
    message: String,
    style: PaymentSuccessStyle
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
        val googlePayButton: GooglePayButton.Style,
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
        val progressIndicatorColor: Color,
        val paymentSuccess: PaymentSuccessStyle
    )

    @Immutable
    data class RegularPaymentStyle(
        val title: POText.Style,
        val shape: Shape,
        val border: POBorderStroke,
        val description: POTextWithIcon.Style
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
        googlePayButton = custom?.googlePayButton?.let {
            GooglePayButton.custom(style = it, isLightTheme)
        } ?: GooglePayButton.default(isLightTheme),
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
        } ?: colors.button.primaryBackgroundDefault,
        paymentSuccess = custom?.paymentSuccess?.custom() ?: defaultPaymentSuccess
    )

    private val defaultRegularPayment: RegularPaymentStyle
        @Composable get() {
            val description = Style(
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
                iconResId = descriptionIconResId ?: defaultRegularPayment.description.iconResId,
                iconColorFilter = if (descriptionIconResId != null) null else
                    ColorFilter.tint(color = description.color)
            )
        )
    }

    private val defaultPaymentSuccess: PaymentSuccessStyle
        @Composable get() = PaymentSuccessStyle(
            message = Style(
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
            backgroundColor = backgroundColorResId?.let { colorResource(id = it) }
                ?: defaultPaymentSuccess.backgroundColor
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
        text = Style(
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
            is Started -> if (state.successMessage != null) successColor else normalColor
            else -> normalColor
        },
        animationSpec = tween(
            durationMillis = CrossfadeAnimationDurationMillis,
            easing = LinearEasing
        )
    ).value

    val ShortAnimationDurationMillis = 300
    val LongAnimationDurationMillis = 600
    val CrossfadeAnimationDurationMillis = 400

    val RowComponentSpacing = 10.dp

    val PaymentLogoSize = 24.dp
    val CaptureLogoHeight = 34.dp

    val CaptureImageWidth = 110.dp
    val CaptureImageHeight = 140.dp

    val SuccessImageWidth = 220.dp
    val SuccessImageHeight = 280.dp

    private val ShortMessageMaxLength = 150

    fun isMessageShort(text: String) = text.length <= ShortMessageMaxLength

    fun messageGravity(text: String): Int =
        if (isMessageShort(text))
            Gravity.CENTER_HORIZONTAL
        else Gravity.START
}
