package com.processout.sdk.ui.napm.v2

import android.Manifest
import android.app.Application
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.os.postDelayed
import androidx.core.text.isDigitsOnly
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.ImageResult
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.processout.sdk.R
import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentAuthorizationDetailsRequest
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentAuthorizationRequest
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentAuthorizationRequest.Parameter.Companion.phoneNumber
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentAuthorizationRequest.Parameter.Companion.string
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentTokenizationDetailsRequest
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodCapture
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameterValues
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodState.CAPTURED
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodTransactionDetails
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse.State
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentCustomerInstruction
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentNextStep
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentNextStep.*
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentNextStep.SubmitData.Parameter
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentNextStep.SubmitData.Parameter.Otp.Subtype
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentNextStep.Unknown
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.POFailure.Code.*
import com.processout.sdk.core.POFailure.InvalidField
import com.processout.sdk.core.POFailure.ValidationCode
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.fold
import com.processout.sdk.core.logger.POLogAttribute
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import com.processout.sdk.core.retry.PORetryStrategy
import com.processout.sdk.ui.base.BaseInteractor
import com.processout.sdk.ui.napm.NativeAlternativePaymentCompletion
import com.processout.sdk.ui.napm.NativeAlternativePaymentCompletion.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentSideEffect
import com.processout.sdk.ui.napm.NativeAlternativePaymentSideEffect.PermissionRequest
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.CancelButton
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.Flow.Authorization
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.Flow.Tokenization
import com.processout.sdk.ui.napm.delegate.v2.NativeAlternativePaymentDefaultValuesRequest
import com.processout.sdk.ui.napm.delegate.v2.NativeAlternativePaymentDefaultValuesResponse
import com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentEvent
import com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentEvent.*
import com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentParameterValue
import com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentParameterValue.Value
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentEvent.*
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentEvent.Action
import com.processout.sdk.ui.napm.v2.NativeAlternativePaymentInteractorState.*
import com.processout.sdk.ui.shared.extension.dpToPx
import com.processout.sdk.ui.shared.provider.BarcodeBitmapProvider
import com.processout.sdk.ui.shared.provider.MediaStorageProvider
import com.processout.sdk.ui.shared.state.FieldValue
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

internal class NativeAlternativePaymentInteractor(
    private val app: Application,
    private var configuration: PONativeAlternativePaymentConfiguration,
    private val invoicesService: POInvoicesService,
    private val barcodeBitmapProvider: BarcodeBitmapProvider,
    private val mediaStorageProvider: MediaStorageProvider,
    private val captureRetryStrategy: PORetryStrategy,
    private val eventDispatcher: POEventDispatcher = POEventDispatcher,
    private var logAttributes: Map<String, String> = logAttributes(
        invoiceId = configuration.invoiceId,
        gatewayConfigurationId = configuration.gatewayConfigurationId
    )
) : BaseInteractor() {

    private companion object {
        const val SUCCESS_DELAY_MS = 3000L

        fun logAttributes(
            invoiceId: String,
            gatewayConfigurationId: String
        ): Map<String, String> = mapOf(
            POLogAttribute.INVOICE_ID to invoiceId,
            POLogAttribute.GATEWAY_CONFIGURATION_ID to gatewayConfigurationId
        )
    }

    private val _completion = MutableStateFlow<NativeAlternativePaymentCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow<NativeAlternativePaymentInteractorState>(Idle)
    val state = _state.asStateFlow()

    private val _sideEffects = Channel<NativeAlternativePaymentSideEffect>()
    val sideEffects = _sideEffects.receiveAsFlow()

    private val handler = Handler(Looper.getMainLooper())
    private val phoneNumberUtil = PhoneNumberUtil.getInstance()

    private var latestDefaultValuesRequest: NativeAlternativePaymentDefaultValuesRequest? = null

    private var captureStartTimestamp = 0L
    private var capturePassedTimestamp = 0L

    fun start() {
        if (_state.value !is Idle) {
            return
        }
        _state.update { Loading }
        POLogger.info("Starting native alternative payment.")
        dispatch(WillStart)
        dispatchFailure()
        collectDefaultValues()
        fetchPaymentDetails()
    }

    fun start(configuration: PONativeAlternativePaymentConfiguration) {
        if (_state.value !is Idle) {
            return
        }
        this.configuration = configuration
        logAttributes = logAttributes(
            invoiceId = configuration.invoiceId,
            gatewayConfigurationId = configuration.gatewayConfigurationId
        )
        start()
    }

    fun reset() {
        interactorScope.coroutineContext.cancelChildren()
        handler.removeCallbacksAndMessages(null)
        latestDefaultValuesRequest = null
        captureStartTimestamp = 0L
        capturePassedTimestamp = 0L
        _completion.update { Awaiting }
        _state.update { Idle }
    }

    private fun fetchPaymentDetails() {
        interactorScope.launch {
            when (val flow = configuration.flow) {
                is Authorization -> fetchAuthorizationDetails(flow.request)
                is Tokenization -> fetchTokenizationDetails(flow.request)
            }
        }
    }

    private fun fetchAuthorizationDetails(
        request: PONativeAlternativePaymentAuthorizationDetailsRequest
    ) {
        interactorScope.launch {
            invoicesService.nativeAlternativePayment(request)
                .onSuccess { response ->
                    handlePaymentState(
                        stateValue = response.toUserInputStateValue(),
                        paymentState = response.state,
                        nextStep = response.nextStep,
                        customerInstructions = response.customerInstructions
                    )
                }.onFailure { failure ->
                    POLogger.info("Failed to fetch authorization details: %s", failure)
                    _completion.update { Failure(failure) }
                }
        }
    }

    private fun fetchTokenizationDetails(
        request: PONativeAlternativePaymentTokenizationDetailsRequest
    ) {
        TODO(reason = "v2")
    }

    private suspend fun handlePaymentState(
        stateValue: UserInputStateValue,
        paymentState: State,
        nextStep: PONativeAlternativePaymentNextStep?,
        customerInstructions: List<PONativeAlternativePaymentCustomerInstruction>?
    ) {
        when (paymentState) {
            State.NEXT_STEP_REQUIRED -> handleNextStep(stateValue, nextStep)
            State.PENDING_CAPTURE -> TODO(reason = "v2")
            State.CAPTURED -> TODO(reason = "v2")
            State.UNKNOWN -> TODO(reason = "v2")
        }
    }

    private fun handleNextStep(
        stateValue: UserInputStateValue,
        nextStep: PONativeAlternativePaymentNextStep?
    ) {
        when (nextStep) {
            is SubmitData -> handleSubmitData(stateValue, nextStep.parameterDefinitions)
            is Redirect -> TODO(reason = "v2")
            Unknown -> TODO(reason = "v2")
            null -> TODO(reason = "v2")
        }
    }

    private fun PONativeAlternativePaymentAuthorizationResponse.toUserInputStateValue() =
        UserInputStateValue(
            fields = emptyList(),
            focusedFieldId = null,
            primaryActionId = ActionId.SUBMIT,
            secondaryAction = NativeAlternativePaymentInteractorState.Action(
                id = ActionId.CANCEL,
                enabled = false
            ),
            submitAllowed = true,
            submitting = false
        )

    private fun UserInputStateValue.toCaptureStateValue(
        parameterValues: PONativeAlternativePaymentMethodParameterValues?,
        barcode: Barcode? = null
    ) = CaptureStateValue(
        paymentProviderName = parameterValues?.providerName,
//        logoUrl = logoUrl(gateway, parameterValues),
        logoUrl = null, // TODO(v2): map from gateway
        customerAction = customerAction(parameterValues, barcode),
        primaryActionId = ActionId.CONFIRM_PAYMENT,
        secondaryAction = NativeAlternativePaymentInteractorState.Action(
            id = ActionId.CANCEL,
            enabled = false
        ),
        withProgressIndicator = false
    )

    private fun logoUrl(
        gateway: PONativeAlternativePaymentMethodTransactionDetails.Gateway,
        parameterValues: PONativeAlternativePaymentMethodParameterValues?
    ): String? {
        if (parameterValues?.providerName != null) {
            return parameterValues.providerLogoUrl
        }
        if (configuration.paymentConfirmation.hideGatewayDetails) {
            return null
        }
        return gateway.logoUrl
    }

    private fun UserInputStateValue.customerAction(
        parameterValues: PONativeAlternativePaymentMethodParameterValues?,
        barcode: Barcode? = null
    ): CustomerAction? {
//        val message = parameterValues?.customerActionMessage
//            ?: gateway.customerActionMessage?.let { escapedMarkdown(it) }
//        return message?.let {
//            CustomerAction(
//                message = it,
//                imageUrl = gateway.customerActionImageUrl,
//                barcode = barcode
//            )
//        }
        return null // TODO(v2): resolve from instructions and gateway
    }

    //region User Input

    private fun handleSubmitData(
        stateValue: UserInputStateValue,
        parameters: List<Parameter>
    ) {
        if (parameters.isEmpty()) {
            POLogger.warn(
                message = "Parameters is empty in response.",
                attributes = logAttributes
            )
        }
        if (failWithUnknownParameter(parameters)) {
            return
        }
        val fields = parameters.toFields()
        val focusedFieldId = fields.firstFocusableFieldId()
        val updatedStateValue = stateValue.copy(
            fields = fields,
            focusedFieldId = focusedFieldId
        )
        _state.update {
            if (_state.value is Loading) {
                Loaded(updatedStateValue)
            } else {
                Submitted(updatedStateValue)
            }
        }
        requestDefaultValues(parameters)
    }

    private fun failWithUnknownParameter(
        parameters: List<Parameter>
    ): Boolean {
        parameters.find { it == Parameter.Unknown }?.let {
            val failure = ProcessOutResult.Failure(
                code = Internal(),
                message = "Unknown parameter type."
            )
            POLogger.error(
                message = "Unexpected response: %s", failure,
                attributes = logAttributes
            )
            _completion.update { Failure(failure) }
            return true
        }
        return false
    }

    private fun List<Parameter>.toFields() =
        map { parameter ->
            val defaultValue = when (parameter) {
                is Parameter.SingleSelect -> FieldValue.Text(
                    TextFieldValue(text = parameter.preselectedValue?.value ?: String())
                )
                is Parameter.PhoneNumber -> FieldValue.PhoneNumber()
                else -> FieldValue.Text()
            }
            Field(
                parameter = parameter,
                value = defaultValue,
                isValid = true,
                description = null
            )
        }

    private fun List<Field>.firstFocusableFieldId(): String? =
        find {
            when (it.parameter) {
                is Parameter.SingleSelect,
                is Parameter.Bool,
                Parameter.Unknown -> false
                else -> true
            }
        }?.id

    private fun startUserInput(stateValue: UserInputStateValue) {
        _state.update { UserInput(stateValue) }
        enableUserInputSecondaryAction()
        POLogger.info("Started: waiting for payment parameters.")
        dispatch(DidStart)
    }

    private fun continueUserInput(stateValue: UserInputStateValue) {
        _state.update {
            UserInput(
                stateValue.copy(
                    submitAllowed = true,
                    submitting = false
                )
            )
        }
        POLogger.info("Submitted: waiting for additional payment parameters.")
        dispatch(DidSubmitParameters(additionalParametersExpected = true))
    }

    //endregion

    //region Default Values

    private fun requestDefaultValues(parameters: List<Parameter>) {
        interactorScope.launch {
            val request = NativeAlternativePaymentDefaultValuesRequest(
                gatewayConfigurationId = configuration.gatewayConfigurationId,
                parameters = parameters
            )
            latestDefaultValuesRequest = request
            eventDispatcher.send(request)
            POLogger.debug("Requested to provide default values for payment parameters: %s", request)
        }
    }

    private fun collectDefaultValues() {
        eventDispatcher.subscribeForResponse<NativeAlternativePaymentDefaultValuesResponse>(
            coroutineScope = interactorScope
        ) { response ->
            if (response.uuid == latestDefaultValuesRequest?.uuid) {
                latestDefaultValuesRequest = null
                POLogger.debug("Collected default values for payment parameters: %s", response)
                _state.whenLoaded { stateValue ->
                    startUserInput(stateValue.updateFieldValues(response.defaultValues))
                }
                _state.whenSubmitted { stateValue ->
                    continueUserInput(stateValue.updateFieldValues(response.defaultValues))
                }
            }
        }
    }

    private fun UserInputStateValue.updateFieldValues(
        values: Map<String, PONativeAlternativePaymentParameterValue>
    ): UserInputStateValue {
        val updatedFields = fields.map { field ->
            values.entries.find { it.key == field.id }?.let { entry ->
                when (val value = entry.value.value) {
                    is Value.String -> {
                        val string = value.value
                        val maxLength = field.maxLength
                        val text = if (maxLength != null) string.take(maxLength) else string
                        field.copy(
                            value = FieldValue.Text(
                                TextFieldValue(
                                    text = text,
                                    selection = TextRange(text.length)
                                )
                            )
                        )
                    }
                    is Value.PhoneNumber -> when (field.parameter) {
                        is Parameter.PhoneNumber -> field.copy(
                            value = FieldValue.PhoneNumber(
                                regionCode = TextFieldValue(
                                    text = field.parameter.dialingCodes?.find { it.id == value.regionCode }
                                        ?.let { value.regionCode } ?: String()
                                ),
                                number = TextFieldValue(
                                    text = value.number,
                                    selection = TextRange(value.number.length)
                                )
                            )
                        )
                        else -> field
                    }
                }
            } ?: field
        }
        return copy(fields = updatedFields)
    }

    //endregion

    fun onEvent(event: NativeAlternativePaymentEvent) {
        when (event) {
            is FieldValueChanged -> updateFieldValue(event.id, event.value)
            is FieldFocusChanged -> updateFieldFocus(event.id, event.isFocused)
            is Action -> when (event.id) {
                ActionId.SUBMIT -> submit()
                ActionId.CANCEL -> cancel()
                ActionId.CONFIRM_PAYMENT -> confirmPayment()
                ActionId.SAVE_BARCODE -> saveBarcode()
            }
            is DialogAction -> when (event.id) {
                ActionId.CONFIRM_SAVE_BARCODE_ERROR -> updateBarcodeState(isError = false)
            }
            is ActionConfirmationRequested -> {
                POLogger.debug("Requested the user to confirm the action: %s", event.id)
                if (event.id == ActionId.CANCEL) {
                    dispatch(DidRequestCancelConfirmation)
                }
            }
            is PermissionRequestResult -> handlePermission(event)
            is Dismiss -> {
                POLogger.info("Dismissed: %s", event.failure)
                dispatch(DidFail(event.failure))
            }
        }
    }

    //region Update Field

    private fun updateFieldValue(id: String, value: FieldValue) {
        _state.whenUserInput { stateValue ->
            val previousValue = stateValue.fields.find { it.id == id }?.value ?: FieldValue.Text()
            val isTextChanged = !value.isTextEquals(previousValue)
            val updatedStateValue = stateValue.copy(
                fields = stateValue.fields.map { field ->
                    updatedField(id, value, field, isTextChanged)
                }
            )
            _state.update { UserInput(updatedStateValue) }
            if (isTextChanged) {
                POLogger.debug("Field is edited by the user: %s", id)
                updatedStateValue.fields.find { it.id == id }?.let {
                    dispatch(ParametersChanged(it.parameter))
                }
                if (updatedStateValue.areAllFieldsValid()) {
                    _state.update { UserInput(updatedStateValue.copy(submitAllowed = true)) }
                }
            }
        }
    }

    private fun updatedField(
        id: String,
        value: FieldValue,
        field: Field,
        isTextChanged: Boolean
    ): Field {
        if (field.id != id) {
            return field
        }
        return if (isTextChanged) {
            field.copy(value = value, isValid = true, description = null)
        } else {
            field.copy(value = value)
        }
    }

    private fun updateFieldFocus(id: String, isFocused: Boolean) {
        if (isFocused) {
            _state.whenUserInput { stateValue ->
                _state.update {
                    UserInput(stateValue.copy(focusedFieldId = id))
                }
            }
        }
    }

    //endregion

    //region Submit & Validation

    private fun submit() {
        _state.whenUserInput { stateValue ->
            POLogger.info("Will submit payment parameters.")
            dispatch(WillSubmitParameters(parameters = stateValue.fields.map { it.parameter }))
            val invalidFields = stateValue.fields.mapNotNull { it.validate() }
            if (invalidFields.isNotEmpty()) {
                val failure = ProcessOutResult.Failure(
                    code = Validation(ValidationCode.general),
                    message = "Invalid fields.",
                    invalidFields = invalidFields
                )
                handlePaymentFailure(
                    failure = failure,
                    replaceWithLocalMessage = false
                )
                return@whenUserInput
            }
            _state.update {
                UserInput(
                    stateValue.copy(
                        submitAllowed = true,
                        submitting = true
                    )
                )
            }
            initiatePayment()
        }
    }

    private fun Field.validate(): InvalidField? {
        when (parameter) {
            is Parameter.PhoneNumber -> if (value is FieldValue.PhoneNumber) {
                val dialingCode = value.regionCode.text.let { regionCode ->
                    if (regionCode.isNotBlank())
                        "+${phoneNumberUtil.getCountryCodeForRegion(regionCode)}"
                    else String()
                }
                val number = value.number.text
                if (required && (dialingCode.isBlank() || number.isBlank())) {
                    return invalidField(R.string.po_native_apm_error_required_parameter)
                }
                val phoneNumber = "$dialingCode$number"
                if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
                    return invalidField(R.string.po_native_apm_error_invalid_phone)
                }
            }
            else -> if (value is FieldValue.Text) {
                val value = value.value.text
                if (required && value.isBlank()) {
                    return invalidField(R.string.po_native_apm_error_required_parameter)
                }
                val length = if (
                    minLength != null && maxLength != null &&
                    minLength == maxLength
                ) maxLength else null
                if (length != null && value.length != length) {
                    return InvalidField(
                        name = id,
                        message = app.resources.getQuantityString(
                            R.plurals.po_native_apm_error_invalid_length, length, length
                        )
                    )
                }
                // TODO(v2): add validation by 'minLength', 'maxLength' and/or range
                when (parameter) {
                    is Parameter.Digits -> if (!value.isDigitsOnly()) {
                        return invalidField(R.string.po_native_apm_error_invalid_number)
                    }
                    is Parameter.Otp -> when (parameter.subtype) {
                        Subtype.DIGITS -> if (!value.isDigitsOnly()) {
                            return invalidField(R.string.po_native_apm_error_invalid_number)
                        }
                        else -> {}
                    }
                    is Parameter.Email -> if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
                        return invalidField(R.string.po_native_apm_error_invalid_email)
                    }
                    else -> {}
                }
            }
        }
        return null
    }

    private fun Field.invalidField(
        @StringRes messageResId: Int
    ) = InvalidField(
        name = id,
        message = app.getString(messageResId)
    )

    private fun UserInputStateValue.areAllFieldsValid() = fields.all { it.isValid }

    private fun initiatePayment() {
        _state.whenUserInput { stateValue ->
            interactorScope.launch {
                val request = PONativeAlternativePaymentAuthorizationRequest(
                    invoiceId = configuration.invoiceId,
                    gatewayConfigurationId = configuration.gatewayConfigurationId,
                    parameters = stateValue.fields.values()
                )
                TODO(reason = "v2")
//                invoicesService.initiatePayment(request)
//                    .onSuccess { response ->
//                        handlePaymentState(
//                            stateValue = stateValue,
//                            paymentState = response.state,
//                            nextStep = response.nextStep,
//                            customerInstructions = response.customerInstructions
//                        )
//                    }.onFailure { failure ->
//                        handlePaymentFailure(
//                            failure = failure,
//                            replaceWithLocalMessage = true
//                        )
//                    }
            }
        }
    }

    private fun List<Field>.values() =
        associate {
            it.id to when (it.value) {
                is FieldValue.Text -> string(value = it.value.value.text)
                is FieldValue.PhoneNumber -> phoneNumber(
                    dialingCode = it.value.regionCode.text.let { regionCode ->
                        if (regionCode.isNotBlank())
                            "+${phoneNumberUtil.getCountryCodeForRegion(regionCode)}"
                        else String()
                    },
                    number = it.value.number.text
                )
            }
        }

    //endregion

    //region Handle Failure

    private fun handlePaymentFailure(
        failure: ProcessOutResult.Failure,
        replaceWithLocalMessage: Boolean // TODO: Delete this when backend localization is ready.
    ) {
        _state.whenUserInput { stateValue ->
            val invalidFields = failure.invalidFields
            if (invalidFields.isNullOrEmpty()) {
                POLogger.info("Unrecoverable payment failure: %s", failure)
                _completion.update { Failure(failure) }
                return@whenUserInput
            }
            val updatedFields = stateValue.fields.map { field ->
                invalidFields.find { it.name == field.id }?.let { invalidField ->
                    field.copy(
                        isValid = false,
                        description = field.errorMessage(
                            originalMessage = invalidField.message,
                            replaceWithLocalMessage = replaceWithLocalMessage
                        )
                    )
                } ?: field
            }
            val firstInvalidFieldId = updatedFields.find { !it.isValid }?.id
            _state.update {
                UserInput(
                    stateValue.copy(
                        fields = updatedFields,
                        focusedFieldId = firstInvalidFieldId ?: stateValue.focusedFieldId,
                        submitAllowed = updatedFields.all { it.isValid },
                        submitting = false
                    )
                )
            }
            POLogger.info("Recovered after the failure: %s", failure)
            dispatch(DidFailToSubmitParameters(failure))
        }
    }

    // TODO: Delete this when backend localization is ready.
    private fun Field.errorMessage(
        originalMessage: String?,
        replaceWithLocalMessage: Boolean
    ): String? =
        if (replaceWithLocalMessage)
            when (parameter) {
                is Parameter.Text -> app.getString(R.string.po_native_apm_error_invalid_text)
                is Parameter.Digits -> app.getString(R.string.po_native_apm_error_invalid_number)
                is Parameter.PhoneNumber -> app.getString(R.string.po_native_apm_error_invalid_phone)
                is Parameter.Email -> app.getString(R.string.po_native_apm_error_invalid_email)
                is Parameter.Card -> null // TODO(v2): add new error string for card
                is Parameter.Otp -> when (parameter.subtype) {
                    Subtype.TEXT -> app.getString(R.string.po_native_apm_error_invalid_text)
                    Subtype.DIGITS -> app.getString(R.string.po_native_apm_error_invalid_number)
                    Subtype.UNKNOWN -> null
                }
                else -> null
            }
        else originalMessage

    //endregion

    //region Capture

    private suspend fun handlePendingCapture(
        stateValue: UserInputStateValue,
        parameterValues: PONativeAlternativePaymentMethodParameterValues?
    ) {
        POLogger.info("All payment parameters has been submitted.")
        dispatch(DidSubmitParameters(additionalParametersExpected = false))
        if (!configuration.paymentConfirmation.waitsConfirmation) {
            POLogger.info("Finished: did not wait for capture confirmation.")
            _completion.update { Success }
            return
        }
        val barcode = parameterValues?.customerActionBarcode?.let { barcode ->
            val size = 250.dpToPx(app)
            barcodeBitmapProvider.generate(
                barcode = barcode,
                width = size,
                height = size
            ).fold(
                onSuccess = { bitmap ->
                    Barcode(
                        type = barcode.type(),
                        bitmap = bitmap,
                        actionId = ActionId.SAVE_BARCODE,
                        confirmErrorActionId = ActionId.CONFIRM_SAVE_BARCODE_ERROR
                    )
                },
                onFailure = { failure ->
                    _completion.update { Failure(failure) }
                    return
                }
            )
        }
        val captureStateValue = stateValue.toCaptureStateValue(parameterValues, barcode)
        preloadAllImages(stateValue = captureStateValue)
        POLogger.info("Waiting for capture confirmation.")
        val additionalActionExpected = !captureStateValue.customerAction?.message.isNullOrBlank()
        dispatch(WillWaitForCaptureConfirmation(additionalActionExpected = additionalActionExpected))
        _state.update { Capturing(captureStateValue) }
        enableCapturingSecondaryAction()
        if (!additionalActionExpected || configuration.paymentConfirmation.confirmButton == null) {
            capture()
        }
    }

    private fun confirmPayment() {
        _state.whenCapturing { stateValue ->
            POLogger.info("User confirmed that required external action is complete.")
            dispatch(DidConfirmPayment)
            _state.update { Capturing(stateValue.copy(primaryActionId = null)) }
            capture()
        }
    }

    private fun capture() {
        if (captureStartTimestamp != 0L) {
            return
        }
        captureStartTimestamp = System.currentTimeMillis()
        enableCapturingProgressIndicator()
        interactorScope.launch {
            val iterator = captureRetryStrategy.iterator
            while (capturePassedTimestamp <= configuration.paymentConfirmation.timeoutSeconds * 1000) {
                val result = invoicesService.captureNativeAlternativePayment(
                    invoiceId = configuration.invoiceId,
                    gatewayConfigurationId = configuration.gatewayConfigurationId
                )
                POLogger.debug("Attempted to capture the payment.")
                if (isCaptureRetryable(result)) {
                    delay(iterator.next())
                    capturePassedTimestamp = System.currentTimeMillis() - captureStartTimestamp
                } else {
                    captureStartTimestamp = 0L
                    capturePassedTimestamp = 0L
                    result.onSuccess {
                        _state.whenCapturing { stateValue ->
                            handleCaptured(stateValue)
                        }
                    }.onFailure { failure ->
                        _completion.update { Failure(failure) }
                    }
                    return@launch
                }
            }
            captureStartTimestamp = 0L
            capturePassedTimestamp = 0L
            _completion.update {
                Failure(
                    ProcessOutResult.Failure(
                        code = Timeout(),
                        message = "Payment confirmation timed out."
                    )
                )
            }
        }
    }

    private fun isCaptureRetryable(
        result: ProcessOutResult<PONativeAlternativePaymentMethodCapture>
    ): Boolean = result.fold(
        onSuccess = { it.state != CAPTURED },
        onFailure = {
            val retryableCodes = listOf(
                NetworkUnreachable,
                Timeout(),
                Internal()
            )
            retryableCodes.contains(it.code)
        }
    )

    private fun handleCaptured(stateValue: CaptureStateValue) {
        POLogger.info("Success: capture confirmed.")
        if (!configuration.paymentConfirmation.waitsConfirmation) {
            _completion.update { Success }
            return
        }
        dispatch(DidCompletePayment)
        if (configuration.skipSuccessScreen) {
            _completion.update { Success }
        } else {
            _state.update { Captured(stateValue) }
            handler.postDelayed(delayInMillis = SUCCESS_DELAY_MS) {
                _completion.update { Success }
            }
        }
    }

    //endregion

    //region Images

    private suspend fun preloadAllImages(stateValue: CaptureStateValue) {
        coroutineScope {
            val deferredResults = mutableListOf<Deferred<ImageResult>>()
            stateValue.logoUrl?.let {
                deferredResults.add(async { preloadImage(it) })
            }
            stateValue.customerAction?.imageUrl?.let {
                deferredResults.add(async { preloadImage(it) })
            }
            deferredResults.awaitAll()
        }
    }

    private suspend fun preloadImage(url: String): ImageResult {
        val request = ImageRequest.Builder(app)
            .data(url)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build()
        return app.imageLoader.execute(request)
    }

    //endregion

    //region Features

    private val CancelButton?.disabledForMillis: Long
        get() = this?.disabledForSeconds?.let { it * 1000L } ?: 0

    private fun enableUserInputSecondaryAction() {
        handler.postDelayed(delayInMillis = configuration.cancelButton.disabledForMillis) {
            _state.whenUserInput { stateValue ->
                _state.update {
                    with(stateValue) {
                        UserInput(copy(secondaryAction = secondaryAction.copy(enabled = true)))
                    }
                }
            }
        }
    }

    private fun enableCapturingSecondaryAction() {
        handler.postDelayed(delayInMillis = configuration.paymentConfirmation.cancelButton.disabledForMillis) {
            _state.whenCapturing { stateValue ->
                _state.update {
                    with(stateValue) {
                        Capturing(copy(secondaryAction = secondaryAction.copy(enabled = true)))
                    }
                }
            }
        }
    }

    private fun enableCapturingProgressIndicator() {
        configuration.paymentConfirmation.showProgressIndicatorAfterSeconds?.let { afterSeconds ->
            handler.postDelayed(delayInMillis = afterSeconds * 1000L) {
                _state.whenCapturing { stateValue ->
                    _state.update { Capturing(stateValue.copy(withProgressIndicator = true)) }
                }
            }
        }
    }

    //endregion

    //region Save Barcode

    private fun saveBarcode() {
        _state.whenCapturing { stateValue ->
            stateValue.customerAction?.barcode?.run {
                interactorScope.launch {
                    when (Build.VERSION.SDK_INT) {
                        in Build.VERSION_CODES.M..Build.VERSION_CODES.P ->
                            _sideEffects.send(
                                PermissionRequest(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            )
                        else -> mediaStorageProvider
                            .saveImage(bitmap)
                            .onFailure { updateBarcodeState(isError = true) }
                    }
                }
            }
        }
    }

    private fun handlePermission(result: PermissionRequestResult) {
        when (result.permission) {
            Manifest.permission.WRITE_EXTERNAL_STORAGE ->
                if (result.isGranted) {
                    _state.whenCapturing { stateValue ->
                        stateValue.customerAction?.barcode?.run {
                            interactorScope.launch {
                                mediaStorageProvider
                                    .saveImage(bitmap)
                                    .onFailure { updateBarcodeState(isError = true) }
                            }
                        }
                    }
                } else {
                    updateBarcodeState(isError = true)
                }
        }
    }

    private fun updateBarcodeState(isError: Boolean) =
        _state.whenCapturing { stateValue ->
            val updatedStateValue = with(stateValue) {
                copy(
                    customerAction = customerAction?.copy(
                        barcode = customerAction.barcode?.copy(
                            isError = isError
                        )
                    )
                )
            }
            _state.update { Capturing(updatedStateValue) }
        }

    //endregion

    //region Dispatch Events

    private fun dispatch(event: PONativeAlternativePaymentEvent) {
        interactorScope.launch {
            eventDispatcher.send(event)
            POLogger.debug("Event has been sent: %s", event)
        }
    }

    private fun dispatchFailure() {
        interactorScope.launch {
            _completion.collect {
                if (it is Failure) {
                    POLogger.warn("%s", it.failure, attributes = logAttributes)
                    dispatch(DidFail(it.failure))
                }
            }
        }
    }

    //endregion

    private fun cancel() {
        _completion.update {
            Failure(
                ProcessOutResult.Failure(
                    code = Cancelled,
                    message = "Cancelled by the user with secondary cancel action."
                ).also { POLogger.info("Cancelled: %s", it) }
            )
        }
    }

    override fun clear() {
        handler.removeCallbacksAndMessages(null)
    }
}
