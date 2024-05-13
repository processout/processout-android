@file:Suppress("AnimateAsStateLabel", "CrossfadeLabel", "MayBeConstant")

package com.processout.sdk.ui.napm

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.processout.sdk.ui.R
import com.processout.sdk.ui.core.component.POActionsContainer
import com.processout.sdk.ui.core.component.POCircularProgressIndicator
import com.processout.sdk.ui.core.component.POHeader
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.code.POCodeField
import com.processout.sdk.ui.core.component.field.dropdown.PODropdownField
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.style.POAxis
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent.Action
import com.processout.sdk.ui.napm.NativeAlternativePaymentScreen.AnimationDurationMillis
import com.processout.sdk.ui.napm.NativeAlternativePaymentScreen.animatedBackgroundColor
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
        containerColor = animatedBackgroundColor(
            state = state,
            normalColor = style.normalBackgroundColor,
            successColor = style.successBackgroundColor
        ),
        topBar = {
            POHeader(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                title = if (state is UserInput) state.title else null,
                style = style.title,
                dividerColor = style.dividerColor,
                dragHandleColor = style.dragHandleColor,
                animationDurationMillis = AnimationDurationMillis
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = ProcessOutTheme.spacing.extraLarge,
                    vertical = ProcessOutTheme.spacing.large
                ),
            verticalArrangement = if (state is Capture) Arrangement.Top else Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state) {
                Loading -> Loading(style.progressIndicatorColor)
                is UserInput -> UserInput(state, onEvent, style)
                is Capture -> Capture(state, onEvent, style)
            }
        }
    }
}

@Composable
private fun Loading(progressIndicatorColor: Color) {
    AnimatedVisibility(enterDelayMillis = AnimationDurationMillis) {
        POCircularProgressIndicator.Medium(color = progressIndicatorColor)
    }
}

@Composable
private fun UserInput(
    state: UserInput,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    style: NativeAlternativePaymentScreen.Style
) {
    AnimatedVisibility {
        Column {
            // TODO
        }
    }
}

@Composable
private fun Capture(
    state: Capture,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    style: NativeAlternativePaymentScreen.Style
) {
    AnimatedVisibility(enterDelayMillis = AnimationDurationMillis) {
        Column {
            // TODO
            Image(
                painter = painterResource(id = R.drawable.po_scheme_elo),
                contentDescription = null
            )
            Crossfade(
                targetState = state.isCaptured,
                animationSpec = tween(durationMillis = AnimationDurationMillis, easing = LinearEasing)
            ) { isCaptured ->
                Column {
                    if (isCaptured) {
                        // TODO
                    } else {
                        // TODO
                    }
                }
            }
        }
    }
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
    POActionsContainer(
        actions = POImmutableList(
            if (style.axis == POAxis.Horizontal) actions.reversed() else actions
        ),
        onClick = { onEvent(Action(id = it)) },
        style = style,
        animationDurationMillis = AnimationDurationMillis
    )
}

@Composable
private fun AnimatedVisibility(
    visibleState: MutableTransitionState<Boolean> = remember {
        MutableTransitionState(initialState = false)
            .apply { targetState = true }
    },
    enterDelayMillis: Int = 0,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = AnimationDurationMillis,
                delayMillis = enterDelayMillis
            )
        ),
        exit = fadeOut(animationSpec = tween(durationMillis = AnimationDurationMillis))
    ) {
        content()
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

    val AnimationDurationMillis = 300

    @Composable
    fun animatedBackgroundColor(
        state: NativeAlternativePaymentViewModelState,
        normalColor: Color,
        successColor: Color
    ): Color = animateColorAsState(
        targetValue = when (state) {
            is Capture -> if (state.isCaptured) successColor else normalColor
            else -> normalColor
        },
        animationSpec = tween(durationMillis = AnimationDurationMillis, easing = LinearEasing)
    ).value
}
