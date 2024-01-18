package com.processout.sdk.ui.card.update

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.Lifecycle
import com.processout.sdk.ui.card.update.CardUpdateEvent.*
import com.processout.sdk.ui.core.component.POActionsContainer
import com.processout.sdk.ui.core.component.POHeader
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.POTextField
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POActionStateExtended
import com.processout.sdk.ui.core.state.POFieldState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.style.POAxis
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.shared.composable.AnimatedImage
import com.processout.sdk.ui.shared.composable.RequestFocus
import com.processout.sdk.ui.shared.composable.rememberLifecycleEvent

@Composable
internal fun CardUpdateScreen(
    state: CardUpdateState,
    onEvent: (CardUpdateEvent) -> Unit,
    style: CardUpdateScreen.Style = CardUpdateScreen.style()
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
            verticalArrangement = Arrangement.spacedBy(ProcessOutTheme.spacing.large)
        ) {
            Fields(
                fields = state.fields,
                onEvent = onEvent,
                style = style.field
            )
            state.errorMessage?.let {
                with(style.errorMessage) {
                    POText(
                        text = it,
                        color = color,
                        style = textStyle
                    )
                }
            }
        }
    }
}

@Composable
private fun Fields(
    fields: POImmutableList<POFieldState>,
    onEvent: (CardUpdateEvent) -> Unit,
    style: POField.Style = POField.default
) {
    val focusRequester = remember { FocusRequester() }
    val lifecycleEvent = rememberLifecycleEvent()
    if (lifecycleEvent == Lifecycle.Event.ON_RESUME) {
        RequestFocus(focusRequester, lifecycleEvent)
    }

    fields.elements.forEachIndexed { index, state ->
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
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            style = style,
            enabled = state.enabled,
            isError = state.isError,
            forceTextDirectionLtr = state.forceTextDirectionLtr,
            placeholderText = state.placeholder,
            trailingIcon = { state.iconResId?.let { AnimatedIcon(id = it) } },
            keyboardOptions = state.keyboardOptions,
            keyboardActions = if (index == fields.elements.lastIndex)
                KeyboardActions(onDone = { onEvent(Submit) })
            else KeyboardActions.Default
        )
    }
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
    onEvent: (CardUpdateEvent) -> Unit,
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

internal object CardUpdateScreen {

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
    fun style(custom: POCardUpdateConfiguration.Style? = null) = Style(
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
