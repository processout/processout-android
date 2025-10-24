package com.processout.sdk.ui.napm.screen

import android.view.Gravity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import coil.compose.AsyncImage
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
import com.processout.sdk.ui.core.component.stepper.POVerticalStepper
import com.processout.sdk.ui.core.shared.image.POImageRenderingMode.ORIGINAL
import com.processout.sdk.ui.core.shared.image.POImageRenderingMode.TEMPLATE
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.core.state.POPhoneNumberFieldState
import com.processout.sdk.ui.core.theme.ProcessOutTheme.dimensions
import com.processout.sdk.ui.core.theme.ProcessOutTheme.spacing
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.Element.*
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration
import com.processout.sdk.ui.napm.screen.NativeAlternativePaymentScreen.ContentTransitionSpec
import com.processout.sdk.ui.napm.screen.NativeAlternativePaymentScreen.LabeledContentStyle
import com.processout.sdk.ui.napm.screen.NativeAlternativePaymentScreen.SuccessStyle
import com.processout.sdk.ui.shared.component.AndroidTextView
import com.processout.sdk.ui.shared.component.rememberLifecycleEvent
import com.processout.sdk.ui.shared.state.FieldState
import com.processout.sdk.ui.shared.state.FieldValue

@Composable
internal fun NativeAlternativePaymentContent(
    content: Content,
    onEvent: (NativeAlternativePaymentEvent) -> Unit,
    style: NativeAlternativePaymentScreen.Style,
    isPrimaryActionEnabled: Boolean,
    isLightTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(spacing.space16)
    ) {
        CustomContent(
            stage = content.stage,
            style = style
        )
        AnimatedContent(
            targetState = content,
            contentKey = { it.uuid },
            transitionSpec = { ContentTransitionSpec }
        ) { content ->
            Column(
                modifier = Modifier.fillMaxWidth(),
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
}

@Composable
private fun CustomContent(
    stage: Stage,
    style: NativeAlternativePaymentScreen.Style
) {
    var visible = true
    var content: PONativeAlternativePaymentConfiguration.Content? = null
    when (stage) {
        is Stage.NextStep -> content = stage.customContent
        is Stage.Pending -> content = stage.customContent
        is Stage.Completed -> visible = false
    }
    if (visible && content != null) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.space16),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content.imageResId?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.padding(vertical = spacing.space16),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Fit
                )
            }
            content.title?.let {
                POText(
                    text = it.value,
                    modifier = Modifier.fillMaxWidth(),
                    color = style.title.color,
                    style = style.title.textStyle,
                    textAlign = if (it.alignCenterHorizontally) TextAlign.Center else TextAlign.Start
                )
            }
            content.bodyText?.let {
                AndroidTextView(
                    text = it.value,
                    modifier = Modifier.fillMaxWidth(),
                    style = style.bodyText,
                    gravity = if (it.alignCenterHorizontally) Gravity.CENTER_HORIZONTAL else Gravity.START,
                    selectable = true
                )
            }
            content.message?.let {
                if (it.icon != null) {
                    POTextWithIcon(
                        text = it.value,
                        modifier = Modifier.fillMaxWidth(),
                        style = POTextWithIcon.Style(
                            text = style.message,
                            iconResId = it.icon.resId,
                            iconColorFilter = when (it.icon.renderingMode) {
                                ORIGINAL -> null
                                TEMPLATE -> ColorFilter.tint(color = style.message.color)
                            }
                        ),
                        horizontalArrangement = Arrangement.spacedBy(spacing.space8)
                    )
                } else {
                    POText(
                        text = it.value,
                        modifier = Modifier.fillMaxWidth(),
                        color = style.message.color,
                        style = style.message.textStyle
                    )
                }
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
