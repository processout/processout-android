package com.processout.sdk.ui.card.update

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.processout.sdk.ui.card.update.CardUpdateEvent.Cancel
import com.processout.sdk.ui.card.update.CardUpdateEvent.FieldValueChanged
import com.processout.sdk.ui.card.update.CardUpdateEvent.Submit
import com.processout.sdk.ui.core.component.POActionsContainer
import com.processout.sdk.ui.core.component.POHeader
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.POTextField
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POActionStateExtended
import com.processout.sdk.ui.core.state.POFieldState
import com.processout.sdk.ui.core.state.POImmutableCollection
import com.processout.sdk.ui.core.style.POAxis
import com.processout.sdk.ui.core.theme.ProcessOutTheme

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
                onEvent = onEvent
            )
        }
    }
}

@Composable
private fun Fields(
    fields: POImmutableCollection<POFieldState>,
    onEvent: (CardUpdateEvent) -> Unit
) {
    fields.elements.forEachIndexed { index, state ->
        var text by remember { mutableStateOf(state.value) }
        POTextField(
            value = text,
            onValueChange = {
                text = it
                onEvent(FieldValueChanged(key = state.key, value = it))
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.enabled,
            placeholderText = state.placeholder,
            trailingIcon = {
                state.iconResId?.let {
                    Image(
                        painter = painterResource(id = it),
                        contentDescription = null,
                        modifier = Modifier
                            .height(ProcessOutTheme.dimensions.formComponentHeight)
                            .padding(POField.ContentPadding),
                        contentScale = ContentScale.FillHeight
                    )
                }
            },
            keyboardOptions = state.keyboardOptions,
            keyboardActions = if (index == fields.elements.size - 1)
                KeyboardActions(onDone = { onEvent(Submit) })
            else KeyboardActions.Default
        )
    }
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
        actions = POImmutableCollection(
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
        val errorDescription: POText.Style,
        val actionsContainer: POActionsContainer.Style,
        val backgroundColor: Color,
        val dividerColor: Color,
        val dragHandleColor: Color
    )

    @Composable
    fun style(custom: POCardUpdateConfiguration.Style? = null) = Style(
        title = custom?.title?.let { POText.custom(style = it) } ?: POText.title,
        field = custom?.input?.let { POField.custom(style = it) } ?: POField.default,
        errorDescription = custom?.errorDescription?.let {
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
