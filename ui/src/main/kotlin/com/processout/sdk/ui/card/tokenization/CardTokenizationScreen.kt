package com.processout.sdk.ui.card.tokenization

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent.*
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState.Item
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState.SectionId.FUTURE_PAYMENTS
import com.processout.sdk.ui.core.component.*
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.checkbox.POCheckbox
import com.processout.sdk.ui.core.component.field.dropdown.PODropdownField
import com.processout.sdk.ui.core.component.field.text.POTextField
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.style.POAxis
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.shared.component.rememberLifecycleEvent
import com.processout.sdk.ui.shared.extension.dpToPx
import com.processout.sdk.ui.shared.state.FieldState

@Composable
internal fun CardTokenizationScreen(
    state: CardTokenizationViewModelState,
    onEvent: (CardTokenizationEvent) -> Unit,
    onContentHeightChanged: (Int) -> Unit,
    style: CardTokenizationScreen.Style = CardTokenizationScreen.style()
) {
    var topBarHeight by remember { mutableIntStateOf(0) }
    var bottomBarHeight by remember { mutableIntStateOf(0) }
    Scaffold(
        modifier = Modifier
            .nestedScroll(rememberNestedScrollInteropConnection())
            .clip(shape = shapes.topRoundedCornersLarge),
        containerColor = style.backgroundColor,
        topBar = {
            POHeader(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .onGloballyPositioned {
                        topBarHeight = it.size.height
                    },
                title = state.title,
                style = style.title,
                dividerColor = style.dividerColor,
                dragHandleColor = style.dragHandleColor,
                withDragHandle = state.draggable
            )
        },
        bottomBar = {
            Actions(
                primary = state.primaryAction,
                secondary = state.secondaryAction,
                onEvent = onEvent,
                style = style.actionsContainer,
                modifier = Modifier.onGloballyPositioned {
                    bottomBarHeight = it.size.height
                }
            )
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .verticalScroll(rememberScrollState())
                .padding(spacing.extraLarge)
        ) {
            val verticalSpacingPx = (spacing.extraLarge * 4 + 10.dp).dpToPx()
            Column(
                modifier = Modifier.onGloballyPositioned {
                    val contentHeight = it.size.height + topBarHeight + bottomBarHeight + verticalSpacingPx
                    onContentHeightChanged(contentHeight)
                }
            ) {
                var clearFocus by remember { mutableStateOf(false) }
                state.cardScannerAction?.let { action ->
                    POButton(
                        state = action,
                        onClick = {
                            clearFocus = true
                            onEvent(Action(id = it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeightIn(min = dimensions.buttonIconSizeSmall)
                            .padding(bottom = spacing.small),
                        style = style.scanButton,
                        iconSize = dimensions.iconSizeSmall
                    )
                }
                Sections(
                    state = state,
                    onEvent = onEvent,
                    style = style
                )
                if (clearFocus || state.focusedFieldId == null) {
                    LocalFocusManager.current.clearFocus(force = true)
                    clearFocus = false
                }
            }
        }
    }
}

@Composable
private fun Sections(
    state: CardTokenizationViewModelState,
    onEvent: (CardTokenizationEvent) -> Unit,
    style: CardTokenizationScreen.Style
) {
    val lifecycleEvent = rememberLifecycleEvent()
    state.sections.elements.forEachIndexed { index, section ->
        val padding = if (section.id == FUTURE_PAYMENTS) {
            spacing.small
        } else when (index) {
            0 -> 0.dp
            else -> spacing.extraLarge
        }
        Spacer(Modifier.requiredHeight(padding))
        Column(
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
            section.items.elements.forEach { item ->
                Item(
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
            style = style.errorMessage,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = spacing.small)
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
    POTextField(
        value = state.value,
        onValueChange = {
            onEvent(
                FieldValueChanged(
                    id = state.id,
                    value = state.inputFilter?.filter(it) ?: it
                )
            )
        },
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                onEvent(
                    FieldFocusChanged(
                        id = state.id,
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
            onClick = { onEvent(Action(id = it)) }
        )
    )
    if (state.id == focusedFieldId && lifecycleEvent == Lifecycle.Event.ON_RESUME) {
        PORequestFocus(focusRequester, lifecycleEvent)
    }
}

@Composable
private fun DropdownField(
    state: FieldState,
    onEvent: (CardTokenizationEvent) -> Unit,
    fieldStyle: POField.Style,
    menuStyle: PODropdownField.MenuStyle,
    modifier: Modifier = Modifier
) {
    PODropdownField(
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
        modifier = modifier
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
        isError = state.isError,
        placeholderText = state.placeholder
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
        text = state.title ?: String(),
        checked = state.value.text.toBooleanStrictOrNull() ?: false,
        onCheckedChange = {
            onEvent(
                FieldValueChanged(
                    id = state.id,
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

@Composable
private fun Actions(
    primary: POActionState,
    secondary: POActionState?,
    onEvent: (CardTokenizationEvent) -> Unit,
    style: POActionsContainer.Style,
    modifier: Modifier = Modifier
) {
    val actions = mutableListOf(primary)
    secondary?.let { actions.add(it) }
    POActionsContainer(
        modifier = modifier,
        actions = POImmutableList(
            if (style.axis == POAxis.Horizontal) actions.reversed() else actions
        ),
        onClick = { onEvent(Action(id = it)) },
        containerStyle = style
    )
}

internal object CardTokenizationScreen {

    @Immutable
    data class Style(
        val title: POText.Style,
        val sectionTitle: POText.Style,
        val field: POField.Style,
        val checkbox: POCheckbox.Style,
        val dropdownMenu: PODropdownField.MenuStyle,
        val errorMessage: POText.Style,
        val scanButton: POButton.Style,
        val actionsContainer: POActionsContainer.Style,
        val backgroundColor: Color,
        val dividerColor: Color,
        val dragHandleColor: Color
    )

    @Composable
    fun style(custom: POCardTokenizationConfiguration.Style? = null) = Style(
        title = custom?.title?.let {
            POText.custom(style = it)
        } ?: POText.title,
        sectionTitle = custom?.sectionTitle?.let {
            POText.custom(style = it)
        } ?: POText.label1,
        field = custom?.field?.let {
            POField.custom(style = it)
        } ?: POField.default,
        checkbox = custom?.checkbox?.let {
            POCheckbox.custom(style = it)
        } ?: POCheckbox.default,
        dropdownMenu = custom?.dropdownMenu?.let {
            PODropdownField.custom(style = it)
        } ?: PODropdownField.defaultMenu,
        errorMessage = custom?.errorMessage?.let {
            POText.custom(style = it)
        } ?: POText.errorLabel,
        scanButton = custom?.scanButton?.let {
            POButton.custom(style = it)
        } ?: POButton.secondary,
        actionsContainer = custom?.actionsContainer?.let {
            POActionsContainer.custom(style = it)
        } ?: POActionsContainer.default,
        backgroundColor = custom?.backgroundColorResId?.let {
            colorResource(id = it)
        } ?: colors.surface.default,
        dividerColor = custom?.dividerColorResId?.let {
            colorResource(id = it)
        } ?: colors.border.subtle,
        dragHandleColor = custom?.dragHandleColorResId?.let {
            colorResource(id = it)
        } ?: colors.border.subtle
    )
}
