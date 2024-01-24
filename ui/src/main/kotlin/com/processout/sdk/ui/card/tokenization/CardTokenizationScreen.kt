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
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent.*
import com.processout.sdk.ui.card.tokenization.CardTokenizationSection.Item
import com.processout.sdk.ui.core.component.POActionsContainer
import com.processout.sdk.ui.core.component.POHeader
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.POTextField
import com.processout.sdk.ui.core.state.*
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
    modifier: Modifier = Modifier,
    style: POField.Style = POField.default
) {
    when (item) {
        is Item.TextField -> TextField(
            state = item.state,
            onEvent = onEvent,
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
    modifier: Modifier = Modifier,
    style: POField.Style = POField.default
) {
    POTextField(
        value = state.value.value,
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
        trailingIcon = { state.iconResId.value?.let { AnimatedIcon(id = it) } },
        keyboardOptions = state.keyboardOptions,
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
    val actions = mutableListOf(
        POActionStateExtended(
            state = primary,
            onClick = { onEvent(Submit) }
        ))
    secondary?.let {
        actions.add(
            POActionStateExtended(
                state = it,
                onClick = { onEvent(Cancel) }
            ))
    }
    POActionsContainer(
        actions = POImmutableList(
            if (style.axis == POAxis.Horizontal) actions.reversed() else actions
        ),
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
