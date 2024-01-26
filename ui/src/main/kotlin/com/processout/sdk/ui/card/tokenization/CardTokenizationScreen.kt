package com.processout.sdk.ui.card.tokenization

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent.Action
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent.FieldValueChanged
import com.processout.sdk.ui.card.tokenization.CardTokenizationSection.Item
import com.processout.sdk.ui.core.component.POActionsContainer
import com.processout.sdk.ui.core.component.POHeader
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.POTextField
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.state.POMutableFieldState
import com.processout.sdk.ui.core.state.POStableList
import com.processout.sdk.ui.core.style.POAxis
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.shared.composable.AnimatedImage

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
            sections.elements.forEach { section ->
                section.items.elements.forEach { item ->
                    Item(
                        item = item,
                        onEvent = onEvent,
                        isPrimaryActionEnabled = state.primaryAction.enabled,
                        modifier = Modifier.fillMaxWidth(),
                        style = style.field
                    )
                }
            }
        }
    }
}

@Composable
private fun Item(
    item: Item,
    onEvent: (CardTokenizationEvent) -> Unit,
    isPrimaryActionEnabled: Boolean,
    modifier: Modifier = Modifier,
    style: POField.Style = POField.default
) {
    when (item) {
        is Item.TextField -> TextField(
            state = item.state,
            onEvent = onEvent,
            isPrimaryActionEnabled = isPrimaryActionEnabled,
            modifier = modifier,
            style = style
        )
        is Item.Group -> Row(
            horizontalArrangement = Arrangement.spacedBy(ProcessOutTheme.spacing.small)
        ) {
            item.items.elements.forEach { groupItem ->
                Item(
                    item = groupItem,
                    onEvent = onEvent,
                    isPrimaryActionEnabled = isPrimaryActionEnabled,
                    modifier = Modifier.weight(1f),
                    style = style
                )
            }
        }
    }
}

@Composable
private fun TextField(
    state: POMutableFieldState,
    onEvent: (CardTokenizationEvent) -> Unit,
    isPrimaryActionEnabled: Boolean,
    modifier: Modifier = Modifier,
    style: POField.Style = POField.default
) {
    POTextField(
        value = state.value,
        onValueChange = {
            onEvent(
                FieldValueChanged(
                    key = state.key,
                    value = state.inputFilter?.filter(it) ?: it
                )
            )
        },
        modifier = modifier,
        style = style,
        enabled = state.enabled,
        isError = state.isError,
        forceTextDirectionLtr = state.forceTextDirectionLtr,
        placeholderText = state.placeholder,
        trailingIcon = { state.iconResId?.let { AnimatedIcon(id = it) } },
        keyboardOptions = state.keyboardOptions,
        keyboardActions = POField.keyboardActions(
            imeAction = state.keyboardOptions.imeAction,
            actionKey = state.keyboardActionKey,
            enabled = isPrimaryActionEnabled,
            onClick = { onEvent(Action(key = it)) }
        ),
        visualTransformation = state.visualTransformation
    )
}

@Composable
private fun AnimatedIcon(@DrawableRes id: Int) {
    AnimatedImage(
        id = id,
        modifier = Modifier
            .height(ProcessOutTheme.dimensions.formComponentHeight)
            .padding(POField.contentPadding),
        contentScale = ContentScale.FillHeight
    )
}

@Composable
private fun Actions(
    primary: POActionState,
    secondary: POActionState?,
    onEvent: (CardTokenizationEvent) -> Unit,
    style: POActionsContainer.Style = POActionsContainer.default
) {
    val actions = mutableListOf(primary)
    secondary?.let { actions.add(it) }
    POActionsContainer(
        actions = POImmutableList(
            if (style.axis == POAxis.Horizontal) actions.reversed() else actions
        ),
        onClick = { onEvent(Action(key = it)) },
        style = style
    )
}

internal object CardTokenizationScreen {

    @Immutable
    data class Style(
        val title: POText.Style,
        val field: POField.Style,
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
        field = custom?.field?.let {
            POField.custom(style = it)
        } ?: POField.default,
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
