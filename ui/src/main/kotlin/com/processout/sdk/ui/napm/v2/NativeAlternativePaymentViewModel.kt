package com.processout.sdk.ui.napm.v2

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
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodTransactionDetails.Invoice
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentNextStep.SubmitData.Parameter
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentNextStep.SubmitData.Parameter.*
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentNextStep.SubmitData.Parameter.Otp.Subtype
import com.processout.sdk.core.retry.PORetryStrategy.Exponential
import com.processout.sdk.ui.core.state.*
import com.processout.sdk.ui.core.state.POActionState.Confirmation
import com.processout.sdk.ui.core.transformation.POPhoneNumberVisualTransformation
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.CancelButton
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentInteractorState.*
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentViewModelState.Field.*
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentViewModelState.Image
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
    ): NativeAlternativePaymentViewModelState = when (state) {
        Idle, Loading -> loading()
        is UserInput -> state.map()
        is Capturing -> state.map()
        is Captured -> state.map()
        else -> this@NativeAlternativePaymentViewModel.state.value
    }

    private fun loading() = NativeAlternativePaymentViewModelState.Loading(
        secondaryAction = null
    )

    private fun UserInput.map() = with(value) {
        NativeAlternativePaymentViewModelState.UserInput(
//            title = configuration.title ?: app.getString(R.string.po_native_apm_title_format, gateway.displayName),
            title = String(), // TODO(v2): map from gateway
            fields = fields.map(),
            focusedFieldId = focusedFieldId,
            primaryAction = POActionState(
                id = primaryActionId,
//                text = configuration.submitButton.text ?: invoice.formatPrimaryActionText(),
                text = String(), // TODO(v2): map from invoice
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

    private fun Capturing.map() = with(value) {
        val secondaryAction = configuration.paymentConfirmation.cancelButton?.toActionState(
            id = secondaryAction.id,
            enabled = secondaryAction.enabled
        )
        val customerActionMessage = customerAction?.message
        if (customerActionMessage.isNullOrBlank()) {
            NativeAlternativePaymentViewModelState.Loading(
                secondaryAction = secondaryAction
            )
        } else {
            val primaryAction = configuration.paymentConfirmation.confirmButton?.let {
                primaryActionId?.let { id ->
                    POActionState(
                        id = id,
                        text = it.text ?: app.getString(R.string.po_native_apm_confirm_payment_button_text),
                        primary = true,
                        icon = it.icon
                    )
                }
            }
            NativeAlternativePaymentViewModelState.Capture(
                title = paymentProviderName,
                logoUrl = logoUrl,
                image = customerAction?.barcode?.let { Image.Bitmap(it.bitmap) }
                    ?: customerAction?.imageUrl?.let { Image.Url(it) },
                message = customerActionMessage,
                primaryAction = primaryAction,
                secondaryAction = secondaryAction,
                saveBarcodeAction = customerAction?.barcode?.let {
                    POActionState(
                        id = it.actionId,
                        text = configuration.barcode.saveButton.text
                            ?: app.getString(
                                R.string.po_native_apm_save_barcode_button_text_format,
                                it.type.rawType.uppercase()
                            ),
                        primary = false,
                        icon = configuration.barcode.saveButton.icon
                    )
                },
                confirmationDialog = confirmationDialog(),
                withProgressIndicator = withProgressIndicator,
                isCaptured = false
            )
        }
    }

    private fun Captured.map() = with(value) {
        NativeAlternativePaymentViewModelState.Capture(
            title = paymentProviderName,
            logoUrl = logoUrl,
            image = null,
            message = configuration.successMessage ?: app.getString(R.string.po_native_apm_success_message),
            primaryAction = null,
            secondaryAction = null,
            saveBarcodeAction = null,
            confirmationDialog = null,
            withProgressIndicator = false,
            isCaptured = true
        )
    }

    private fun List<Field>.map(): POImmutableList<NativeAlternativePaymentViewModelState.Field> {
        val lastFocusableFieldId = lastFocusableFieldId()
        val fields = mapNotNull { field ->
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
                is Bool -> null // TODO(v2): add checkbox field
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
        return POImmutableList(fields)
    }

    private fun Field.toTextField(
        keyboardAction: KeyboardAction
    ): NativeAlternativePaymentViewModelState.Field {
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
                title = label,
                description = description,
                placeholder = parameter.placeholder(),
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
    ): NativeAlternativePaymentViewModelState.Field =
        CodeField(
            FieldState(
                id = id,
                value = value.textFieldValue(),
                length = maxLength,
                title = label,
                description = description,
                isError = !isValid,
                inputFilter = parameter.inputFilter(),
                keyboardOptions = parameter.keyboardOptions(keyboardAction.imeAction),
                keyboardActionId = keyboardAction.actionId
            )
        )

    private fun Field.toRadioField(): NativeAlternativePaymentViewModelState.Field =
        RadioField(
            FieldState(
                id = id,
                value = value.textFieldValue(),
                availableValues = parameter.availableValues(),
                title = label,
                description = description,
                isError = !isValid
            )
        )

    private fun Field.toDropdownField(): NativeAlternativePaymentViewModelState.Field =
        DropdownField(
            FieldState(
                id = id,
                value = value.textFieldValue(),
                availableValues = parameter.availableValues(),
                title = label,
                description = description,
                isError = !isValid
            )
        )

    private fun Field.toPhoneNumberField(
        keyboardAction: KeyboardAction
    ): NativeAlternativePaymentViewModelState.Field =
        PhoneNumberField(
            POPhoneNumberFieldState(
                id = id,
                dialingCode = when (value) {
                    is FieldValue.PhoneNumber -> value.dialingCode
                    else -> TextFieldValue()
                },
                dialingCodes = parameter.dialingCodes(),
                dialingCodePlaceholder = null,
                number = when (value) {
                    is FieldValue.PhoneNumber -> value.number
                    else -> TextFieldValue()
                },
                numberPlaceholder = parameter.placeholder(),
                title = label,
                description = description,
                isError = !isValid,
                forceTextDirectionLtr = true,
                inputFilter = parameter.inputFilter(),
                visualTransformation = POPhoneNumberVisualTransformation(),
                keyboardOptions = parameter.keyboardOptions(keyboardAction.imeAction),
                keyboardActionId = keyboardAction.actionId
            )
        )

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

    private fun Parameter.dialingCodes(): POImmutableList<POAvailableValue> {
        val availableValues = when (this) {
            is PhoneNumber -> dialingCodes?.map {
                POAvailableValue(
                    value = it.id,
                    text = it.value
                )
            } ?: emptyList()
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

    private fun Parameter.placeholder(): String? =
        when (this) {
            is PhoneNumber -> app.getString(R.string.po_native_apm_phone_placeholder)
            is Email -> app.getString(R.string.po_native_apm_email_placeholder)
            else -> null
        }

    private fun Invoice.formatPrimaryActionText() =
        try {
            val price = NumberFormat.getCurrencyInstance().apply {
                currency = Currency.getInstance(currencyCode)
            }.format(amount.toDouble())
            app.getString(R.string.po_native_apm_submit_button_text_format, price)
        } catch (_: Exception) {
            app.getString(R.string.po_native_apm_submit_button_text)
        }

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

    private fun CaptureStateValue.confirmationDialog(): ConfirmationDialogState? =
        customerAction?.barcode?.let { barcode ->
            if (barcode.isError) {
                configuration.barcode.saveErrorConfirmation?.let {
                    ConfirmationDialogState(
                        id = barcode.confirmErrorActionId,
                        title = it.title ?: app.getString(R.string.po_native_apm_save_barcode_error_title),
                        message = it.message ?: app.getString(R.string.po_native_apm_save_barcode_error_message),
                        confirmActionText = it.confirmActionText
                            ?: app.getString(R.string.po_native_apm_save_barcode_error_confirm),
                        dismissActionText = it.dismissActionText
                    )
                }
            } else null
        }

    override fun onCleared() {
        interactor.clear()
    }
}
