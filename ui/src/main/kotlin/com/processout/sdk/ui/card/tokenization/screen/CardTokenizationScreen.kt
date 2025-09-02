package com.processout.sdk.ui.card.tokenization.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent.Action
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration
import com.processout.sdk.ui.core.component.POActionsContainer
import com.processout.sdk.ui.core.component.POButton
import com.processout.sdk.ui.core.component.POHeader
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.checkbox.POCheckbox
import com.processout.sdk.ui.core.component.field.dropdown.PODropdownField
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.style.POAxis
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.shared.extension.dpToPx

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
            val verticalSpacingPx = (spacing.extraLarge * 4 + 15.dp).dpToPx()
            CardTokenizationContent(
                state = state,
                onEvent = onEvent,
                onContentHeightChanged = { contentHeight ->
                    val totalHeight = contentHeight + topBarHeight + bottomBarHeight + verticalSpacingPx
                    onContentHeightChanged(totalHeight)
                },
                style = style
            )
        }
    }
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
        val radioGroup: PORadioGroup.Style,
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
        radioGroup = custom?.radioButton?.let {
            PORadioGroup.custom(style = it)
        } ?: PORadioGroup.default,
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
        } ?: colors.border.border4,
        dragHandleColor = custom?.dragHandleColorResId?.let {
            colorResource(id = it)
        } ?: colors.icon.disabled
    )
}
