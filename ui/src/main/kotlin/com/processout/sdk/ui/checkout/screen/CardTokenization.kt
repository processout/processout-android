package com.processout.sdk.ui.checkout.screen

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState.Item
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState.Section
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState.SectionId.CARD_INFORMATION
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState.SectionId.FUTURE_PAYMENTS
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState.SectionId.PREFERRED_SCHEME
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent.*
import com.processout.sdk.ui.core.component.*
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.checkbox.POCheckbox
import com.processout.sdk.ui.core.component.field.dropdown.PODropdownField
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup
import com.processout.sdk.ui.core.component.field.text.POTextField
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.shared.component.rememberLifecycleEvent
import com.processout.sdk.ui.shared.state.FieldState

@Composable
internal fun CardTokenization(
    id: String,
    state: CardTokenizationViewModelState,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: DynamicCheckoutScreen.Style
) {
    if (state.focusedFieldId == null) {
        LocalFocusManager.current.clearFocus(force = true)
    }
    state.cardScannerAction?.let { action ->
        POButton(
            state = action,
            onClick = {
                onEvent(
                    Action(
                        actionId = it,
                        paymentMethodId = id
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeightIn(min = dimensions.buttonIconSizeSmall)
                .padding(bottom = spacing.small),
            style = style.scanCardButton,
            iconSize = dimensions.iconSizeSmall
        )
    }
    val lifecycleEvent = rememberLifecycleEvent()
    state.sections.elements.forEach { section ->
        Section(
            id = id,
            section = section,
            onEvent = onEvent,
            lifecycleEvent = lifecycleEvent,
            focusedFieldId = state.focusedFieldId,
            isPrimaryActionEnabled = state.primaryAction.enabled && !state.primaryAction.loading,
            style = style
        )
    }
}

@Composable
private fun Section(
    id: String,
    section: Section,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    lifecycleEvent: Lifecycle.Event,
    focusedFieldId: String?,
    isPrimaryActionEnabled: Boolean,
    style: DynamicCheckoutScreen.Style
) {
    val paddingTop = when (section.id) {
        CARD_INFORMATION -> 0.dp
        PREFERRED_SCHEME -> if (section.title == null) spacing.small else spacing.extraLarge
        FUTURE_PAYMENTS -> spacing.small
        else -> spacing.extraLarge
    }
    Column(
        modifier = Modifier.padding(top = paddingTop),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        section.title?.let {
            with(style.label) {
                POText(
                    text = it,
                    color = color,
                    style = textStyle
                )
            }
        }
        section.items?.elements?.forEach { item ->
            Item(
                id = id,
                item = item,
                onEvent = onEvent,
                lifecycleEvent = lifecycleEvent,
                focusedFieldId = focusedFieldId,
                isPrimaryActionEnabled = isPrimaryActionEnabled,
                style = style,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    POExpandableText(
        text = section.errorMessage,
        style = style.errorText,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = spacing.small)
    )
    var currentSubsection by remember { mutableStateOf(Section(id = String())) }
    currentSubsection = section.subsection ?: currentSubsection
    AnimatedVisibility(
        visible = section.subsection != null,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Section(
            id = id,
            section = currentSubsection,
            onEvent = onEvent,
            lifecycleEvent = lifecycleEvent,
            focusedFieldId = focusedFieldId,
            isPrimaryActionEnabled = isPrimaryActionEnabled,
            style = style
        )
    }
}

@Composable
private fun Item(
    id: String,
    item: Item,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    lifecycleEvent: Lifecycle.Event,
    focusedFieldId: String?,
    isPrimaryActionEnabled: Boolean,
    style: DynamicCheckoutScreen.Style,
    modifier: Modifier = Modifier
) {
    when (item) {
        is Item.TextField -> TextField(
            id = id,
            state = item.state,
            onEvent = onEvent,
            lifecycleEvent = lifecycleEvent,
            focusedFieldId = focusedFieldId,
            isPrimaryActionEnabled = isPrimaryActionEnabled,
            style = style.field,
            modifier = modifier
        )
        is Item.RadioField -> RadioField(
            id = id,
            state = item.state,
            onEvent = onEvent,
            style = style.radioGroup,
            modifier = modifier
        )
        is Item.DropdownField -> DropdownField(
            id = id,
            state = item.state,
            onEvent = onEvent,
            fieldStyle = style.field,
            menuStyle = style.dropdownMenu,
            modifier = modifier
        )
        is Item.CheckboxField -> CheckboxField(
            id = id,
            state = item.state,
            onEvent = onEvent,
            style = style.checkbox,
            modifier = modifier
        )
        is Item.Group -> Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            item.items.elements.forEach { groupItem ->
                Item(
                    id = id,
                    item = groupItem,
                    onEvent = onEvent,
                    lifecycleEvent = lifecycleEvent,
                    focusedFieldId = focusedFieldId,
                    isPrimaryActionEnabled = isPrimaryActionEnabled,
                    style = style,
                    modifier = Modifier.weight(1f)
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
    style: POField.Style,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    POTextField(
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
        style = style,
        enabled = state.enabled,
        isError = state.isError,
        forceTextDirectionLtr = state.forceTextDirectionLtr,
        placeholderText = state.placeholder,
        trailingIcon = { state.iconResId?.let { AnimatedFieldIcon(id = it) } },
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
private fun RadioField(
    id: String,
    state: FieldState,
    onEvent: (DynamicCheckoutEvent) -> Unit,
    style: PORadioGroup.Style,
    modifier: Modifier = Modifier
) {
    PORadioGroup(
        value = state.value.text,
        onValueChange = {
            onEvent(
                FieldValueChanged(
                    paymentMethodId = id,
                    fieldId = state.id,
                    value = TextFieldValue(text = it)
                )
            )
        },
        availableValues = state.availableValues ?: POImmutableList(emptyList()),
        modifier = modifier,
        style = style
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
    PODropdownField(
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
private fun AnimatedFieldIcon(@DrawableRes id: Int) {
    POAnimatedImage(
        id = id,
        modifier = Modifier
            .requiredHeight(dimensions.formComponentMinHeight)
            .padding(POField.contentPadding),
        contentScale = ContentScale.FillHeight
    )
}
