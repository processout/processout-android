@file:Suppress("AnimateAsStateLabel", "CrossfadeLabel", "MayBeConstant")

package com.processout.sdk.ui.napm

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import coil.compose.AsyncImage
import com.processout.sdk.R
import com.processout.sdk.ui.core.component.*
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.POFieldLabels
import com.processout.sdk.ui.core.component.field.code.POCodeField
import com.processout.sdk.ui.core.component.field.code.POLabeledCodeField
import com.processout.sdk.ui.core.component.field.dropdown.PODropdownField
import com.processout.sdk.ui.core.component.field.dropdown.POLabeledDropdownField
import com.processout.sdk.ui.core.component.field.radio.POLabeledRadioField
import com.processout.sdk.ui.core.component.field.radio.PORadioGroup
import com.processout.sdk.ui.core.component.field.text.POLabeledTextField
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.style.POAxis
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentScreen.AnimationDurationMillis
import com.processout.sdk.ui.napm.NativeAlternativePaymentScreen.CaptureImageHeight
import com.processout.sdk.ui.napm.NativeAlternativePaymentScreen.CaptureImageWidth
import com.processout.sdk.ui.napm.NativeAlternativePaymentScreen.animatedBackgroundColor
import com.processout.sdk.ui.napm.NativeAlternativePaymentScreen.codeFieldHorizontalAlignment
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.Field.*
import com.processout.sdk.ui.shared.component.TextAndroidView
import com.processout.sdk.ui.shared.component.rememberLifecycleEvent
import com.processout.sdk.ui.shared.state.FieldState

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
                containerStyle = style.actionsContainer,
                dialogStyle = style.dialog
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
                    vertical = if (state is Capture) 0.dp else ProcessOutTheme.spacing.large
                ),
            verticalArrangement = if (state is Capture) Arrangement.Top else Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state) {
                is Loading -> Loading(style.progressIndicatorColor)
                is UserInput -> UserInput(state, onEvent, style)
                is Capture -> Capture(state, style)
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
        Column(
            verticalArrangement = Arrangement.spacedBy(ProcessOutTheme.spacing.small)
        ) {
            val lifecycleEvent = rememberLifecycleEvent()
            val labelsStyle = remember {
                POFieldLabels.Style(
                    title = style.label,
                    description = style.errorMessage
                )
            }
            val isPrimaryActionEnabled = with(state.primaryAction) { enabled && !loading }
            state.fields.elements.forEach { field ->
                when (field) {
                    is TextField -> TextField(
                        state = field.state,
                        onEvent = onEvent,
                        lifecycleEvent = lifecycleEvent,
                        focusedFieldId = state.focusedFieldId,
                        isPrimaryActionEnabled = isPrimaryActionEnabled,
                        fieldStyle = style.field,
                        labelsStyle = labelsStyle,
                        modifier = Modifier.fillMaxWidth()
                    )
                    is CodeField -> CodeField(
                        state = field.state,
                        onEvent = onEvent,
                        lifecycleEvent = lifecycleEvent,
                        focusedFieldId = state.focusedFieldId,
                        isPrimaryActionEnabled = isPrimaryActionEnabled,
                        fieldStyle = style.codeField,
                        labelsStyle = labelsStyle,
                        horizontalAlignment = codeFieldHorizontalAlignment(state.fields.elements)
                    )
                    is RadioField -> RadioField(
                        state = field.state,
                        onEvent = onEvent,
                        radioGroupStyle = style.radioGroup,
                        labelsStyle = labelsStyle
                    )
                    is DropdownField -> DropdownField(
                        state = field.state,
                        onEvent = onEvent,
                        fieldStyle = style.field,
                        menuStyle = style.dropdownMenu,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun TextField(
    state: FieldState,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    lifecycleEvent: Lifecycle.Event,
    focusedFieldId: String?,
    isPrimaryActionEnabled: Boolean,
    fieldStyle: POField.Style,
    labelsStyle: POFieldLabels.Style,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    POLabeledTextField(
        value = state.value,
        onValueChange = {
            onEvent(
                FieldValueChanged(
                    id = state.id,
                    value = state.inputFilter?.filter(it) ?: it
                )
            )
        },
        title = state.title ?: String(),
        description = state.description,
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                onEvent(
                    FieldFocusChanged(
                        id = state.id,
                        isFocused = it.isFocused
                    )
                )
            },
        fieldStyle = fieldStyle,
        labelsStyle = labelsStyle,
        enabled = state.enabled,
        isError = state.isError,
        forceTextDirectionLtr = state.forceTextDirectionLtr,
        placeholderText = state.placeholder,
        visualTransformation = state.visualTransformation,
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

@Composable
private fun CodeField(
    state: FieldState,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    lifecycleEvent: Lifecycle.Event,
    focusedFieldId: String?,
    isPrimaryActionEnabled: Boolean,
    fieldStyle: POField.Style,
    labelsStyle: POFieldLabels.Style,
    horizontalAlignment: Alignment.Horizontal,
    modifier: Modifier = Modifier
) {
    POLabeledCodeField(
        value = state.value,
        onValueChange = {
            onEvent(
                FieldValueChanged(
                    id = state.id,
                    value = it
                )
            )
        },
        title = state.title ?: String(),
        description = state.description,
        modifier = modifier
            .onFocusChanged {
                onEvent(
                    FieldFocusChanged(
                        id = state.id,
                        isFocused = it.isFocused
                    )
                )
            },
        fieldStyle = fieldStyle,
        labelsStyle = labelsStyle,
        length = state.length ?: POCodeField.LengthMax,
        horizontalAlignment = horizontalAlignment,
        enabled = state.enabled,
        isError = state.isError,
        isFocused = state.id == focusedFieldId,
        lifecycleEvent = lifecycleEvent,
        keyboardOptions = state.keyboardOptions,
        keyboardActions = POField.keyboardActions(
            imeAction = state.keyboardOptions.imeAction,
            actionId = state.keyboardActionId,
            enabled = isPrimaryActionEnabled,
            onClick = { onEvent(Action(id = it)) }
        )
    )
}

@Composable
private fun RadioField(
    state: FieldState,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    radioGroupStyle: PORadioGroup.Style,
    labelsStyle: POFieldLabels.Style,
    modifier: Modifier = Modifier
) {
    POLabeledRadioField(
        value = state.value,
        onValueChange = {
            onEvent(
                FieldValueChanged(
                    id = state.id,
                    value = it
                )
            )
        },
        availableValues = state.availableValues ?: POImmutableList(emptyList()),
        title = state.title ?: String(),
        description = state.description,
        modifier = modifier,
        radioGroupStyle = radioGroupStyle,
        labelsStyle = labelsStyle,
        isError = state.isError
    )
}

@Composable
private fun DropdownField(
    state: FieldState,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    fieldStyle: POField.Style,
    menuStyle: PODropdownField.MenuStyle,
    modifier: Modifier = Modifier
) {
    POLabeledDropdownField(
        value = state.value,
        onValueChange = {
            onEvent(
                FieldValueChanged(
                    id = state.id,
                    value = it
                )
            )
        },
        availableValues = state.availableValues ?: POImmutableList(emptyList()),
        title = state.title ?: String(),
        description = state.description,
        modifier = modifier
            .onFocusChanged {
                onEvent(
                    FieldFocusChanged(
                        id = state.id,
                        isFocused = it.isFocused
                    )
                )
            },
        fieldStyle = fieldStyle,
        menuStyle = menuStyle,
        isError = state.isError,
        placeholderText = state.placeholder
    )
}

@Composable
private fun Capture(
    state: Capture,
    style: NativeAlternativePaymentScreen.Style
) {
    AnimatedVisibility(enterDelayMillis = AnimationDurationMillis) {
        Column(
            modifier = Modifier.padding(
                top = ProcessOutTheme.spacing.extraSmall,
                bottom = ProcessOutTheme.spacing.extraLarge
            ),
            verticalArrangement = Arrangement.spacedBy(ProcessOutTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CaptureHeader(state, style)
            Crossfade(
                targetState = state.isCaptured,
                animationSpec = tween(durationMillis = AnimationDurationMillis, easing = LinearEasing)
            ) { isCaptured ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(ProcessOutTheme.spacing.large),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isCaptured) {
                        SuccessContent(state, style)
                    } else {
                        CaptureContent(state, style)
                    }
                }
            }
        }
    }
}

@Composable
private fun CaptureHeader(
    state: Capture,
    style: NativeAlternativePaymentScreen.Style
) {
    var showLogo by remember { mutableStateOf(true) }
    if (showLogo) {
        AsyncImage(
            model = state.logoUrl,
            contentDescription = null,
            modifier = Modifier.requiredHeight(34.dp),
            contentScale = ContentScale.FillHeight,
            onError = {
                showLogo = false
            }
        )
    } else if (state.title != null) {
        POText(
            text = state.title,
            color = style.title.color,
            style = style.title.textStyle
        )
    }
}

@Composable
private fun CaptureContent(
    state: Capture,
    style: NativeAlternativePaymentScreen.Style
) {
    if (state.withProgressIndicator) {
        AnimatedProgressIndicator(style.progressIndicatorColor)
    }
    TextAndroidView(
        text = state.message,
        style = style.message,
        modifier = Modifier.fillMaxWidth(),
        selectable = true,
        linksClickable = true
    )
    var showImage by remember { mutableStateOf(true) }
    if (showImage) {
        AsyncImage(
            model = state.imageUrl,
            contentDescription = null,
            modifier = Modifier.requiredSize(
                width = CaptureImageWidth,
                height = CaptureImageHeight
            ),
            alignment = Alignment.Center,
            contentScale = ContentScale.Fit,
            onError = {
                showImage = false
            }
        )
    }
}

@Composable
private fun AnimatedProgressIndicator(
    progressIndicatorColor: Color
) {
    AnimatedVisibility(
        visibleState = remember {
            MutableTransitionState(initialState = false)
                .apply { targetState = true }
        },
        enter = expandVertically() + fadeIn(animationSpec = tween(durationMillis = AnimationDurationMillis)),
        exit = shrinkVertically() + fadeOut(animationSpec = tween(durationMillis = AnimationDurationMillis))
    ) {
        POCircularProgressIndicator.Medium(color = progressIndicatorColor)
    }
}

@Composable
private fun SuccessContent(
    state: Capture,
    style: NativeAlternativePaymentScreen.Style
) {
    POText(
        text = state.message,
        modifier = Modifier.fillMaxWidth(),
        color = style.successMessage.color,
        style = style.successMessage.textStyle,
        textAlign = TextAlign.Center
    )
    Image(
        painter = painterResource(id = style.successImageResId),
        contentDescription = null,
        modifier = Modifier.requiredSize(
            width = CaptureImageWidth,
            height = CaptureImageHeight
        ),
        alignment = Alignment.Center,
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun Actions(
    state: NativeAlternativePaymentViewModelState,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    containerStyle: POActionsContainer.Style,
    dialogStyle: PODialog.Style
) {
    var primary: POActionState? = null
    var secondary: POActionState? = null
    when (state) {
        is Loading -> secondary = state.secondaryAction
        is UserInput -> {
            primary = state.primaryAction
            secondary = state.secondaryAction
        }
        is Capture -> secondary = state.secondaryAction
    }
    val actions = mutableListOf<POActionState>()
    primary?.let { actions.add(it) }
    secondary?.let { actions.add(it) }
    POActionsContainer(
        actions = POImmutableList(
            if (containerStyle.axis == POAxis.Horizontal) actions.reversed() else actions
        ),
        onClick = { onEvent(Action(id = it)) },
        containerStyle = containerStyle,
        dialogStyle = dialogStyle,
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
        val dialog: PODialog.Style,
        val normalBackgroundColor: Color,
        val successBackgroundColor: Color,
        val message: TextAndroidView.Style,
        val errorMessage: POText.Style,
        val successMessage: POText.Style,
        @DrawableRes val successImageResId: Int,
        val progressIndicatorColor: Color,
        val dividerColor: Color,
        val dragHandleColor: Color
    )

    @Composable
    fun style(custom: PONativeAlternativePaymentConfiguration.Style? = null) =
        with(ProcessOutTheme) {
            Style(
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
                dialog = custom?.dialog?.let {
                    PODialog.custom(style = it)
                } ?: PODialog.default,
                normalBackgroundColor = custom?.background?.normalColorResId?.let {
                    colorResource(id = it)
                } ?: colors.surface.level1,
                successBackgroundColor = custom?.background?.successColorResId?.let {
                    colorResource(id = it)
                } ?: colors.surface.success,
                message = custom?.message?.let { style ->
                    val controlsTintColor = custom.controlsTintColorResId?.let { colorResource(id = it) }
                    TextAndroidView.custom(
                        style = style,
                        controlsTintColor = controlsTintColor ?: colors.text.primary
                    )
                } ?: TextAndroidView.default,
                errorMessage = custom?.errorMessage?.let {
                    POText.custom(style = it)
                } ?: POText.errorLabel,
                successMessage = custom?.successMessage?.let {
                    POText.custom(style = it)
                } ?: POText.Style(
                    color = colors.text.success,
                    textStyle = typography.fixed.body
                ),
                successImageResId = custom?.successImageResId ?: R.drawable.po_success_image,
                progressIndicatorColor = custom?.progressIndicatorColorResId?.let {
                    colorResource(id = it)
                } ?: colors.action.primaryDefault,
                dividerColor = custom?.dividerColorResId?.let {
                    colorResource(id = it)
                } ?: colors.border.subtle,
                dragHandleColor = custom?.dragHandleColorResId?.let {
                    colorResource(id = it)
                } ?: colors.border.disabled
            )
        }

    val CaptureImageWidth = 220.dp
    val CaptureImageHeight = 280.dp

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

    fun codeFieldHorizontalAlignment(fields: List<Field>): Alignment.Horizontal =
        if (fields.size == 1 && fields[0] is CodeField)
            Alignment.CenterHorizontally else Alignment.Start
}
