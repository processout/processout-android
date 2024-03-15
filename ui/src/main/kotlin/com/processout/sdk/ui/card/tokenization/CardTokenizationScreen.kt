package com.processout.sdk.ui.card.tokenization

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.Lifecycle
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent.*
import com.processout.sdk.ui.card.tokenization.CardTokenizationSection.Item
import com.processout.sdk.ui.core.component.POActionsContainer
import com.processout.sdk.ui.core.component.POHeader
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.PODropdownField
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.POTextField
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.state.POMutableFieldState
import com.processout.sdk.ui.core.state.POStableList
import com.processout.sdk.ui.core.style.POAxis
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.shared.composable.AnimatedImage
import com.processout.sdk.ui.shared.composable.ExpandableText
import com.processout.sdk.ui.shared.composable.RequestFocus
import com.processout.sdk.ui.shared.composable.rememberLifecycleEvent

@Composable
internal fun CardTokenizationScreen(
    state: CardTokenizationState,
    sections: POStableList<CardTokenizationSection>,
    onEvent: (CardTokenizationEvent) -> Unit,
    style: CardTokenizationScreen.Style = CardTokenizationScreen.style()
) {
    Scaffold(
        modifier = Modifier
            .nestedScroll(rememberNestedScrollInteropConnection())
            .clip(shape = ProcessOutTheme.shapes.topRoundedCornersLarge),
        containerColor = style.backgroundColor,
        topBar = {
            POHeader(
                modifier = Modifier.verticalScroll(rememberScrollState()),
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
                style = style.actionsContainer
            )
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = ProcessOutTheme.spacing.extraLarge,
                    vertical = ProcessOutTheme.spacing.large
                ),
            verticalArrangement = Arrangement.spacedBy(ProcessOutTheme.spacing.small)
        ) {
            Sections(
                state = state,
                sections = sections,
                onEvent = onEvent,
                style = style
            )
        }
    }
}

@Composable
private fun Sections(
    state: CardTokenizationState,
    sections: POStableList<CardTokenizationSection>,
    onEvent: (CardTokenizationEvent) -> Unit,
    style: CardTokenizationScreen.Style
) {
    if (state.focusedFieldId == null) {
        LocalFocusManager.current.clearFocus(force = true)
    }
    val lifecycleEvent = rememberLifecycleEvent()
    sections.elements.forEach { section ->
        section.title?.let {
            with(style.sectionTitle) {
                POText(
                    text = it,
                    color = color,
                    style = textStyle
                )
            }
        }
        section.items.forEach { item ->
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
        ExpandableText(
            text = section.errorMessage,
            style = style.errorMessage,
            modifier = Modifier.fillMaxWidth()
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
        is Item.Group -> Row(
            horizontalArrangement = Arrangement.spacedBy(ProcessOutTheme.spacing.small)
        ) {
            item.items.forEach { groupItem ->
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
    state: POMutableFieldState,
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
        trailingIcon = { state.iconResId?.let { AnimatedIcon(id = it) } },
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
        RequestFocus(focusRequester, lifecycleEvent)
    }
}

@Composable
private fun DropdownField(
    state: POMutableFieldState,
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
        modifier = modifier,
        fieldStyle = fieldStyle,
        menuStyle = menuStyle,
        enabled = state.enabled,
        isError = state.isError,
        placeholderText = state.placeholder
    )
}

@Composable
private fun AnimatedIcon(@DrawableRes id: Int) {
    AnimatedImage(
        id = id,
        modifier = Modifier
            .requiredHeight(ProcessOutTheme.dimensions.formComponentHeight)
            .padding(POField.contentPadding),
        contentScale = ContentScale.FillHeight
    )
}

@Composable
private fun Actions(
    primary: POActionState,
    secondary: POActionState?,
    onEvent: (CardTokenizationEvent) -> Unit,
    style: POActionsContainer.Style
) {
    val actions = mutableListOf(primary)
    secondary?.let { actions.add(it) }
    POActionsContainer(
        actions = POImmutableList(
            if (style.axis == POAxis.Horizontal) actions.reversed() else actions
        ),
        onClick = { onEvent(Action(id = it)) },
        style = style
    )
}

internal object CardTokenizationScreen {

    @Immutable
    data class Style(
        val title: POText.Style,
        val sectionTitle: POText.Style,
        val field: POField.Style,
        val dropdownMenu: PODropdownField.MenuStyle,
        val errorMessage: POText.Style,
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
        } ?: POText.labelHeading,
        field = custom?.field?.let {
            POField.custom(style = it)
        } ?: POField.default,
        dropdownMenu = custom?.dropdownMenu?.let {
            PODropdownField.custom(style = it)
        } ?: PODropdownField.defaultMenu,
        errorMessage = custom?.errorMessage?.let {
            POText.custom(style = it)
        } ?: POText.errorLabel,
        actionsContainer = custom?.actionsContainer?.let {
            POActionsContainer.custom(style = it)
        } ?: POActionsContainer.default,
        backgroundColor = custom?.backgroundColorResId?.let {
            colorResource(id = it)
        } ?: ProcessOutTheme.colors.surface.level1,
        dividerColor = custom?.dividerColorResId?.let {
            colorResource(id = it)
        } ?: ProcessOutTheme.colors.border.subtle,
        dragHandleColor = custom?.dragHandleColorResId?.let {
            colorResource(id = it)
        } ?: ProcessOutTheme.colors.border.disabled
    )
}
