@file:Suppress("MayBeConstant")

package com.processout.sdk.ui.checkout.screen

import android.view.Gravity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent
import com.processout.sdk.ui.checkout.DynamicCheckoutExtendedEvent.*
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.*
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.RegularPaymentLogoSize
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.RowComponentSpacing
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.ShortAnimationDurationMillis
import com.processout.sdk.ui.core.R
import com.processout.sdk.ui.core.component.*
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.code.POCodeField
import com.processout.sdk.ui.core.component.field.dropdown.PODropdownField
import com.processout.sdk.ui.core.component.field.radio.PORadioButton
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup.toRadioButtonStyle
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
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
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.systemBars))
        Scaffold(
            modifier = Modifier.clip(shape = shapes.topRoundedCornersLarge),
            containerColor = style.backgroundColor,
            topBar = { Header() },
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
            RegularPayments(
                payments = state.regularPayments,
                onEvent = onEvent,
                style = style
            )
        }
    }
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
        var showLogo by remember { mutableStateOf(true) }
        if (showLogo) {
            val logoUrl = with(payment.state.logoResource) {
                if (isSystemInDarkTheme()) {
                    darkUrl?.raster ?: lightUrl.raster
                } else {
                    lightUrl.raster
                }
            }
            AsyncImage(
                model = logoUrl,
                contentDescription = null,
                modifier = Modifier.requiredSize(RegularPaymentLogoSize),
                onError = { showLogo = false }
            )
        } else {
            Box(
                modifier = Modifier
                    .requiredSize(RegularPaymentLogoSize)
                    .background(
                        color = style.regularPayment.title.color,
                        shape = shapes.roundedCornersSmall
                    )
            )
        }
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
                onClick = { onEvent(Action(id = it)) },
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
        val regularPayment: RegularPaymentStyle,
        val label: POText.Style,
        val field: POField.Style,
        val codeField: POField.Style,
        val radioGroup: PORadioGroup.Style,
        val dropdownMenu: PODropdownField.MenuStyle,
        val bodyText: TextAndroidView.Style,
        val errorText: POText.Style,
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

    val RegularPaymentLogoSize = 24.dp

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
