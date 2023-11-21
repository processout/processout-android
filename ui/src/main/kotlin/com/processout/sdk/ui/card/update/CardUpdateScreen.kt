package com.processout.sdk.ui.card.update

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import com.processout.sdk.ui.card.update.CardUpdateEvent.Cancel
import com.processout.sdk.ui.card.update.CardUpdateEvent.Submit
import com.processout.sdk.ui.core.component.POActionsContainer
import com.processout.sdk.ui.core.component.POHeader
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POActionStateExtended
import com.processout.sdk.ui.core.state.POImmutableCollection
import com.processout.sdk.ui.core.theme.ProcessOutTheme

@Composable
internal fun CardUpdateScreen(
    state: CardUpdateState,
    onEvent: (CardUpdateEvent) -> Unit,
    style: CardUpdateScreen.Style = CardUpdateScreen.style()
) {
    Surface(
        modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection()),
        shape = ProcessOutTheme.shapes.topRoundedCornersLarge,
        color = style.backgroundColor
    ) {
        Scaffold(
            containerColor = Color.Transparent,
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
                    .padding(scaffoldPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                POHeader(
                    title = state.title,
                    style = style.title,
                    dividerColor = style.dividerColor,
                    dragHandleColor = style.dragHandleColor,
                    withDragHandle = state.draggable
                )
                Fields()
            }
        }
    }
}

@Composable
private fun Fields() {
    // TODO
}

@Composable
private fun Actions(
    primary: POActionState,
    secondary: POActionState,
    onEvent: (CardUpdateEvent) -> Unit,
    style: POActionsContainer.Style = POActionsContainer.default
) = POActionsContainer(
    actions = POImmutableCollection(
        listOf(
            POActionStateExtended(
                state = secondary,
                onClick = { onEvent(Cancel) }
            ),
            POActionStateExtended(
                state = primary,
                onClick = { onEvent(Submit) }
            )
        )
    ),
    style = style
)

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
        errorDescription = custom?.errorDescription?.let { POText.custom(style = it) } ?: POText.errorLabel,
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
