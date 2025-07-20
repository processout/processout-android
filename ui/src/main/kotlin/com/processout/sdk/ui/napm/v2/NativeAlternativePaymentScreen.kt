@file:Suppress("AnimateAsStateLabel", "CrossfadeLabel", "MayBeConstant")

package com.processout.sdk.ui.napm.v2

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import coil.compose.AsyncImage
import com.processout.sdk.api.model.response.POImageResource
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
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentEvent.*
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentScreen.AnimationDurationMillis
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentScreen.LogoHeight
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentViewModelState.*
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentViewModelState.Element.*
import com.processout.sdk.ui.shared.component.AndroidTextView
import com.processout.sdk.ui.shared.component.rememberLifecycleEvent
import com.processout.sdk.ui.shared.extension.conditional
import com.processout.sdk.ui.shared.extension.dpToPx
import com.processout.sdk.ui.shared.state.FieldState
import com.processout.sdk.ui.shared.state.FieldValue

@Composable
internal fun NativeAlternativePaymentScreen(
    state: NativeAlternativePaymentViewModelState,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    onContentHeightChanged: (Int) -> Unit,
    isLightTheme: Boolean,
    style: NativeAlternativePaymentScreen.Style = NativeAlternativePaymentScreen.style()
) {
    var topBarHeight by remember { mutableIntStateOf(0) }
    var bottomBarHeight by remember { mutableIntStateOf(0) }
    Scaffold(
        modifier = Modifier
            .nestedScroll(rememberNestedScrollInteropConnection())
            .clip(shape = shapes.topRoundedCornersLarge),
        containerColor = style.normalBackgroundColor,
        topBar = {
            Header(
                logo = if (state is Loaded) state.logo else null,
                title = if (state is Loaded) state.title else null,
                titleStyle = style.title,
                dividerColor = style.dividerColor,
                dragHandleColor = style.dragHandleColor,
                isLightTheme = isLightTheme,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .onGloballyPositioned {
                        topBarHeight = it.size.height
                    }
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
        val verticalSpacing = spacing.space20
        val verticalSpacingPx = verticalSpacing.dpToPx()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = spacing.extraLarge,
                    vertical = verticalSpacing
                ),
            verticalArrangement = if (state is Loading) Arrangement.Center else Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state) {
                is Loading -> Loading(style.progressIndicatorColor)
                is Loaded -> Loaded(
                    content = state.content,
                    onEvent = onEvent,
                    style = style,
                    isPrimaryActionEnabled = state.primaryAction?.let { it.enabled && !it.loading } ?: false,
                    isLightTheme = isLightTheme,
                    modifier = Modifier.onGloballyPositioned {
                        val contentHeight = it.size.height + topBarHeight + bottomBarHeight + verticalSpacingPx * 2
                        onContentHeightChanged(contentHeight)
                    }
                )
            }
        }
    }
}

@Composable
private fun Header(
    logo: POImageResource?,
    title: String?,
    titleStyle: POText.Style,
    dividerColor: Color,
    dragHandleColor: Color,
    isLightTheme: Boolean,
    modifier: Modifier = Modifier,
    withDragHandle: Boolean = true,
    animationDurationMillis: Int = AnimationDurationMillis
) {
    Box(modifier = modifier.fillMaxWidth()) {
        if (withDragHandle) {
            PODragHandle(
                modifier = Modifier
                    .padding(top = spacing.space10)
                    .align(alignment = Alignment.TopCenter),
                color = dragHandleColor
            )
        }
        AnimatedVisibility(
            visible = logo != null || !title.isNullOrBlank(),
            enter = fadeIn(animationSpec = tween(durationMillis = animationDurationMillis)),
            exit = fadeOut(animationSpec = tween(durationMillis = animationDurationMillis)),
        ) {
            Column(
                modifier = Modifier.conditional(
                    condition = withDragHandle,
                    whenTrue = { padding(top = spacing.space20) },
                    whenFalse = { padding(top = spacing.space12) }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = spacing.space20,
                            end = spacing.space20,
                            bottom = spacing.space12
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val logoUrl = logo?.let {
                        if (isLightTheme) {
                            it.lightUrl.raster
                        } else {
                            it.darkUrl?.raster ?: it.lightUrl.raster
                        }
                    }
                    AsyncImage(
                        model = logoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(0.8f, fill = false)
                            .requiredHeight(LogoHeight),
                        contentScale = ContentScale.FillHeight
                    )
                    if (title != null) {
                        POText(
                            text = title,
                            modifier = Modifier.weight(1f, fill = false),
                            color = titleStyle.color,
                            style = titleStyle.textStyle
                        )
                    }
                }
                HorizontalDivider(thickness = 1.dp, color = dividerColor)
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
private fun Loaded(
    content: Content,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    style: NativeAlternativePaymentScreen.Style,
    isPrimaryActionEnabled: Boolean,
    isLightTheme: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(spacing.space16)
        ) {
            Elements(
                elements = when (content) {
                    is Content.NextStep -> content.elements
                    is Content.Pending -> content.elements
                    is Content.Completed -> content.elements
                },
                onEvent = onEvent,
                style = style,
                focusedFieldId = if (content is Content.NextStep) content.focusedFieldId else null,
                isPrimaryActionEnabled = isPrimaryActionEnabled,
                isLightTheme = isLightTheme
            )
        }
    }
}

@Composable
private fun Elements(
    elements: POImmutableList<Element>,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    style: NativeAlternativePaymentScreen.Style,
    focusedFieldId: String?,
    isPrimaryActionEnabled: Boolean,
    isLightTheme: Boolean
) {
    val lifecycleEvent = rememberLifecycleEvent()
    elements.elements.forEach { element ->
        when (element) {
            is TextField -> TextField(
                state = element.state,
                onEvent = onEvent,
                lifecycleEvent = lifecycleEvent,
                focusedFieldId = focusedFieldId,
                isPrimaryActionEnabled = isPrimaryActionEnabled,
                fieldStyle = style.field,
                descriptionStyle = style.errorMessageBox,
                modifier = Modifier.fillMaxWidth()
            )
            is CodeField -> CodeField(
                state = element.state,
                onEvent = onEvent,
                lifecycleEvent = lifecycleEvent,
                focusedFieldId = focusedFieldId,
                isPrimaryActionEnabled = isPrimaryActionEnabled,
                fieldStyle = style.codeField,
                descriptionStyle = style.errorMessageBox,
                modifier = Modifier.fillMaxWidth()
            )
            is RadioField -> RadioField(
                state = element.state,
                onEvent = onEvent,
                fieldStyle = style.radioField,
                descriptionStyle = style.errorMessageBox,
                modifier = Modifier.fillMaxWidth()
            )
            is DropdownField -> DropdownField(
                state = element.state,
                onEvent = onEvent,
                fieldStyle = style.field,
                menuStyle = style.dropdownMenu,
                descriptionStyle = style.errorMessageBox,
                modifier = Modifier.fillMaxWidth()
            )
            is CheckboxField -> CheckboxField(
                state = element.state,
                onEvent = onEvent,
                checkboxStyle = style.checkbox,
                descriptionStyle = style.errorMessageBox,
                modifier = Modifier.fillMaxWidth()
            )
            is PhoneNumberField -> PhoneNumberField(
                state = element.state,
                onEvent = onEvent,
                lifecycleEvent = lifecycleEvent,
                focusedFieldId = focusedFieldId,
                isPrimaryActionEnabled = isPrimaryActionEnabled,
                fieldStyle = style.field,
                dropdownMenuStyle = style.dropdownMenu,
                descriptionStyle = style.errorMessageBox,
                modifier = Modifier.fillMaxWidth()
            )
            is InstructionMessage -> AndroidTextView(
                text = element.value,
                style = style.bodyText,
                modifier = Modifier.fillMaxWidth(),
                selectable = true,
                linksClickable = true
            )
            is Image -> Image(
                image = element,
                isLightTheme = isLightTheme
            )
            is Barcode -> Barcode(
                barcode = element,
                onEvent = onEvent,
                style = style
            )
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
private fun Image(
    image: Image,
    isLightTheme: Boolean
) {
    var showImage by remember { mutableStateOf(true) }
    if (showImage) {
        Box(
            modifier = Modifier
                .padding(horizontal = spacing.space48)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val imageUrl = image.value.let {
                if (isLightTheme) {
                    it.lightUrl.raster
                } else {
                    it.darkUrl?.raster ?: it.lightUrl.raster
                }
            }
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                alignment = Alignment.Center,
                contentScale = ContentScale.Fit,
                onError = {
                    showImage = false
                }
            )
        }
    }
}

@Composable
private fun Barcode(
    barcode: Barcode,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    style: NativeAlternativePaymentScreen.Style
) {
    Column(
        modifier = Modifier.padding(horizontal = spacing.space48),
        verticalArrangement = Arrangement.spacedBy(space = spacing.space8),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val bitmap = barcode.image
        Image(
            bitmap = remember(bitmap) { bitmap.asImageBitmap() },
            contentDescription = null,
            alignment = Alignment.Center,
            contentScale = ContentScale.Fit
        )
        barcode.saveBarcodeAction.let {
            POButton(
                text = it.text,
                onClick = { onEvent(Action(id = it.id)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeightIn(min = 40.dp),
                style = style.actionsContainer.secondary,
                icon = it.icon
            )
        }
    }
    barcode.confirmationDialog?.let {
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
private fun Actions(
    state: NativeAlternativePaymentViewModelState,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    containerStyle: POActionsContainer.Style,
    dialogStyle: PODialog.Style,
    modifier: Modifier = Modifier
) {
    var primary: POActionState? = null
    var secondary: POActionState? = null
    when (state) {
        is Loading -> secondary = state.secondaryAction
        is Loaded -> {
            primary = state.primaryAction
            secondary = state.secondaryAction
        }
    }
    val actions = listOfNotNull(primary, secondary)
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
        val bodyText: AndroidTextView.Style,
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
                } ?: POText.Style(
                    color = colors.text.primary,
                    textStyle = typography.s20(FontWeight.Medium)
                ),
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
                bodyText = custom?.bodyText?.let { style ->
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
                } ?: colors.border.border4,
                dragHandleColor = custom?.dragHandleColorResId?.let {
                    colorResource(id = it)
                } ?: colors.icon.disabled
            )
        }

    val LogoHeight = 26.dp

    val AnimationDurationMillis = 300
    val CrossfadeAnimationDurationMillis = 400
}
