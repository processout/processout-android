package com.processout.sdk.ui.checkout.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.Lifecycle
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState.Item
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent.FieldFocusChanged
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent.FieldValueChanged
import com.processout.sdk.ui.core.component.POAnimatedImage
import com.processout.sdk.ui.core.component.POExpandableText
import com.processout.sdk.ui.core.component.PORequestFocus
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.dropdown.PODropdownField
import com.processout.sdk.ui.core.component.field.text.POTextField
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.shared.component.rememberLifecycleEvent
import com.processout.sdk.ui.shared.extension.conditional
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
    val lifecycleEvent = rememberLifecycleEvent()
    state.sections.elements.forEachIndexed { index, section ->
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            section.title?.let {
                with(style.label) {
                    POText(
                        text = it,
                        modifier = Modifier.conditional(
                            condition = index != 0,
                            modifier = { padding(top = spacing.extraLarge) }
                        ),
                        color = color,
                        style = textStyle
                    )
                }
            }
            section.items.elements.forEach { item ->
                Item(
                    id = id,
                    item = item,
                    onEvent = onEvent,
                    lifecycleEvent = lifecycleEvent,
                    focusedFieldId = state.focusedFieldId,
                    isPrimaryActionEnabled = state.primaryAction.enabled && !state.primaryAction.loading,
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
        is Item.DropdownField -> DropdownField(
            id = id,
            state = item.state,
            onEvent = onEvent,
            fieldStyle = style.field,
            menuStyle = style.dropdownMenu,
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
                    DynamicCheckoutEvent.Action(
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
private fun AnimatedFieldIcon(@DrawableRes id: Int) {
    POAnimatedImage(
        id = id,
        modifier = Modifier
            .requiredHeight(dimensions.formComponentMinHeight)
            .padding(POField.contentPadding),
        contentScale = ContentScale.FillHeight
    )
}
