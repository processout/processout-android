package com.processout.sdk.ui.napm

import android.app.Application
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse.Invoice
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentElement.Form.Parameter
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentElement.Form.Parameter.*
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentElement.Form.Parameter.Otp.Subtype
import com.processout.sdk.core.retry.PORetryStrategy.Exponential
import com.processout.sdk.ui.core.state.*
import com.processout.sdk.ui.core.state.POActionState.Confirmation
import com.processout.sdk.ui.core.transformation.POPhoneNumberVisualTransformation
import com.processout.sdk.ui.core.transformation.POPhoneNumberVisualTransformation.PhoneNumberFormat
import com.processout.sdk.ui.napm.NativeAlternativePaymentInteractorState.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.Content
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.Element.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.Stage
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.CancelButton
import com.processout.sdk.ui.shared.extension.currentAppLocale
import com.processout.sdk.ui.shared.extension.map
import com.processout.sdk.ui.shared.filter.DigitsInputFilter
import com.processout.sdk.ui.shared.filter.TextLengthInputFilter
import com.processout.sdk.ui.shared.provider.BarcodeBitmapProvider
import com.processout.sdk.ui.shared.provider.MediaStorageProvider
import com.processout.sdk.ui.shared.state.ConfirmationDialogState
import com.processout.sdk.ui.shared.state.FieldState
import com.processout.sdk.ui.shared.state.FieldValue
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

internal class NativeAlternativePaymentViewModel private constructor(
    private val app: Application,
    configuration: PONativeAlternativePaymentConfiguration,
    private val interactor: NativeAlternativePaymentInteractor
) : ViewModel() {

    class Factory(
        private val app: Application,
        private val configuration: PONativeAlternativePaymentConfiguration
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            NativeAlternativePaymentViewModel(
                app = app,
                configuration = configuration,
                interactor = NativeAlternativePaymentInteractor(
                    app = app,
                    configuration = configuration,
                    invoicesService = ProcessOut.instance.invoices,
                    customerTokensService = ProcessOut.instance.customerTokens,
                    barcodeBitmapProvider = BarcodeBitmapProvider(),
                    mediaStorageProvider = MediaStorageProvider(app),
                    captureRetryStrategy = Exponential(
                        maxRetries = Int.MAX_VALUE,
                        initialDelay = 150,
                        minDelay = 3 * 1000,
                        maxDelay = 90 * 1000,
                        factor = 1.45
                    )
                )
            ) as T
    }

    private data class KeyboardAction(
        val imeAction: ImeAction,
        val actionId: String?
    )

    var configuration = configuration
        private set

    val completion = interactor.completion

    val state = interactor.state.map(viewModelScope, ::map)

    val sideEffects = interactor.sideEffects

    private val codeFieldLengthRange = 1..8

    init {
        addCloseable(interactor.interactorScope)
    }

    fun start() = interactor.start()

    fun start(configuration: PONativeAlternativePaymentConfiguration) {
        this.configuration = configuration
        interactor.start(configuration)
    }

    fun reset() = interactor.reset()

    fun onEvent(event: NativeAlternativePaymentEvent) = interactor.onEvent(event)

    private fun map(
        state: NativeAlternativePaymentInteractorState
    ): NativeAlternativePaymentViewModelState =
        when (state) {
            Idle, Loading -> loading()
            is NextStep -> state.map()
            is Pending -> state.map()
            is Completed -> state.map()
            else -> this@NativeAlternativePaymentViewModel.state.value
        }

    private fun loading() = NativeAlternativePaymentViewModelState.Loading(
        secondaryAction = null
    )

    private fun NextStep.map() = with(value) {
        NativeAlternativePaymentViewModelState.Loaded(
            logo = paymentMethod.logo,
            title = configuration.title ?: invoice?.priceTitle(),
            content = Content(
                uuid = uuid,
                stage = Stage.NextStep(
                    focusedFieldId = focusedFieldId
                ),
                elements = elements.map(fields)
            ),
            primaryAction = POActionState(
                id = primaryActionId,
                text = redirect?.hint
                    ?: configuration.submitButton.text
                    ?: app.getString(R.string.po_native_apm_continue_button_text),
                primary = true,
                enabled = submitAllowed,
                loading = submitting,
                icon = configuration.submitButton.icon
            ),
            secondaryAction = configuration.cancelButton?.toActionState(
                id = secondaryAction.id,
                enabled = secondaryAction.enabled && !submitting
            )
        )
    }

    private fun Pending.map() = with(value) {
        NativeAlternativePaymentViewModelState.Loaded(
            logo = paymentMethod.logo,
            title = configuration.title ?: invoice?.priceTitle(),
            content = Content(
                uuid = uuid,
                stage = Stage.Pending(
                    stepper = stepper
                ),
                elements = elements?.map(fields = null)
            ),
            primaryAction = configuration.paymentConfirmation.confirmButton?.let {
                primaryActionId?.let { id ->
                    POActionState(
                        id = id,
                        text = it.text ?: app.getString(R.string.po_native_apm_confirm_payment_button_text),
                        primary = true,
                        icon = it.icon
                    )
                }
            },
            secondaryAction = configuration.paymentConfirmation.cancelButton?.toActionState(
                id = secondaryAction.id,
                enabled = secondaryAction.enabled
            )
        )
    }

    private fun Completed.map() = with(value) {
        NativeAlternativePaymentViewModelState.Loaded(
            logo = paymentMethod.logo,
            title = configuration.title ?: invoice?.priceTitle(),
            content = Content(
                uuid = uuid,
                stage = Stage.Completed(
                    title = configuration.success?.title ?: app.getString(R.string.po_native_apm_success_title),
                    message = configuration.success?.message ?: invoice?.priceSuccessMessage()
                ),
                elements = elements?.map(fields = null)
            ),
            primaryAction = configuration.success?.doneButton?.let {
                primaryActionId?.let { id ->
                    POActionState(
                        id = id,
                        text = it.text ?: app.getString(R.string.po_native_apm_done_button_text),
                        primary = true,
                        icon = it.icon
                    )
                }
            },
            secondaryAction = null
        )
    }

    private fun List<Element>.map(
        fields: List<Field>?
    ): POImmutableList<NativeAlternativePaymentViewModelState.Element> {
        val elements = flatMap { element ->
            when (element) {
                is Element.Form -> element.form.parameterDefinitions.map(fields)
                is Element.Instruction -> listOf(element.instruction.map())
                is Element.InstructionGroup -> emptyList() // TODO(v2)
            }
        }
        return POImmutableList(elements)
    }

    private fun List<Parameter>.map(
        fields: List<Field>?
    ): List<NativeAlternativePaymentViewModelState.Element> {
        val lastFocusableFieldId = fields?.lastFocusableFieldId()
        return mapNotNull { parameter ->
            fields?.find { it.id == parameter.key }?.let { field ->
                val keyboardAction = keyboardAction(field.id, lastFocusableFieldId)
                when (field.parameter) {
                    is PhoneNumber -> field.toPhoneNumberField(keyboardAction)
                    is SingleSelect -> {
                        val availableValuesSize = field.parameter.availableValues.size
                        if (availableValuesSize <= configuration.inlineSingleSelectValuesLimit) {
                            field.toRadioField()
                        } else {
                            field.toDropdownField()
                        }
                    }
                    is Bool -> field.toCheckboxField()
                    is Digits -> if (field.maxLength in codeFieldLengthRange) {
                        field.toCodeField(keyboardAction)
                    } else {
                        field.toTextField(keyboardAction)
                    }
                    is Otp -> when (field.parameter.subtype) {
                        Subtype.TEXT,
                        Subtype.DIGITS -> if (field.maxLength in codeFieldLengthRange) {
                            field.toCodeField(keyboardAction)
                        } else {
                            field.toTextField(keyboardAction)
                        }
                        Subtype.UNKNOWN -> null
                    }
                    Unknown -> null
                    else -> field.toTextField(keyboardAction)
                }
            }
        }
    }

    private fun Field.toTextField(
        keyboardAction: KeyboardAction
    ): NativeAlternativePaymentViewModelState.Element {
        val ltrParameterTypes = setOf(
            Digits::class.java,
            Email::class.java,
            Card::class.java,
            Otp::class.java
        )
        return TextField(
            FieldState(
                id = id,
                value = value.textFieldValue(),
                label = label,
                description = description,
                isError = !isValid,
                forceTextDirectionLtr = ltrParameterTypes.contains(parameter::class.java),
                inputFilter = parameter.inputFilter(),
                keyboardOptions = parameter.keyboardOptions(keyboardAction.imeAction),
                keyboardActionId = keyboardAction.actionId
            )
        )
    }

    private fun Field.toCodeField(
        keyboardAction: KeyboardAction
    ): NativeAlternativePaymentViewModelState.Element =
        CodeField(
            FieldState(
                id = id,
                value = value.textFieldValue(),
                length = maxLength,
                label = label,
                description = description,
                isError = !isValid,
                inputFilter = parameter.inputFilter(),
                keyboardOptions = parameter.keyboardOptions(keyboardAction.imeAction),
                keyboardActionId = keyboardAction.actionId
            )
        )

    private fun Field.toRadioField(): NativeAlternativePaymentViewModelState.Element =
        RadioField(
            FieldState(
                id = id,
                value = value.textFieldValue(),
                availableValues = parameter.availableValues(),
                label = label,
                description = description,
                isError = !isValid
            )
        )

    private fun Field.toDropdownField(): NativeAlternativePaymentViewModelState.Element =
        DropdownField(
            FieldState(
                id = id,
                value = value.textFieldValue(),
                availableValues = parameter.availableValues(),
                label = label,
                description = description,
                isError = !isValid
            )
        )

    private fun Field.toCheckboxField(): NativeAlternativePaymentViewModelState.Element =
        CheckboxField(
            FieldState(
                id = id,
                value = value.textFieldValue(),
                label = label,
                description = description,
                isError = !isValid
            )
        )

    private fun Field.toPhoneNumberField(
        keyboardAction: KeyboardAction
    ): NativeAlternativePaymentViewModelState.Element {
        val regionCode = when (value) {
            is FieldValue.PhoneNumber -> value.regionCode
            else -> TextFieldValue()
        }
        return PhoneNumberField(
            POPhoneNumberFieldState(
                id = id,
                regionCode = regionCode,
                regionCodes = parameter.phoneNumberRegionCodes(),
                regionCodeLabel = app.getString(R.string.po_native_apm_label_country),
                number = when (value) {
                    is FieldValue.PhoneNumber -> value.number
                    else -> TextFieldValue()
                },
                numberLabel = label,
                description = description,
                isError = !isValid,
                forceTextDirectionLtr = true,
                inputFilter = parameter.inputFilter(),
                visualTransformation = POPhoneNumberVisualTransformation(
                    regionCode = regionCode.text,
                    expectedFormat = PhoneNumberFormat.NATIONAL
                ),
                keyboardOptions = parameter.keyboardOptions(keyboardAction.imeAction),
                keyboardActionId = keyboardAction.actionId
            )
        )
    }

    private fun FieldValue.textFieldValue() =
        when (this) {
            is FieldValue.Text -> value
            else -> TextFieldValue()
        }

    private fun Parameter.availableValues(): POImmutableList<POAvailableValue>? =
        when (this) {
            is SingleSelect -> POImmutableList(
                availableValues.map {
                    POAvailableValue(
                        value = it.value,
                        text = it.label
                    )
                }
            )
            else -> null
        }

    private fun Parameter.phoneNumberRegionCodes(): POImmutableList<POAvailableValue> {
        val currentAppLocale = app.currentAppLocale()
        val availableValues = when (this) {
            is PhoneNumber -> dialingCodes.map {
                val localizedCountry = Locale(String(), it.regionCode).getDisplayCountry(currentAppLocale)
                POAvailableValue(
                    value = it.regionCode,
                    text = "$localizedCountry (${it.value})",
                    formattedText = it.value
                )
            }.sortedBy { it.text }
            else -> emptyList()
        }
        return POImmutableList(availableValues)
    }

    private fun Parameter.inputFilter(): POInputFilter? =
        when (this) {
            is PhoneNumber -> DigitsInputFilter()
            is Digits -> DigitsInputFilter(maxLength)
            is Card -> DigitsInputFilter(maxLength)
            is Text -> maxLength?.let { TextLengthInputFilter(maxLength = it) }
            is Otp -> when (subtype) {
                Subtype.TEXT -> maxLength?.let { TextLengthInputFilter(maxLength = it) }
                Subtype.DIGITS -> DigitsInputFilter(maxLength)
                Subtype.UNKNOWN -> null
            }
            else -> null
        }

    private fun List<Field>.lastFocusableFieldId(): String? =
        reversed().find {
            when (it.parameter) {
                is SingleSelect,
                is Bool,
                Unknown -> false
                else -> true
            }
        }?.id

    private fun keyboardAction(fieldId: String, lastFocusableFieldId: String?) =
        if (fieldId == lastFocusableFieldId) {
            KeyboardAction(
                imeAction = ImeAction.Done,
                actionId = ActionId.SUBMIT
            )
        } else {
            KeyboardAction(
                imeAction = ImeAction.Next,
                actionId = null
            )
        }

    private fun Parameter.keyboardOptions(
        imeAction: ImeAction
    ): KeyboardOptions = when (this) {
        is Text -> KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text,
            imeAction = imeAction
        )
        is SingleSelect -> KeyboardOptions.Default
        is Bool -> KeyboardOptions.Default
        is Digits -> KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = imeAction
        )
        is PhoneNumber -> KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = imeAction
        )
        is Email -> KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction
        )
        is Card -> KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = imeAction
        )
        is Otp -> when (subtype) {
            Subtype.TEXT -> KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                keyboardType = KeyboardType.Text,
                imeAction = imeAction
            )
            Subtype.DIGITS -> KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = imeAction
            )
            Subtype.UNKNOWN -> KeyboardOptions.Default
        }
        Unknown -> KeyboardOptions.Default
    }

    private fun Instruction.map(): NativeAlternativePaymentViewModelState.Element =
        when (this) {
            is Instruction.Message -> map()
            is Instruction.Image -> Image(value = value)
            is Instruction.Barcode -> map()
        }

    private fun Instruction.Message.map() =
        if (label == null) {
            InstructionMessage(value = value)
        } else {
            CopyableInstructionMessage(
                label = label,
                value = value,
                copyText = app.getString(R.string.po_native_apm_copy_button_text),
                copiedText = app.getString(R.string.po_native_apm_copied_button_text)
            )
        }

    private fun Instruction.Barcode.map() =
        Barcode(
            image = bitmap,
            saveBarcodeAction = POActionState(
                id = actionId,
                text = configuration.barcode.saveButton.text
                    ?: app.getString(
                        R.string.po_native_apm_save_barcode_button_text_format,
                        type.rawType.uppercase()
                    ),
                primary = false,
                icon = configuration.barcode.saveButton.icon
            ),
            confirmationDialog = if (isError) {
                configuration.barcode.saveErrorConfirmation?.let {
                    ConfirmationDialogState(
                        id = confirmErrorActionId,
                        title = it.title ?: app.getString(R.string.po_native_apm_save_barcode_error_title),
                        message = it.message ?: app.getString(R.string.po_native_apm_save_barcode_error_message),
                        confirmActionText = it.confirmActionText
                            ?: app.getString(R.string.po_native_apm_save_barcode_error_confirm),
                        dismissActionText = it.dismissActionText
                    )
                }
            } else null
        )

    private fun Invoice.price(): String? =
        try {
            val formatter = NumberFormat.getCurrencyInstance()
            formatter.currency = Currency.getInstance(currency)
            formatter.format(amount.toDouble())
        } catch (_: Exception) {
            null
        }

    private fun Invoice.priceTitle(): String? =
        price()?.let { app.getString(R.string.po_native_apm_pay_format, it) }

    private fun Invoice.priceSuccessMessage(): String? =
        price()?.let { app.getString(R.string.po_native_apm_success_message_format, it) }

    private fun CancelButton.toActionState(
        id: String,
        enabled: Boolean
    ) = POActionState(
        id = id,
        text = text ?: app.getString(R.string.po_native_apm_cancel_button_text),
        primary = false,
        enabled = enabled,
        icon = icon,
        confirmation = confirmation?.run {
            Confirmation(
                title = title ?: app.getString(R.string.po_cancel_payment_confirmation_title),
                message = message,
                confirmActionText = confirmActionText
                    ?: app.getString(R.string.po_cancel_payment_confirmation_confirm),
                dismissActionText = dismissActionText
                    ?: app.getString(R.string.po_cancel_payment_confirmation_dismiss)
            )
        }
    )

    override fun onCleared() {
        interactor.clear()
    }
}
