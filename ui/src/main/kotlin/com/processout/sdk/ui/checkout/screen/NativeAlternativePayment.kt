package com.processout.sdk.ui.checkout.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.Lifecycle
import coil.compose.AsyncImage
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent.*
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.CaptureImageHeight
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.CaptureImageWidth
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.CaptureLogoHeight
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.LongAnimationDurationMillis
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.ShortAnimationDurationMillis
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.isMessageShort
import com.processout.sdk.ui.checkout.screen.DynamicCheckoutScreen.messageGravity
import com.processout.sdk.ui.core.component.POButton
import com.processout.sdk.ui.core.component.POCircularProgressIndicator
import com.processout.sdk.ui.core.component.PORequestFocus
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.POFieldLabels
import com.processout.sdk.ui.core.component.field.code.POCodeField
import com.processout.sdk.ui.core.component.field.code.POLabeledCodeField
import com.processout.sdk.ui.core.component.field.dropdown.PODropdownField
import com.processout.sdk.ui.core.component.field.dropdown.POLabeledDropdownField
import com.processout.sdk.ui.core.component.field.radio.POLabeledRadioField
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup
import com.processout.sdk.ui.core.component.field.text.POLabeledTextField
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.Capture
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.Field.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.UserInput
import com.processout.sdk.ui.shared.component.TextAndroidView
import com.processout.sdk.ui.shared.component.rememberLifecycleEvent
import com.processout.sdk.ui.shared.extension.conditional
import com.processout.sdk.ui.shared.state.FieldState

@Composable
internal fun NativeAlternativePayment(
    id: String,
    state: NativeAlternativePaymentViewModelState,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style
) {
    when (state) {
        is UserInput -> UserInput(id, state, onEvent, style)
        is Capture -> if (!state.isCaptured) {
            Capture(id, state, onEvent, style)
        }
        else -> {}
    }
}

@Composable
private fun UserInput(
    id: String,
    state: UserInput,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(spacing.extraLarge)
    ) {
        val lifecycleEvent = rememberLifecycleEvent()
        val labelsStyle = remember {
            POFieldLabels.Style(
                title = style.label,
                description = style.errorText
            )
        }
        val isPrimaryActionEnabled = with(state.primaryAction) { enabled && !loading }
        state.fields.elements.forEach { field ->
            when (field) {
                is TextField -> TextField(
                    id = id,
                    state = field.state,
                    onEvent = onEvent,
                    lifecycleEvent = lifecycleEvent,
                    focusedFieldId = state.focusedFieldId,
                    isPrimaryActionEnabled = isPrimaryActionEnabled,
                    fieldStyle = style.field,
                    labelsStyle = labelsStyle,
                    modifier = Modifier.fillMaxWidth()
                )
                is CodeField -> CodeField(
                    id = id,
                    state = field.state,
                    onEvent = onEvent,
                    lifecycleEvent = lifecycleEvent,
                    focusedFieldId = state.focusedFieldId,
                    isPrimaryActionEnabled = isPrimaryActionEnabled,
                    fieldStyle = style.codeField,
                    labelsStyle = labelsStyle,
                    horizontalAlignment = Alignment.Start
                )
                is RadioField -> RadioField(
                    id = id,
                    state = field.state,
                    onEvent = onEvent,
                    radioGroupStyle = style.radioGroup,
                    labelsStyle = labelsStyle
                )
                is DropdownField -> DropdownField(
                    id = id,
                    state = field.state,
                    onEvent = onEvent,
                    fieldStyle = style.field,
                    menuStyle = style.dropdownMenu,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun TextField(
    id: String,
    state: FieldState,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    lifecycleEvent: Lifecycle.Event,
    focusedFieldId: String?,
    isPrimaryActionEnabled: Boolean,
    fieldStyle: POField.Style,
    labelsStyle: POFieldLabels.Style,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    POLabeledTextField(
        value = state.value,
        onValueChange = {
            onEvent(
                FieldValueChanged(
                    paymentMethodId = id,
                    fieldId = state.id,
                    value = state.inputFilter?.filter(it) ?: it
                )
            )
        },
        title = state.title ?: String(),
        description = state.description,
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                onEvent(
                    FieldFocusChanged(
                        paymentMethodId = id,
                        fieldId = state.id,
                        isFocused = it.isFocused
                    )
                )
            },
        fieldStyle = fieldStyle,
        labelsStyle = labelsStyle,
        enabled = state.enabled,
        isError = state.isError,
        forceTextDirectionLtr = state.forceTextDirectionLtr,
        placeholderText = state.placeholder,
        visualTransformation = state.visualTransformation,
        keyboardOptions = state.keyboardOptions,
        keyboardActions = POField.keyboardActions(
            imeAction = state.keyboardOptions.imeAction,
            actionId = state.keyboardActionId,
            enabled = isPrimaryActionEnabled,
            onClick = {
                onEvent(
                    Action(
                        actionId = it,
                        paymentMethodId = id
                    )
                )
            }
        )
    )
    if (state.id == focusedFieldId && lifecycleEvent == Lifecycle.Event.ON_RESUME) {
        PORequestFocus(focusRequester, lifecycleEvent)
    }
}

@Composable
private fun CodeField(
    id: String,
    state: FieldState,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    lifecycleEvent: Lifecycle.Event,
    focusedFieldId: String?,
    isPrimaryActionEnabled: Boolean,
    fieldStyle: POField.Style,
    labelsStyle: POFieldLabels.Style,
    horizontalAlignment: Alignment.Horizontal,
    modifier: Modifier = Modifier
) {
    POLabeledCodeField(
        value = state.value,
        onValueChange = {
            onEvent(
                FieldValueChanged(
                    paymentMethodId = id,
                    fieldId = state.id,
                    value = it
                )
            )
        },
        title = state.title ?: String(),
        description = state.description,
        modifier = modifier
            .onFocusChanged {
                onEvent(
                    FieldFocusChanged(
                        paymentMethodId = id,
                        fieldId = state.id,
                        isFocused = it.isFocused
                    )
                )
            },
        fieldStyle = fieldStyle,
        labelsStyle = labelsStyle,
        length = state.length ?: POCodeField.LengthMax,
        horizontalAlignment = horizontalAlignment,
        enabled = state.enabled,
        isError = state.isError,
        isFocused = state.id == focusedFieldId,
        lifecycleEvent = lifecycleEvent,
        keyboardOptions = state.keyboardOptions,
        keyboardActions = POField.keyboardActions(
            imeAction = state.keyboardOptions.imeAction,
            actionId = state.keyboardActionId,
            enabled = isPrimaryActionEnabled,
            onClick = {
                onEvent(
                    Action(
                        actionId = it,
                        paymentMethodId = id
                    )
                )
            }
        )
    )
}

@Composable
private fun RadioField(
    id: String,
    state: FieldState,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    radioGroupStyle: PORadioGroup.Style,
    labelsStyle: POFieldLabels.Style,
    modifier: Modifier = Modifier
) {
    POLabeledRadioField(
        value = state.value,
        onValueChange = {
            onEvent(
                FieldValueChanged(
                    paymentMethodId = id,
                    fieldId = state.id,
                    value = it
                )
            )
        },
        availableValues = state.availableValues ?: POImmutableList(emptyList()),
        title = state.title ?: String(),
        description = state.description,
        modifier = modifier,
        radioGroupStyle = radioGroupStyle,
        labelsStyle = labelsStyle,
        isError = state.isError
    )
}

@Composable
private fun DropdownField(
    id: String,
    state: FieldState,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    fieldStyle: POField.Style,
    menuStyle: PODropdownField.MenuStyle,
    modifier: Modifier = Modifier
) {
    POLabeledDropdownField(
        value = state.value,
        onValueChange = {
            onEvent(
                FieldValueChanged(
                    paymentMethodId = id,
                    fieldId = state.id,
                    value = it
                )
            )
        },
        availableValues = state.availableValues ?: POImmutableList(emptyList()),
        title = state.title ?: String(),
        description = state.description,
        modifier = modifier
            .onFocusChanged {
                onEvent(
                    FieldFocusChanged(
                        paymentMethodId = id,
                        fieldId = state.id,
                        isFocused = it.isFocused
                    )
                )
            },
        fieldStyle = fieldStyle,
        menuStyle = menuStyle,
        isError = state.isError,
        placeholderText = state.placeholder
    )
}

@Composable
private fun Capture(
    id: String,
    state: Capture,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style
) {
    AnimatedVisibility(
        visibleState = remember {
            MutableTransitionState(initialState = false)
                .apply { targetState = true }
        },
        enter = fadeIn(animationSpec = tween(durationMillis = LongAnimationDurationMillis)),
        exit = fadeOut(animationSpec = tween(durationMillis = LongAnimationDurationMillis))
    ) {
        val withPaddingTop = isMessageShort(state.message) &&
                state.logoUrl == null && state.title == null &&
                !state.withProgressIndicator
        Column(
            modifier = Modifier.conditional(
                condition = withPaddingTop,
                modifier = { padding(top = spacing.extraLarge) }
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.extraLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CaptureHeader(state, style)
            if (state.withProgressIndicator) {
                AnimatedProgressIndicator(style.progressIndicatorColor)
            }
            TextAndroidView(
                text = state.message,
                style = style.bodyText,
                modifier = Modifier.fillMaxWidth(),
                gravity = messageGravity(state.message),
                selectable = true,
                linksClickable = true
            )
            var showImage by remember { mutableStateOf(true) }
            if (showImage) {
                AsyncImage(
                    model = state.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.requiredSize(
                        width = CaptureImageWidth,
                        height = CaptureImageHeight
                    ),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Fit,
                    onError = {
                        showImage = false
                    }
                )
            }
            state.primaryAction?.let { action ->
                POButton(
                    text = action.text,
                    onClick = {
                        onEvent(
                            Action(
                                actionId = action.id,
                                paymentMethodId = id
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    style = style.actionsContainer.primary
                )
            }
        }
    }
}

@Composable
private fun CaptureHeader(
    state: Capture,
    style: DynamicCheckoutScreen.Style
) {
    var showLogo by remember { mutableStateOf(true) }
    if (showLogo) {
        AsyncImage(
            model = state.logoUrl,
            contentDescription = null,
            modifier = Modifier.requiredHeight(CaptureLogoHeight),
            contentScale = ContentScale.FillHeight,
            onError = {
                showLogo = false
            }
        )
    } else if (state.title != null) {
        with(style.regularPayment.title) {
            POText(
                text = state.title,
                color = color,
                style = textStyle
            )
        }
    }
}

@Composable
private fun AnimatedProgressIndicator(
    progressIndicatorColor: Color
) {
    AnimatedVisibility(
        visibleState = remember {
            MutableTransitionState(initialState = false)
                .apply { targetState = true }
        },
        enter = expandVertically() + fadeIn(animationSpec = tween(durationMillis = ShortAnimationDurationMillis)),
        exit = shrinkVertically() + fadeOut(animationSpec = tween(durationMillis = ShortAnimationDurationMillis))
    ) {
        POCircularProgressIndicator.Large(color = progressIndicatorColor)
    }
}
