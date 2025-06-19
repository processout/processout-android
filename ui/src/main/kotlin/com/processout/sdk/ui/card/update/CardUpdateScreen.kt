package com.processout.sdk.ui.card.update

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
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.processout.sdk.ui.card.update.CardUpdateEvent.*
import com.processout.sdk.ui.core.component.*
import com.processout.sdk.ui.core.component.field.POField
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
internal fun CardUpdateScreen(
    state: CardUpdateState,
    onEvent: (CardUpdateEvent) -> Unit,
    onContentHeightChanged: (Int) -> Unit,
    style: CardUpdateScreen.Style = CardUpdateScreen.style()
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
                Fields(
                    fields = state.fields,
                    onEvent = onEvent,
                    focusedFieldId = state.focusedFieldId,
                    isPrimaryActionEnabled = state.primaryAction.enabled && !state.primaryAction.loading,
                    style = style.field
                )
                POExpandableText(
                    text = state.errorMessage,
                    style = style.errorMessage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = spacing.small)
                )
            }
        }
    }
}

@Composable
private fun Fields(
    fields: POImmutableList<FieldState>,
    onEvent: (CardUpdateEvent) -> Unit,
    focusedFieldId: String?,
    isPrimaryActionEnabled: Boolean,
    style: POField.Style
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        val lifecycleEvent = rememberLifecycleEvent()
        fields.elements.forEach { state ->
            val focusRequester = remember { FocusRequester() }
            POTextField(
                value = state.value,
                onValueChange = {
                    if (state.enabled) {
                        onEvent(
                            FieldValueChanged(
                                id = state.id,
                                value = state.inputFilter?.filter(it) ?: it
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
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
                readOnly = !state.enabled,
                isError = state.isError,
                forceTextDirectionLtr = state.forceTextDirectionLtr,
                placeholder = state.placeholder,
                trailingIcon = { state.iconResId?.let { AnimatedFieldIcon(id = it) } },
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
    }
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
    onEvent: (CardUpdateEvent) -> Unit,
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
        } ?: colors.surface.default,
        dividerColor = custom?.dividerColorResId?.let {
            colorResource(id = it)
        } ?: colors.border.subtle,
        dragHandleColor = custom?.dragHandleColorResId?.let {
            colorResource(id = it)
        } ?: colors.border.subtle
    )
}
