@file:Suppress("CrossfadeLabel")

package com.processout.sdk.ui.napm

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.ui.core.component.POActionsContainer
import com.processout.sdk.ui.core.component.POCircularProgressIndicator
import com.processout.sdk.ui.core.component.POHeader
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.code.POCodeField
import com.processout.sdk.ui.core.component.field.dropdown.PODropdownField
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup
import com.processout.sdk.ui.core.component.field.text.POTextField
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.style.POAxis
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent.Action
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.*

@Composable
internal fun NativeAlternativePaymentScreen(
    state: NativeAlternativePaymentViewModelState,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    style: NativeAlternativePaymentScreen.Style = NativeAlternativePaymentScreen.style()
) {
    Scaffold(
        modifier = Modifier
            .nestedScroll(rememberNestedScrollInteropConnection())
            .clip(shape = ProcessOutTheme.shapes.topRoundedCornersLarge),
        containerColor = style.normalBackgroundColor,
        topBar = {
            POHeader(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                title = if (state is UserInput) state.title else null,
                style = style.title,
                dividerColor = style.dividerColor,
                dragHandleColor = style.dragHandleColor,
                animationDurationMillis = AnimationConstants.DefaultDurationMillis
            )
        },
        bottomBar = {
            Actions(
                state = state,
                onEvent = onEvent,
                style = style.actionsContainer
            )
        }
    ) { scaffoldPadding ->
        Crossfade(
            targetState = state,
            animationSpec = tween(durationMillis = AnimationConstants.DefaultDurationMillis)
        ) { state ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = ProcessOutTheme.spacing.extraLarge,
                        vertical = ProcessOutTheme.spacing.large
                    ),
                verticalArrangement = Arrangement.spacedBy(
                    space = ProcessOutTheme.spacing.small,
                    alignment = Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (state) {
                    Loading -> POCircularProgressIndicator.Medium(color = style.progressIndicatorColor)
                    is UserInput -> UserInput(state)
                    is Capture -> Capture(state)
                }
            }
        }
    }
}

@Composable
private fun UserInput(
    state: UserInput
) {
    POTextField(value = TextFieldValue(), onValueChange = {})
    POTextField(value = TextFieldValue(), onValueChange = {})
    POTextField(value = TextFieldValue(), onValueChange = {})
}

@Composable
private fun Capture(
    state: Capture
) {
    POText(text = "111")
    POText(text = "222")
    POText(text = "333")
}

@Composable
private fun Actions(
    state: NativeAlternativePaymentViewModelState,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    style: POActionsContainer.Style
) {
    var primary: POActionState? = null
    var secondary: POActionState? = null
    when (state) {
        is UserInput -> {
            primary = state.primaryAction
            secondary = state.secondaryAction
        }
        is Capture -> secondary = state.secondaryAction
        else -> {}
    }
    val actions = mutableListOf<POActionState>()
    primary?.let { actions.add(it) }
    secondary?.let { actions.add(it) }
    if (actions.isNotEmpty()) {
        POActionsContainer(
            actions = POImmutableList(
                if (style.axis == POAxis.Horizontal) actions.reversed() else actions
            ),
            onClick = { onEvent(Action(id = it)) },
            style = style
        )
    }
}

internal object NativeAlternativePaymentScreen {

    @Immutable
    data class Style(
        val title: POText.Style,
        val label: POText.Style,
        val field: POField.Style,
        val codeField: POField.Style,
        val radioGroup: PORadioGroup.Style,
        val dropdownMenu: PODropdownField.MenuStyle,
        val actionsContainer: POActionsContainer.Style,
        val normalBackgroundColor: Color,
        val successBackgroundColor: Color,
        val message: POText.Style,
        val errorMessage: POText.Style,
        val successMessage: POText.Style,
        @DrawableRes val successImageResId: Int?,
        val progressIndicatorColor: Color,
        val controlsTintColor: Color,
        val dividerColor: Color,
        val dragHandleColor: Color
    )

    @Composable
    fun style(custom: PONativeAlternativePaymentConfiguration.Style? = null) = Style(
        title = custom?.title?.let {
            POText.custom(style = it)
        } ?: POText.title,
        label = custom?.label?.let {
            POText.custom(style = it)
        } ?: POText.labelHeading,
        field = custom?.field?.let {
            POField.custom(style = it)
        } ?: POField.default,
        codeField = custom?.codeField?.let {
            POField.custom(style = it)
        } ?: POCodeField.default,
        radioGroup = custom?.radioButton?.let {
            PORadioGroup.custom(style = it)
        } ?: PORadioGroup.default,
        dropdownMenu = custom?.dropdownMenu?.let {
            PODropdownField.custom(style = it)
        } ?: PODropdownField.defaultMenu,
        actionsContainer = custom?.actionsContainer?.let {
            POActionsContainer.custom(style = it)
        } ?: POActionsContainer.default,
        normalBackgroundColor = custom?.background?.normalColorResId?.let {
            colorResource(id = it)
        } ?: ProcessOutTheme.colors.surface.level1,
        successBackgroundColor = custom?.background?.successColorResId?.let {
            colorResource(id = it)
        } ?: ProcessOutTheme.colors.surface.success,
        message = custom?.message?.let {
            POText.custom(style = it)
        } ?: POText.Style(
            color = ProcessOutTheme.colors.text.primary,
            textStyle = ProcessOutTheme.typography.fixed.bodyCompact
        ),
        errorMessage = custom?.errorMessage?.let {
            POText.custom(style = it)
        } ?: POText.errorLabel,
        successMessage = custom?.successMessage?.let {
            POText.custom(style = it)
        } ?: POText.Style(
            color = ProcessOutTheme.colors.text.success,
            textStyle = ProcessOutTheme.typography.fixed.body
        ),
        successImageResId = custom?.successImageResId,
        progressIndicatorColor = custom?.progressIndicatorColorResId?.let {
            colorResource(id = it)
        } ?: ProcessOutTheme.colors.action.primaryDefault,
        controlsTintColor = custom?.controlsTintColorResId?.let {
            colorResource(id = it)
        } ?: ProcessOutTheme.colors.action.primaryDefault,
        dividerColor = custom?.dividerColorResId?.let {
            colorResource(id = it)
        } ?: ProcessOutTheme.colors.border.subtle,
        dragHandleColor = custom?.dragHandleColorResId?.let {
            colorResource(id = it)
        } ?: ProcessOutTheme.colors.border.disabled
    )
}
