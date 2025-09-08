package com.processout.sdk.ui.card.tokenization.screen

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
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent.*
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState.Item
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState.Section
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState.SectionId.CARD_INFORMATION
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState.SectionId.FUTURE_PAYMENTS
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState.SectionId.PREFERRED_SCHEME
import com.processout.sdk.ui.core.component.*
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.checkbox.POCheckbox
import com.processout.sdk.ui.core.component.field.dropdown.PODropdownField
import com.processout.sdk.ui.core.component.field.dropdown.PODropdownField2
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup
import com.processout.sdk.ui.core.component.field.text.POTextField2
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.style.POAxis
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.shared.component.rememberLifecycleEvent
import com.processout.sdk.ui.shared.state.FieldState

@Composable
internal fun CardTokenizationContent(
    state: CardTokenizationViewModelState,
    onEvent: (CardTokenizationEvent) -> Unit,
    style: CardTokenizationScreen.Style,
    withActionsContainer: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (state.focusedFieldId == null) {
            LocalFocusManager.current.clearFocus(force = true)
        }
        state.cardScannerAction?.let { action ->
            POButton(
                state = action,
                onClick = { onEvent(Action(id = it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeightIn(min = dimensions.buttonIconSizeSmall)
                    .padding(bottom = spacing.small),
                style = style.scanButton,
                iconSize = dimensions.iconSizeSmall
            )
        }
        val lifecycleEvent = rememberLifecycleEvent()
        state.sections.elements.forEach { section ->
            Section(
                section = section,
                onEvent = onEvent,
                lifecycleEvent = lifecycleEvent,
                focusedFieldId = state.focusedFieldId,
                isPrimaryActionEnabled = state.primaryAction?.enabled == true && !state.primaryAction.loading,
                style = style
            )
        }
        if (withActionsContainer) {
            ActionsContainer(
                primary = state.primaryAction,
                secondary = state.secondaryAction,
                onEvent = onEvent,
                style = style.actionsContainer,
                confirmationDialogStyle = style.dialog
            )
        }
    }
}

@Composable
private fun Section(
    section: Section,
    onEvent: (CardTokenizationEvent) -> Unit,
    lifecycleEvent: Lifecycle.Event,
    focusedFieldId: String?,
    isPrimaryActionEnabled: Boolean,
    style: CardTokenizationScreen.Style
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
            with(style.sectionTitle) {
                POText(
                    text = it,
                    color = color,
                    style = textStyle
                )
            }
        }
        section.items?.elements?.forEach { item ->
            Item(
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
        style = style.errorMessage,
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
    item: Item,
    onEvent: (CardTokenizationEvent) -> Unit,
    lifecycleEvent: Lifecycle.Event,
    focusedFieldId: String?,
    isPrimaryActionEnabled: Boolean,
    style: CardTokenizationScreen.Style,
    modifier: Modifier = Modifier
) {
    when (item) {
        is Item.TextField -> TextField(
            state = item.state,
            onEvent = onEvent,
            lifecycleEvent = lifecycleEvent,
            focusedFieldId = focusedFieldId,
            isPrimaryActionEnabled = isPrimaryActionEnabled,
            style = style.field,
            modifier = modifier
        )
        is Item.RadioField -> RadioField(
            state = item.state,
            onEvent = onEvent,
            style = style.radioGroup,
            modifier = modifier
        )
        is Item.DropdownField -> DropdownField(
            state = item.state,
            onEvent = onEvent,
            fieldStyle = style.field,
            menuStyle = style.dropdownMenu,
            modifier = modifier
        )
        is Item.CheckboxField -> CheckboxField(
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
    state: FieldState,
    onEvent: (CardTokenizationEvent) -> Unit,
    lifecycleEvent: Lifecycle.Event,
    focusedFieldId: String?,
    isPrimaryActionEnabled: Boolean,
    style: POField.Style,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    POTextField2(
        value = state.value,
        onValueChange = {
            onEvent(
                FieldValueChanged(
                    id = state.id,
                    value = state.inputFilter?.filter(it) ?: it
                )
            )
        },
        modifier = modifier,
        textFieldModifier = Modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                onEvent(
                    FieldFocusChanged(
                        id = state.id,
                        isFocused = it.isFocused
                    )
                )
            },
        fieldStyle = style,
        enabled = state.enabled,
        isError = state.isError,
        forceTextDirectionLtr = state.forceTextDirectionLtr,
        label = state.label,
        trailingIcon = { state.iconResId?.let { AnimatedFieldIcon(id = it) } },
        visualTransformation = state.visualTransformation,
        keyboardOptions = state.keyboardOptions,
        keyboardActions = POField.keyboardActions(
            imeAction = state.keyboardOptions.imeAction,
            actionId = state.keyboardActionId,
            enabled = isPrimaryActionEnabled,
            onClick = { onEvent(Action(id = it)) }
        )
    )
    if (state.id == focusedFieldId && lifecycleEvent == Lifecycle.Event.ON_RESUME) {
        PORequestFocus(focusRequester, lifecycleEvent)
    }
}

@Composable
private fun RadioField(
    state: FieldState,
    onEvent: (CardTokenizationEvent) -> Unit,
    style: PORadioGroup.Style,
    modifier: Modifier = Modifier
) {
    PORadioGroup(
        value = state.value.text,
        onValueChange = {
            if (state.enabled) {
                onEvent(
                    FieldValueChanged(
                        id = state.id,
                        value = TextFieldValue(text = it)
                    )
                )
            }
        },
        availableValues = state.availableValues ?: POImmutableList(emptyList()),
        modifier = modifier,
        style = style
    )
}

@Composable
private fun DropdownField(
    state: FieldState,
    onEvent: (CardTokenizationEvent) -> Unit,
    fieldStyle: POField.Style,
    menuStyle: PODropdownField.MenuStyle,
    modifier: Modifier = Modifier
) {
    PODropdownField2(
        value = state.value,
        onValueChange = {
            onEvent(
                FieldValueChanged(
                    id = state.id,
                    value = it
                )
            )
        },
        availableValues = state.availableValues ?: POImmutableList(emptyList()),
        modifier = modifier,
        textFieldModifier = Modifier
            .onFocusChanged {
                onEvent(
                    FieldFocusChanged(
                        id = state.id,
                        isFocused = it.isFocused
                    )
                )
            },
        fieldStyle = fieldStyle,
        menuStyle = menuStyle,
        enabled = state.enabled,
        isError = state.isError
    )
}

@Composable
private fun CheckboxField(
    state: FieldState,
    onEvent: (CardTokenizationEvent) -> Unit,
    style: POCheckbox.Style,
    modifier: Modifier = Modifier
) {
    POCheckbox(
        text = state.label ?: String(),
        checked = state.value.text.toBooleanStrictOrNull() ?: false,
        onCheckedChange = {
            if (state.enabled) {
                onEvent(
                    FieldValueChanged(
                        id = state.id,
                        value = TextFieldValue(text = it.toString())
                    )
                )
            }
        },
        modifier = modifier,
        style = style,
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

@Composable
private fun ActionsContainer(
    primary: POActionState?,
    secondary: POActionState?,
    onEvent: (CardTokenizationEvent) -> Unit,
    style: POActionsContainer.Style,
    confirmationDialogStyle: PODialog.Style
) {
    var actions = listOfNotNull(primary, secondary)
    if (actions.isNotEmpty()) {
        if (style.axis == POAxis.Horizontal) {
            actions = actions.reversed()
        }
        val paddingTop = spacing.space28
        val spacing = spacing.space12
        when (style.axis) {
            POAxis.Vertical -> Column(
                modifier = Modifier.padding(top = paddingTop),
                verticalArrangement = Arrangement.spacedBy(space = spacing)
            ) {
                Actions(
                    actions = POImmutableList(elements = actions),
                    onClick = { onEvent(Action(id = it)) },
                    primaryActionStyle = style.primary,
                    secondaryActionStyle = style.secondary,
                    confirmationDialogStyle = confirmationDialogStyle
                )
            }
            POAxis.Horizontal -> Row(
                modifier = Modifier.padding(top = paddingTop),
                horizontalArrangement = Arrangement.spacedBy(space = spacing)
            ) {
                Actions(
                    actions = POImmutableList(elements = actions),
                    onClick = { onEvent(Action(id = it)) },
                    primaryActionStyle = style.primary,
                    secondaryActionStyle = style.secondary,
                    confirmationDialogStyle = confirmationDialogStyle,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun Actions(
    actions: POImmutableList<POActionState>,
    onClick: (String) -> Unit,
    primaryActionStyle: POButton.Style,
    secondaryActionStyle: POButton.Style,
    confirmationDialogStyle: PODialog.Style,
    modifier: Modifier = Modifier
) {
    actions.elements.forEach { state ->
        POButton(
            state = state,
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .requiredHeightIn(min = dimensions.interactiveComponentMinSize),
            style = if (state.primary) primaryActionStyle else secondaryActionStyle,
            confirmationDialogStyle = confirmationDialogStyle
        )
    }
}
