@file:Suppress("AnimateAsStateLabel", "CrossfadeLabel", "MayBeConstant")

package com.processout.sdk.ui.napm.v2

import android.view.Gravity
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import coil.compose.AsyncImage
import com.processout.sdk.ui.R
import com.processout.sdk.ui.core.component.*
import com.processout.sdk.ui.core.component.field.POField
import com.processout.sdk.ui.core.component.field.checkbox.POCheckbox
import com.processout.sdk.ui.core.component.field.checkbox.POCheckboxField
import com.processout.sdk.ui.core.component.field.code.POCodeField
import com.processout.sdk.ui.core.component.field.code.POCodeField2
import com.processout.sdk.ui.core.component.field.dropdown.PODropdownField
import com.processout.sdk.ui.core.component.field.dropdown.PODropdownField2
import com.processout.sdk.ui.core.component.field.phone.POPhoneNumberField
import com.processout.sdk.ui.core.component.field.radio.PORadioField
import com.processout.sdk.ui.core.component.field.text.POTextField2
import com.processout.sdk.ui.core.component.stepper.POStepper
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.state.POPhoneNumberFieldState
import com.processout.sdk.ui.core.style.POAxis
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentEvent.*
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentScreen.AnimationDurationMillis
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentScreen.CaptureImageHeight
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentScreen.CaptureImageWidth
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentScreen.CaptureLogoHeight
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentScreen.CrossfadeAnimationDurationMillis
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentScreen.animatedBackgroundColor
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentScreen.messageGravity
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentViewModelState.*
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentViewModelState.Field.*
import com.processout.sdk.ui.shared.component.AndroidTextView
import com.processout.sdk.ui.shared.component.rememberLifecycleEvent
import com.processout.sdk.ui.shared.extension.dpToPx
import com.processout.sdk.ui.shared.state.FieldState
import com.processout.sdk.ui.shared.state.FieldValue

@Composable
internal fun NativeAlternativePaymentScreen(
    state: NativeAlternativePaymentViewModelState,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    onContentHeightChanged: (Int) -> Unit,
    style: NativeAlternativePaymentScreen.Style = NativeAlternativePaymentScreen.style()
) {
    var topBarHeight by remember { mutableIntStateOf(0) }
    var bottomBarHeight by remember { mutableIntStateOf(0) }
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
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .onGloballyPositioned {
                        topBarHeight = it.size.height
                    },
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
                dialogStyle = style.dialog,
                modifier = Modifier.onGloballyPositioned {
                    bottomBarHeight = it.size.height
                }
            )
        }
    ) { scaffoldPadding ->
        val verticalSpacing = ProcessOutTheme.spacing.extraLarge
        val verticalSpacingPx = verticalSpacing.dpToPx()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = ProcessOutTheme.spacing.extraLarge,
                    vertical = if (state is Capture) 0.dp else verticalSpacing
                ),
            verticalArrangement = if (state is Loading) Arrangement.Center else Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state) {
                is Loading -> Loading(style.progressIndicatorColor)
                is UserInput -> UserInput(
                    modifier = Modifier.onGloballyPositioned {
                        val contentHeight = it.size.height + topBarHeight + bottomBarHeight + verticalSpacingPx * 2
                        onContentHeightChanged(contentHeight)
                    },
                    state = state,
                    onEvent = onEvent,
                    style = style
                )
                is Capture -> Capture(state, onEvent, style)
            }
        }
    }
}

@Composable
private fun Loading(progressIndicatorColor: Color) {
    AnimatedVisibility(enterDelayMillis = AnimationDurationMillis) {
        POCircularProgressIndicator.Large(color = progressIndicatorColor)
    }
}

@Composable
private fun UserInput(
    state: UserInput,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    style: NativeAlternativePaymentScreen.Style,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(ProcessOutTheme.spacing.extraLarge)
        ) {
            val lifecycleEvent = rememberLifecycleEvent()
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
                        descriptionStyle = style.errorMessageBox,
                        modifier = Modifier.fillMaxWidth()
                    )
                    is CodeField -> CodeField(
                        state = field.state,
                        onEvent = onEvent,
                        lifecycleEvent = lifecycleEvent,
                        focusedFieldId = state.focusedFieldId,
                        isPrimaryActionEnabled = isPrimaryActionEnabled,
                        fieldStyle = style.codeField,
                        descriptionStyle = style.errorMessageBox,
                        modifier = Modifier.fillMaxWidth()
                    )
                    is RadioField -> RadioField(
                        state = field.state,
                        onEvent = onEvent,
                        fieldStyle = style.radioField,
                        descriptionStyle = style.errorMessageBox,
                        modifier = Modifier.fillMaxWidth()
                    )
                    is DropdownField -> DropdownField(
                        state = field.state,
                        onEvent = onEvent,
                        fieldStyle = style.field,
                        menuStyle = style.dropdownMenu,
                        descriptionStyle = style.errorMessageBox,
                        modifier = Modifier.fillMaxWidth()
                    )
                    is CheckboxField -> CheckboxField(
                        state = field.state,
                        onEvent = onEvent,
                        checkboxStyle = style.checkbox,
                        descriptionStyle = style.errorMessageBox,
                        modifier = Modifier.fillMaxWidth()
                    )
                    is PhoneNumberField -> PhoneNumberField(
                        state = field.state,
                        onEvent = onEvent,
                        lifecycleEvent = lifecycleEvent,
                        focusedFieldId = state.focusedFieldId,
                        isPrimaryActionEnabled = isPrimaryActionEnabled,
                        fieldStyle = style.field,
                        dropdownMenuStyle = style.dropdownMenu,
                        descriptionStyle = style.errorMessageBox,
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
    descriptionStyle: POMessageBox.Style,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    POTextField2(
        value = state.value,
        onValueChange = {
            onEvent(
                FieldValueChanged(
                    id = state.id,
                    value = FieldValue.Text(value = state.inputFilter?.filter(it) ?: it)
                )
            )
        },
        modifier = modifier,
        textFieldModifier = Modifier
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
        descriptionStyle = descriptionStyle,
        label = state.label,
        placeholder = state.placeholder,
        description = state.description,
        enabled = state.enabled,
        isError = state.isError,
        forceTextDirectionLtr = state.forceTextDirectionLtr,
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
    descriptionStyle: POMessageBox.Style,
    modifier: Modifier = Modifier
) {
    POCodeField2(
        value = state.value,
        onValueChange = {
            onEvent(
                FieldValueChanged(
                    id = state.id,
                    value = FieldValue.Text(value = it)
                )
            )
        },
        modifier = modifier,
        textFieldModifier = Modifier
            .onFocusChanged {
                onEvent(
                    FieldFocusChanged(
                        id = state.id,
                        isFocused = it.isFocused
                    )
                )
            },
        fieldStyle = fieldStyle,
        descriptionStyle = descriptionStyle,
        length = state.length ?: POCodeField.LengthMax,
        label = state.label,
        description = state.description,
        enabled = state.enabled,
        isError = state.isError,
        isFocused = state.id == focusedFieldId,
        lifecycleEvent = lifecycleEvent,
        inputFilter = state.inputFilter,
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
    fieldStyle: PORadioField.Style,
    descriptionStyle: POMessageBox.Style,
    modifier: Modifier = Modifier
) {
    PORadioField(
        value = state.value,
        onValueChange = {
            onEvent(
                FieldValueChanged(
                    id = state.id,
                    value = FieldValue.Text(value = it)
                )
            )
        },
        availableValues = state.availableValues ?: POImmutableList(emptyList()),
        modifier = modifier,
        fieldStyle = fieldStyle,
        descriptionStyle = descriptionStyle,
        title = state.label,
        description = state.description,
        isError = state.isError
    )
}

@Composable
private fun DropdownField(
    state: FieldState,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    fieldStyle: POField.Style,
    menuStyle: PODropdownField.MenuStyle,
    descriptionStyle: POMessageBox.Style,
    modifier: Modifier = Modifier
) {
    PODropdownField2(
        value = state.value,
        onValueChange = {
            onEvent(
                FieldValueChanged(
                    id = state.id,
                    value = FieldValue.Text(value = it)
                )
            )
        },
        availableValues = state.availableValues ?: POImmutableList(emptyList()),
        modifier = modifier,
        textFieldModifier = Modifier
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
        descriptionStyle = descriptionStyle,
        isError = state.isError,
        label = state.label,
        placeholder = state.placeholder,
        description = state.description
    )
}

@Composable
private fun CheckboxField(
    state: FieldState,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    checkboxStyle: POCheckbox.Style,
    descriptionStyle: POMessageBox.Style,
    modifier: Modifier = Modifier
) {
    POCheckboxField(
        text = state.label ?: String(),
        checked = state.value.text.toBooleanStrictOrNull() ?: false,
        onCheckedChange = {
            onEvent(
                FieldValueChanged(
                    id = state.id,
                    value = FieldValue.Text(
                        value = TextFieldValue(text = it.toString())
                    )
                )
            )
        },
        modifier = modifier,
        checkboxStyle = checkboxStyle,
        descriptionStyle = descriptionStyle,
        isError = state.isError,
        description = state.description
    )
}

@Composable
private fun PhoneNumberField(
    state: POPhoneNumberFieldState,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    lifecycleEvent: Lifecycle.Event,
    focusedFieldId: String?,
    isPrimaryActionEnabled: Boolean,
    fieldStyle: POField.Style,
    dropdownMenuStyle: PODropdownField.MenuStyle,
    descriptionStyle: POMessageBox.Style,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    POPhoneNumberField(
        state = state,
        onValueChange = { regionCode, number ->
            onEvent(
                FieldValueChanged(
                    id = state.id,
                    value = FieldValue.PhoneNumber(
                        regionCode = regionCode,
                        number = number
                    )
                )
            )
        },
        modifier = modifier,
        textFieldModifier = Modifier
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
        dropdownMenuStyle = dropdownMenuStyle,
        descriptionStyle = descriptionStyle,
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
private fun Capture(
    state: Capture,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    style: NativeAlternativePaymentScreen.Style
) {
    AnimatedVisibility(enterDelayMillis = AnimationDurationMillis) {
        Column(
            modifier = Modifier.padding(
                top = ProcessOutTheme.spacing.large,
                bottom = ProcessOutTheme.spacing.extraLarge
            ),
            verticalArrangement = Arrangement.spacedBy(ProcessOutTheme.spacing.extraLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CaptureHeader(state, style)
            Crossfade(
                targetState = state.isCaptured,
                animationSpec = tween(
                    durationMillis = CrossfadeAnimationDurationMillis,
                    easing = LinearEasing
                )
            ) { isCaptured ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(ProcessOutTheme.spacing.extraLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isCaptured) {
                        SuccessContent(state, style)
                    } else {
                        CaptureContent(state, onEvent, style)
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
            modifier = Modifier.requiredHeight(CaptureLogoHeight),
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
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    style: NativeAlternativePaymentScreen.Style
) {
    if (state.withProgressIndicator) {
        AnimatedProgressIndicator(style.progressIndicatorColor)
    }
    AndroidTextView(
        text = state.message,
        style = style.message,
        modifier = Modifier.fillMaxWidth(),
        gravity = messageGravity(state.message),
        selectable = true,
        linksClickable = true
    )
    var showImage by remember { mutableStateOf(state.image != null) }
    if (showImage) {
        when (state.image) {
            is Image.Url -> AsyncImage(
                model = state.image.value,
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
            is Image.Bitmap -> {
                val bitmap = state.image.value
                Image(
                    bitmap = remember(bitmap) { bitmap.asImageBitmap() },
                    contentDescription = null,
                    modifier = Modifier.requiredSize(
                        width = CaptureImageWidth,
                        height = CaptureImageHeight
                    ),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Fit
                )
            }
            else -> {}
        }
    }
    state.confirmationDialog?.let {
        PODialog(
            title = it.title,
            message = it.message,
            confirmActionText = it.confirmActionText,
            dismissActionText = it.dismissActionText,
            onConfirm = { onEvent(DialogAction(id = it.id, isConfirmed = true)) },
            onDismiss = { onEvent(DialogAction(id = it.id, isConfirmed = false)) },
            style = style.dialog
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
        POCircularProgressIndicator.Large(color = progressIndicatorColor)
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
    dialogStyle: PODialog.Style,
    modifier: Modifier = Modifier
) {
    var primary: POActionState? = null
    var secondary: POActionState? = null
    var saveBarcode: POActionState? = null
    when (state) {
        is Loading -> secondary = state.secondaryAction
        is UserInput -> {
            primary = state.primaryAction
            secondary = state.secondaryAction
        }
        is Capture -> {
            primary = state.primaryAction
            secondary = state.secondaryAction
            saveBarcode = state.saveBarcodeAction
        }
    }
    val actions = listOfNotNull(primary, saveBarcode, secondary)
    POActionsContainer(
        modifier = modifier,
        actions = POImmutableList(
            if (containerStyle.axis == POAxis.Horizontal) actions.reversed() else actions
        ),
        onClick = { onEvent(Action(id = it)) },
        onConfirmationRequested = { onEvent(ActionConfirmationRequested(id = it)) },
        containerStyle = containerStyle,
        confirmationDialogStyle = dialogStyle,
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
        val field: POField.Style,
        val codeField: POField.Style,
        val radioField: PORadioField.Style,
        val checkbox: POCheckbox.Style,
        val dropdownMenu: PODropdownField.MenuStyle,
        val dialog: PODialog.Style,
        val stepper: POStepper.Style,
        val actionsContainer: POActionsContainer.Style,
        val normalBackgroundColor: Color,
        val successBackgroundColor: Color,
        val message: AndroidTextView.Style,
        val errorMessageBox: POMessageBox.Style,
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
                field = custom?.field?.let {
                    POField.custom(style = it)
                } ?: POField.default2,
                codeField = custom?.codeField?.let {
                    POField.custom(style = it)
                } ?: POCodeField.default2,
                radioField = custom?.radioField?.let {
                    PORadioField.custom(style = it)
                } ?: PORadioField.default,
                checkbox = custom?.checkbox?.let {
                    POCheckbox.custom(style = it)
                } ?: POCheckbox.default2,
                dropdownMenu = custom?.dropdownMenu?.let {
                    PODropdownField.custom(style = it)
                } ?: PODropdownField.defaultMenu2,
                dialog = custom?.dialog?.let {
                    PODialog.custom(style = it)
                } ?: PODialog.default,
                stepper = custom?.stepper?.let {
                    POStepper.custom(style = it)
                } ?: POStepper.default,
                actionsContainer = custom?.actionsContainer?.let {
                    POActionsContainer.custom(style = it)
                } ?: POActionsContainer.default2,
                normalBackgroundColor = custom?.background?.normalColorResId?.let {
                    colorResource(id = it)
                } ?: colors.surface.default,
                successBackgroundColor = custom?.background?.successColorResId?.let {
                    colorResource(id = it)
                } ?: colors.surface.success,
                message = custom?.message?.let { style ->
                    val controlsTintColor = custom.controlsTintColorResId?.let { colorResource(id = it) }
                    AndroidTextView.custom(
                        style = style,
                        controlsTintColor = controlsTintColor ?: colors.text.primary
                    )
                } ?: AndroidTextView.default,
                errorMessageBox = custom?.errorMessageBox?.let {
                    POMessageBox.custom(style = it)
                } ?: POMessageBox.error2,
                successMessage = custom?.successMessage?.let {
                    POText.custom(style = it)
                } ?: POText.Style(
                    color = colors.text.success,
                    textStyle = typography.body1
                ),
                successImageResId = custom?.successImageResId ?: R.drawable.po_success_image,
                progressIndicatorColor = custom?.progressIndicatorColorResId?.let {
                    colorResource(id = it)
                } ?: colors.button.primaryBackgroundDefault,
                dividerColor = custom?.dividerColorResId?.let {
                    colorResource(id = it)
                } ?: colors.border.subtle,
                dragHandleColor = custom?.dragHandleColorResId?.let {
                    colorResource(id = it)
                } ?: colors.border.subtle
            )
        }

    val CaptureLogoHeight = 34.dp

    val CaptureImageWidth = 220.dp
    val CaptureImageHeight = 280.dp

    val AnimationDurationMillis = 300
    val CrossfadeAnimationDurationMillis = 400

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
        animationSpec = tween(
            durationMillis = CrossfadeAnimationDurationMillis,
            easing = LinearEasing
        )
    ).value

    private val ShortMessageMaxLength = 150

    fun messageGravity(text: String): Int =
        if (text.length <= ShortMessageMaxLength)
            Gravity.CENTER_HORIZONTAL else Gravity.START
}
