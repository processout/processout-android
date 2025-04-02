package com.processout.sdk.ui.napm

import android.app.Application
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.dispatcher.napm.PODefaultNativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterType
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterType.*
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodTransactionDetails.Invoice
import com.processout.sdk.core.retry.PORetryStrategy.Exponential
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POActionState.Confirmation
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.napm.NativeAlternativePaymentInteractorState.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.Field.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.Image
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.CancelButton
import com.processout.sdk.ui.shared.extension.map
import com.processout.sdk.ui.shared.filter.PhoneNumberInputFilter
import com.processout.sdk.ui.shared.provider.BarcodeBitmapProvider
import com.processout.sdk.ui.shared.provider.MediaStorageProvider
import com.processout.sdk.ui.shared.state.ConfirmationDialogState
import com.processout.sdk.ui.shared.state.FieldState
import com.processout.sdk.ui.shared.transformation.PhoneNumberVisualTransformation
import java.text.NumberFormat
import java.util.Currency

internal class NativeAlternativePaymentViewModel private constructor(
    private val app: Application,
    configuration: PONativeAlternativePaymentConfiguration,
    private val interactor: NativeAlternativePaymentInteractor
) : ViewModel() {

    class Factory(
        private val app: Application,
        private val configuration: PONativeAlternativePaymentConfiguration,
        private val eventDispatcher: PODefaultNativeAlternativePaymentMethodEventDispatcher
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
                    ),
                    eventDispatcher = eventDispatcher
                )
            ) as T
    }

    private companion object {
        const val CODE_FIELD_LENGTH_MIN = 1
        const val CODE_FIELD_LENGTH_MAX = 6
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
            title = configuration.title ?: app.getString(R.string.po_native_apm_title_format, gateway.displayName),
            fields = fields.map(),
            focusedFieldId = focusedFieldId,
            primaryAction = POActionState(
                id = primaryActionId,
                text = configuration.submitButton.text ?: invoice.formatPrimaryActionText(),
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
        val fields = map { field ->
            val keyboardAction = keyboardAction(field.id, lastFocusableFieldId)
            when (field.type) {
                NUMERIC -> if (field.length in CODE_FIELD_LENGTH_MIN..CODE_FIELD_LENGTH_MAX) {
                    field.toCodeField(keyboardAction)
                } else {
                    field.toTextField(keyboardAction)
                }
                SINGLE_SELECT -> {
                    val availableValuesCount = field.availableValues?.size ?: 0
                    if (availableValuesCount <= configuration.inlineSingleSelectValuesLimit) {
                        field.toRadioField()
                    } else {
                        field.toDropdownField()
                    }
                }
                else -> field.toTextField(keyboardAction)
            }
        }
        return POImmutableList(fields)
    }

    private fun Field.toTextField(
        keyboardAction: KeyboardAction
    ): NativeAlternativePaymentViewModelState.Field =
        TextField(
            FieldState(
                id = id,
                value = value,
                title = displayName,
                description = description,
                placeholder = type.placeholder(),
                isError = !isValid,
                forceTextDirectionLtr = setOf(NUMERIC, EMAIL, PHONE).contains(type),
                inputFilter = if (type == PHONE) PhoneNumberInputFilter() else null,
                visualTransformation = if (type == PHONE) PhoneNumberVisualTransformation() else VisualTransformation.None,
                keyboardOptions = type.keyboardOptions(keyboardAction.imeAction),
                keyboardActionId = keyboardAction.actionId
            )
        )

    private fun Field.toCodeField(
        keyboardAction: KeyboardAction
    ): NativeAlternativePaymentViewModelState.Field =
        CodeField(
            FieldState(
                id = id,
                value = value,
                length = length,
                title = displayName,
                description = description,
                isError = !isValid,
                keyboardOptions = type.keyboardOptions(keyboardAction.imeAction),
                keyboardActionId = keyboardAction.actionId
            )
        )

    private fun Field.toRadioField(): NativeAlternativePaymentViewModelState.Field =
        RadioField(
            FieldState(
                id = id,
                value = value,
                availableValues = availableValues?.let { POImmutableList(it) },
                title = displayName,
                description = description,
                isError = !isValid
            )
        )

    private fun Field.toDropdownField(): NativeAlternativePaymentViewModelState.Field =
        DropdownField(
            FieldState(
                id = id,
                value = value,
                availableValues = availableValues?.let { POImmutableList(it) },
                title = displayName,
                description = description,
                isError = !isValid
            )
        )

    private fun List<Field>.lastFocusableFieldId(): String? {
        reversed().forEach { field ->
            if (field.type != SINGLE_SELECT) {
                return field.id
            }
        }
        return null
    }

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

    private fun ParameterType.keyboardOptions(
        imeAction: ImeAction
    ): KeyboardOptions = when (this) {
        NUMERIC -> KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = imeAction
        )
        TEXT -> KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text,
            imeAction = imeAction
        )
        EMAIL -> KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction
        )
        PHONE -> KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = imeAction
        )
        SINGLE_SELECT -> KeyboardOptions.Default
        UNKNOWN -> KeyboardOptions(
            imeAction = imeAction
        )
    }

    private fun ParameterType.placeholder(): String? = when (this) {
        EMAIL -> app.getString(R.string.po_native_apm_email_placeholder)
        PHONE -> app.getString(R.string.po_native_apm_phone_placeholder)
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
