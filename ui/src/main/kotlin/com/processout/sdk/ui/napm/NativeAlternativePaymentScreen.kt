@file:Suppress("NAME_SHADOWING", "MayBeConstant")

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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
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
import com.processout.sdk.ui.core.component.stepper.POVerticalStepper
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.state.POPhoneNumberFieldState
import com.processout.sdk.ui.core.style.POAxis
import com.processout.sdk.ui.core.style.POLabeledContentStyle
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.core.theme.ProcessOutTheme.colors
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.shapes
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.core.theme.ProcessOutTheme.typography
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentScreen.AnimationDurationMillis
import com.processout.sdk.ui.napm.NativeAlternativePaymentScreen.ContentTransitionSpec
import com.processout.sdk.ui.napm.NativeAlternativePaymentScreen.LabeledContentStyle
import com.processout.sdk.ui.napm.NativeAlternativePaymentScreen.LogoHeight
import com.processout.sdk.ui.napm.NativeAlternativePaymentScreen.SuccessStyle
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.Element.*
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
    if (state is Loaded) {
        when (state.content.stage) {
            is Stage.Pending,
            is Stage.Completed -> LocalFocusManager.current.clearFocus(force = true)
            else -> {}
        }
    }
    var topBarHeight by remember { mutableIntStateOf(0) }
    var bottomBarHeight by remember { mutableIntStateOf(0) }
    Scaffold(
        modifier = Modifier
            .nestedScroll(rememberNestedScrollInteropConnection())
            .clip(shape = shapes.topRoundedCornersLarge),
        containerColor = style.backgroundColor,
        topBar = {
            Header(
                logo = if (state is Loaded) state.header?.logo else null,
                title = if (state is Loaded) state.header?.title else null,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .verticalScroll(rememberScrollState())
                .padding(spacing.space20),
            verticalArrangement = if (state is Loading) Arrangement.Center else Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val adjustedContentHeight = 90.dp.dpToPx()
            when (state) {
                is Loading -> POCircularProgressIndicator.Large(
                    color = style.progressIndicatorColor
                )
                is Loaded -> AnimatedVisibility {
                    Loaded(
                        content = state.content,
                        onEvent = onEvent,
                        style = style,
                        isPrimaryActionEnabled = state.primaryAction?.let { it.enabled && !it.loading } ?: false,
                        isLightTheme = isLightTheme,
                        modifier = Modifier.onGloballyPositioned {
                            val contentHeight = it.size.height + topBarHeight + bottomBarHeight + adjustedContentHeight
                            onContentHeightChanged(contentHeight)
                        }
                    )
                }
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
    withDragHandle: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        if (withDragHandle) {
            PODragHandle(
                modifier = Modifier
                    .padding(top = spacing.space10)
                    .align(alignment = Alignment.TopCenter),
                color = dragHandleColor
            )
        }
        var showLogo by remember { mutableStateOf(true) }
        val logoUrl = logo?.let {
            if (isLightTheme) {
                it.lightUrl.raster
            } else {
                it.darkUrl?.raster ?: it.lightUrl.raster
            }
        }
        AnimatedVisibility(
            visible = showLogo && !logoUrl.isNullOrBlank() || !title.isNullOrBlank(),
            enter = fadeIn(animationSpec = tween(durationMillis = AnimationDurationMillis)),
            exit = fadeOut(animationSpec = tween(durationMillis = AnimationDurationMillis)),
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
                    Box {
                        if (showLogo && !logoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = logoUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .requiredHeight(LogoHeight)
                                    .padding(end = spacing.space16),
                                contentScale = ContentScale.FillHeight,
                                onError = {
                                    showLogo = false
                                }
                            )
                        }
                    }
                    if (!title.isNullOrBlank()) {
                        POText(
                            text = title,
                            modifier = Modifier.weight(1f, fill = false),
                            color = titleStyle.color,
                            style = titleStyle.textStyle,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2
                        )
                    }
                }
                HorizontalDivider(thickness = 1.dp, color = dividerColor)
            }
        }
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
    AnimatedContent(
        targetState = content,
        contentKey = { it.uuid },
        transitionSpec = { ContentTransitionSpec }
    ) { content ->
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(spacing.space16)
        ) {
            val stage = content.stage
            when (stage) {
                is Stage.Pending -> stage.stepper?.let {
                    POVerticalStepper(
                        steps = it.steps,
                        activeStepIndex = it.activeStepIndex,
                        modifier = Modifier.fillMaxWidth(),
                        style = style.stepper
                    )
                }
                is Stage.Completed -> SuccessContent(
                    state = stage,
                    style = style.success
                )
                else -> {}
            }
            if (content.elements != null) {
                Elements(
                    elements = content.elements,
                    onEvent = onEvent,
                    style = style,
                    focusedFieldId = if (stage is Stage.NextStep) stage.focusedFieldId else null,
                    isPrimaryActionEnabled = isPrimaryActionEnabled,
                    isLightTheme = isLightTheme
                )
            }
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
            is Message -> AndroidTextView(
                text = element.value,
                style = style.bodyText,
                modifier = Modifier.fillMaxWidth(),
                selectable = true,
                linksClickable = true
            )
            is CopyableMessage -> CopyableMessage(
                message = element,
                style = style.labeledContent,
                modifier = Modifier.fillMaxWidth()
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
            is InstructionGroup -> InstructionGroup(
                group = element,
                onEvent = onEvent,
                style = style,
                isLightTheme = isLightTheme,
                modifier = Modifier.fillMaxWidth()
            )
            else -> {}
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
private fun CopyableMessage(
    message: CopyableMessage,
    style: LabeledContentStyle,
    modifier: Modifier = Modifier
) {
    POLabeledContent(
        label = message.label,
        labelStyle = style.label,
        modifier = modifier,
        trailingContent = {
            POCopyButton(
                textToCopy = message.value,
                copyText = message.copyText,
                copiedText = message.copiedText,
                modifier = Modifier.requiredHeightIn(min = dimensions.buttonIconSizeSmall),
                style = style.copyButton
            )
        },
        trailingContentAlignment = Alignment.Center
    ) {
        POText(
            text = message.value,
            color = style.text.color,
            style = style.text.textStyle
        )
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
private fun InstructionGroup(
    group: InstructionGroup,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    style: NativeAlternativePaymentScreen.Style,
    isLightTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val instructions = group.instructions.elements
    val items: List<@Composable () -> Unit> = instructions.mapNotNull { instruction ->
        when (instruction) {
            is Message -> {
                {
                    AndroidTextView(
                        text = instruction.value,
                        style = style.bodyText,
                        modifier = Modifier.fillMaxWidth(),
                        selectable = true,
                        linksClickable = true
                    )
                }
            }
            is CopyableMessage -> {
                {
                    CopyableMessage(
                        message = instruction,
                        style = style.labeledContent,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            is Image -> {
                {
                    Image(
                        image = instruction,
                        isLightTheme = isLightTheme
                    )
                }
            }
            is Barcode -> {
                {
                    Barcode(
                        barcode = instruction,
                        onEvent = onEvent,
                        style = style
                    )
                }
            }
            else -> null
        }
    }
    POGroupedContent(
        title = group.label,
        items = POImmutableList(items),
        modifier = modifier,
        style = style.groupedContent
    )
}

@Composable
private fun SuccessContent(
    state: Stage.Completed,
    style: SuccessStyle
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = spacing.space16,
                bottom = spacing.space8
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = style.successImageResId),
            contentDescription = null,
            alignment = Alignment.Center,
            contentScale = ContentScale.Fit
        )
        POText(
            text = state.title,
            modifier = Modifier.padding(top = spacing.space16),
            color = style.title.color,
            style = style.title.textStyle
        )
        state.message?.let {
            POText(
                text = it,
                modifier = Modifier.padding(top = spacing.space8),
                color = style.message.color,
                style = style.message.textStyle
            )
        }
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
    val secondary: POActionState?
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
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(animationSpec = tween(durationMillis = AnimationDurationMillis)),
        exit = fadeOut(animationSpec = tween(durationMillis = AnimationDurationMillis))
    ) {
        content()
    }
}

internal object NativeAlternativePaymentScreen {

    @Immutable
    data class Style(
        val title: POText.Style,
        val bodyText: AndroidTextView.Style,
        val labeledContent: LabeledContentStyle,
        val groupedContent: POGroupedContent.Style,
        val field: POField.Style,
        val codeField: POField.Style,
        val radioField: PORadioField.Style,
        val dropdownMenu: PODropdownField.MenuStyle,
        val checkbox: POCheckbox.Style,
        val dialog: PODialog.Style,
        val stepper: POStepper.Style,
        val success: SuccessStyle,
        val errorMessageBox: POMessageBox.Style,
        val actionsContainer: POActionsContainer.Style,
        val backgroundColor: Color,
        val progressIndicatorColor: Color,
        val dividerColor: Color,
        val dragHandleColor: Color
    )

    @Immutable
    data class LabeledContentStyle(
        val label: POText.Style,
        val text: POText.Style,
        val copyButton: POButton.Style
    )

    @Immutable
    data class SuccessStyle(
        val title: POText.Style,
        val message: POText.Style,
        @DrawableRes
        val successImageResId: Int
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
                bodyText = custom?.bodyText?.let { style ->
                    val controlsTintColor = custom.controlsTintColorResId?.let { colorResource(id = it) }
                    AndroidTextView.custom(
                        style = style,
                        controlsTintColor = controlsTintColor ?: colors.text.primary
                    )
                } ?: AndroidTextView.default,
                labeledContent = custom?.labeledContent?.custom() ?: defaultLabeledContent,
                groupedContent = custom?.groupedContent?.let {
                    POGroupedContent.custom(style = it)
                } ?: POGroupedContent.default,
                field = custom?.field?.let {
                    POField.custom(style = it)
                } ?: POField.default2,
                codeField = custom?.codeField?.let {
                    POField.custom(style = it)
                } ?: POCodeField.default2,
                radioField = custom?.radioField?.let {
                    PORadioField.custom(style = it)
                } ?: PORadioField.default,
                dropdownMenu = custom?.dropdownMenu?.let {
                    PODropdownField.custom(style = it)
                } ?: PODropdownField.defaultMenu2,
                checkbox = custom?.checkbox?.let {
                    POCheckbox.custom(style = it)
                } ?: POCheckbox.default2,
                dialog = custom?.dialog?.let {
                    PODialog.custom(style = it)
                } ?: PODialog.default,
                stepper = custom?.stepper?.let {
                    POStepper.custom(style = it)
                } ?: POStepper.default,
                success = custom?.success?.custom() ?: defaultSuccess,
                errorMessageBox = custom?.errorMessageBox?.let {
                    POMessageBox.custom(style = it)
                } ?: POMessageBox.error2,
                actionsContainer = custom?.actionsContainer?.let {
                    POActionsContainer.custom(style = it)
                } ?: POActionsContainer.default2,
                backgroundColor = custom?.backgroundColorResId?.let {
                    colorResource(id = it)
                } ?: colors.surface.default,
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

    private val defaultLabeledContent: LabeledContentStyle
        @Composable get() = LabeledContentStyle(
            label = POText.Style(
                color = colors.text.placeholder,
                textStyle = typography.s12(FontWeight.Medium)
            ),
            text = POText.Style(
                color = colors.text.primary,
                textStyle = typography.s15(FontWeight.Medium)
            ),
            copyButton = POCopyButton.default
        )

    @Composable
    private fun POLabeledContentStyle.custom() =
        LabeledContentStyle(
            label = POText.custom(style = label),
            text = POText.custom(style = text),
            copyButton = defaultLabeledContent.copyButton
        )

    private val defaultSuccess: SuccessStyle
        @Composable get() = SuccessStyle(
            title = POText.Style(
                color = colors.text.primary,
                textStyle = typography.s20(FontWeight.SemiBold)
            ),
            message = POText.Style(
                color = colors.text.secondary,
                textStyle = typography.paragraph.s16()
            ),
            successImageResId = R.drawable.po_success_image_v2
        )

    @Composable
    private fun PONativeAlternativePaymentConfiguration.Style.SuccessStyle.custom() =
        SuccessStyle(
            title = title?.let { POText.custom(style = it) } ?: defaultSuccess.title,
            message = message?.let { POText.custom(style = it) } ?: defaultSuccess.message,
            successImageResId = successImageResId ?: defaultSuccess.successImageResId
        )

    val LogoHeight = 26.dp
    val AnimationDurationMillis = 300

    val ContentTransitionSpec = fadeIn(
        animationSpec = tween(
            durationMillis = AnimationDurationMillis,
            easing = LinearEasing
        )
    ) togetherWith fadeOut(
        animationSpec = tween(
            durationMillis = AnimationDurationMillis,
            easing = LinearEasing
        )
    )
}
