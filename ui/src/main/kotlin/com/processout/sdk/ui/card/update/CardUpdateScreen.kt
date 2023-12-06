package com.processout.sdk.ui.card.update

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.processout.sdk.ui.card.update.CardUpdateEvent.*
import com.processout.sdk.ui.core.component.POActionsContainer
import com.processout.sdk.ui.core.component.POHeader
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.POTextField
import com.processout.sdk.ui.core.state.*
import com.processout.sdk.ui.core.style.POAxis
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import kotlinx.coroutines.job

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
            with(style.errorMessage) {
                POText(
                    text = state.errorMessage ?: String(),
                    color = color,
                    style = textStyle
                )
            }
        }
    }
}

@Composable
private fun Fields(
    fields: POImmutableCollection<POFieldState>,
    onEvent: (CardUpdateEvent) -> Unit,
    style: POField.Style = POField.default
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        coroutineContext.job.invokeOnCompletion {
            focusRequester.requestFocus()
        }
    }

    fields.elements.forEachIndexed { index, state ->
        var text by remember { mutableStateOf(String()) }
        text = state.value
        POTextField(
            value = text,
            onValueChange = {
                val formatted = state.formatter?.format(it) ?: it
                text = formatted
                onEvent(FieldValueChanged(key = state.key, value = formatted))
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
            keyboardActions = if (index == fields.elements.size - 1)
                KeyboardActions(onDone = { onEvent(Submit) })
            else KeyboardActions.Default
        )
    }
}

@Composable
private fun AnimatedIcon(
    @DrawableRes id: Int,
    visibleState: MutableTransitionState<Boolean> = remember {
        MutableTransitionState(initialState = false)
            .apply { targetState = true }
    }
) {
    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(animationSpec = tween()),
        exit = fadeOut(animationSpec = tween())
    ) {
        Image(
            painter = painterResource(id = id),
            contentDescription = null,
            modifier = Modifier
                .height(ProcessOutTheme.dimensions.formComponentHeight)
                .padding(POField.contentPadding),
            contentScale = ContentScale.FillHeight
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
