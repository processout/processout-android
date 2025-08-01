@file:Suppress("SameParameterValue")

package com.processout.sdk.ui.napm

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
import com.processout.sdk.R
import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentAuthorizationRequest
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentSubmitData
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentSubmitData.Parameter.Companion.phoneNumber
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentSubmitData.Parameter.Companion.string
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentTokenizationRequest
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.api.model.response.POImageResource
import com.processout.sdk.api.model.response.napm.v2.*
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentAuthorizationResponse.Invoice
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentElement.Form.Parameter
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentElement.Form.Parameter.Otp.Subtype
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentState.*
import com.processout.sdk.api.service.POCustomerTokensService
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
import com.processout.sdk.ui.core.component.stepper.POStepper
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.napm.NativeAlternativePaymentCompletion.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent.Action
import com.processout.sdk.ui.napm.NativeAlternativePaymentInteractorState.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentSideEffect.PermissionRequest
import com.processout.sdk.ui.napm.NativeAlternativePaymentSideEffect.Redirect
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.CancelButton
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.Flow
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.Flow.Authorization
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.Flow.Tokenization
import com.processout.sdk.ui.napm.delegate.v2.NativeAlternativePaymentDefaultValuesRequest
import com.processout.sdk.ui.napm.delegate.v2.NativeAlternativePaymentDefaultValuesResponse
import com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentEvent
import com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentEvent.*
import com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentParameterValue
import com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentParameterValue.Value
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
import java.util.UUID

internal class NativeAlternativePaymentInteractor(
    private val app: Application,
    private var configuration: PONativeAlternativePaymentConfiguration,
    private val invoicesService: POInvoicesService,
    private val customerTokensService: POCustomerTokensService,
    private val barcodeBitmapProvider: BarcodeBitmapProvider,
    private val mediaStorageProvider: MediaStorageProvider,
    private val captureRetryStrategy: PORetryStrategy,
    private val eventDispatcher: POEventDispatcher = POEventDispatcher,
    private var logAttributes: Map<String, String> = logAttributes(configuration.flow)
) : BaseInteractor() {

    private companion object {
        fun logAttributes(flow: Flow): Map<String, String> =
            when (flow) {
                is Authorization -> mapOf(
                    POLogAttribute.INVOICE_ID to flow.invoiceId,
                    POLogAttribute.GATEWAY_CONFIGURATION_ID to flow.gatewayConfigurationId
                )
                is Tokenization -> mapOf(
                    POLogAttribute.CUSTOMER_ID to flow.customerId,
                    POLogAttribute.CUSTOMER_TOKEN_ID to flow.customerTokenId,
                    POLogAttribute.GATEWAY_CONFIGURATION_ID to flow.gatewayConfigurationId
                )
            }
    }

    private val _completion = MutableStateFlow<NativeAlternativePaymentCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow<NativeAlternativePaymentInteractorState>(Idle)
    val state = _state.asStateFlow()

    private val _sideEffects = Channel<NativeAlternativePaymentSideEffect>()
    val sideEffects = _sideEffects.receiveAsFlow()

    private val handler = Handler(Looper.getMainLooper())

    private var paymentState: PONativeAlternativePaymentState = UNKNOWN
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
        logAttributes = logAttributes(configuration.flow)
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
                is Authorization -> fetchAuthorizationDetails(flow)
                is Tokenization -> fetchTokenizationDetails(flow)
            }
        }
    }

    private suspend fun fetchAuthorizationDetails(flow: Authorization) {
        val request = PONativeAlternativePaymentAuthorizationRequest(
            invoiceId = flow.invoiceId,
            gatewayConfigurationId = flow.gatewayConfigurationId,
            source = flow.customerTokenId
        )
        invoicesService.authorize(request)
            .onSuccess { response ->
                handlePaymentState(
                    stateValue = initNextStepStateValue(
                        paymentMethod = response.paymentMethod,
                        invoice = response.invoice
                    ),
                    paymentState = response.state,
                    elements = response.elements,
                    redirect = response.redirect
                )
            }.onFailure { failure ->
                POLogger.info("Failed to fetch authorization details: %s", failure)
                _completion.update { Failure(failure) }
            }
    }

    private suspend fun fetchTokenizationDetails(flow: Tokenization) {
        val request = PONativeAlternativePaymentTokenizationRequest(
            customerId = flow.customerId,
            customerTokenId = flow.customerTokenId,
            gatewayConfigurationId = flow.gatewayConfigurationId
        )
        customerTokensService.tokenize(request)
            .onSuccess { response ->
                handlePaymentState(
                    stateValue = initNextStepStateValue(
                        paymentMethod = response.paymentMethod,
                        invoice = null
                    ),
                    paymentState = response.state,
                    elements = response.elements,
                    redirect = response.redirect
                )
            }.onFailure { failure ->
                POLogger.info("Failed to fetch tokenization details: %s", failure)
                _completion.update { Failure(failure) }
            }
    }

    private suspend fun initNextStepStateValue(
        paymentMethod: PONativeAlternativePaymentMethodDetails,
        invoice: Invoice?,
    ): NextStepStateValue {
        preloadImages(resources = listOf(paymentMethod.logo))
        return NextStepStateValue(
            uuid = UUID.randomUUID().toString(),
            paymentMethod = paymentMethod,
            invoice = invoice,
            redirect = null,
            elements = emptyList(),
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
    }

    private suspend fun handlePaymentState(
        stateValue: NextStepStateValue,
        paymentState: PONativeAlternativePaymentState,
        elements: List<PONativeAlternativePaymentElement>?,
        redirect: PONativeAlternativePaymentRedirect?
    ) {
        this.paymentState = paymentState
        val mappedElements = elements?.map() ?: emptyList()
        preloadImages(resources = mappedElements.images())
        when (paymentState) {
            NEXT_STEP_REQUIRED -> handleNextStep(stateValue, mappedElements, redirect)
            PENDING -> handlePending(stateValue, mappedElements)
            SUCCESS -> handleSuccess(
                stateValue.toPendingStateValue(
                    uuid = UUID.randomUUID().toString(),
                    elements = mappedElements
                )
            )
            UNKNOWN -> {
                val failure = ProcessOutResult.Failure(
                    code = Internal(),
                    message = "Unsupported payment state."
                )
                POLogger.error(
                    message = "%s", failure,
                    attributes = logAttributes
                )
                _completion.update { Failure(failure) }
            }
        }
    }

    //region Next Step

    private fun handleNextStep(
        stateValue: NextStepStateValue,
        elements: List<Element>,
        redirect: PONativeAlternativePaymentRedirect?
    ) {
        val parameters = elements.flatMap {
            if (it is Element.Form) it.form.parameterDefinitions else emptyList()
        }
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
        val updatedStateValue = stateValue.copy(
            uuid = UUID.randomUUID().toString(),
            redirect = redirect,
            elements = elements,
            fields = fields,
            focusedFieldId = fields.firstFocusableFieldId()
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

    private suspend fun List<PONativeAlternativePaymentElement>.map(): List<Element> =
        mapNotNull { element ->
            when (element) {
                is PONativeAlternativePaymentElement.Form ->
                    Element.Form(form = element)
                is PONativeAlternativePaymentElement.CustomerInstruction ->
                    element.instruction.map()?.let {
                        Element.Instruction(instruction = it)
                    }
                is PONativeAlternativePaymentElement.CustomerInstructionGroup ->
                    Element.InstructionGroup(
                        label = element.label,
                        instructions = element.instructions.mapNotNull { it.map() }
                    )
                PONativeAlternativePaymentElement.Unknown -> {
                    // TODO(v2)
                    null
                }
            }
        }

    private suspend fun PONativeAlternativePaymentCustomerInstruction.map(): Instruction? =
        when (this) {
            is PONativeAlternativePaymentCustomerInstruction.Message ->
                Instruction.Message(
                    label = label,
                    value = value
                )
            is PONativeAlternativePaymentCustomerInstruction.Image ->
                Instruction.Image(value = value)
            is PONativeAlternativePaymentCustomerInstruction.Barcode -> {
                val size = 250.dpToPx(app)
                barcodeBitmapProvider.generate(
                    barcode = value,
                    width = size,
                    height = size
                ).fold(
                    onSuccess = { bitmap ->
                        Instruction.Barcode(
                            type = value.type(),
                            bitmap = bitmap,
                            actionId = ActionId.SAVE_BARCODE,
                            confirmErrorActionId = ActionId.CONFIRM_SAVE_BARCODE_ERROR
                        )
                    },
                    onFailure = { failure ->
                        _completion.update { Failure(failure) }
                        null
                    }
                )
            }
            PONativeAlternativePaymentCustomerInstruction.Unknown -> {
                // TODO(v2)
                null
            }
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
                is Parameter.Bool -> FieldValue.Text(TextFieldValue(text = "false"))
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

    private fun startNextStep(stateValue: NextStepStateValue) {
        _state.update { NextStep(stateValue) }
        enableNextStepSecondaryAction()
        POLogger.info("Started: waiting for payment parameters.")
        dispatch(DidStart)
    }

    private fun continueNextStep(stateValue: NextStepStateValue) {
        _state.update {
            NextStep(
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
                gatewayConfigurationId = when (val flow = configuration.flow) {
                    is Authorization -> flow.gatewayConfigurationId
                    is Tokenization -> flow.gatewayConfigurationId
                },
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
                    startNextStep(stateValue.updateFieldValues(response.defaultValues))
                }
                _state.whenSubmitted { stateValue ->
                    continueNextStep(stateValue.updateFieldValues(response.defaultValues))
                }
            }
        }
    }

    private fun NextStepStateValue.updateFieldValues(
        values: Map<String, PONativeAlternativePaymentParameterValue>
    ): NextStepStateValue {
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
                                    text = if (field.parameter.dialingCodes.any { it.regionCode == value.regionCode })
                                        value.regionCode else String()
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
                ActionId.DONE -> _completion.update { Success }
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
            is RedirectResult -> handleRedirect(event.result)
            is Dismiss -> {
                POLogger.info("Dismissed: %s", event.failure)
                dispatch(DidFail(event.failure, paymentState))
            }
        }
    }

    //region Update Field

    private fun updateFieldValue(id: String, value: FieldValue) {
        _state.whenNextStep { stateValue ->
            val previousValue = stateValue.fields.find { it.id == id }?.value ?: FieldValue.Text()
            val isTextChanged = !value.isTextEquals(previousValue)
            val updatedStateValue = stateValue.copy(
                fields = stateValue.fields.map { field ->
                    updatedField(id, value, field, isTextChanged)
                }
            )
            _state.update { NextStep(updatedStateValue) }
            if (isTextChanged) {
                POLogger.debug("Field is edited by the user: %s", id)
                updatedStateValue.fields.find { it.id == id }?.let {
                    dispatch(ParametersChanged(it.parameter))
                }
                if (updatedStateValue.areAllFieldsValid()) {
                    _state.update { NextStep(updatedStateValue.copy(submitAllowed = true)) }
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
            _state.whenNextStep { stateValue ->
                _state.update {
                    NextStep(stateValue.copy(focusedFieldId = id))
                }
            }
        }
    }

    //endregion

    //region Submit & Validation

    private fun submit() {
        _state.whenNextStep { stateValue ->
            if (stateValue.redirect != null) {
                redirect(
                    stateValue = stateValue,
                    redirectUrl = stateValue.redirect.url
                )
                return@whenNextStep
            }
            POLogger.info("Will submit payment parameters.")
            dispatch(WillSubmitParameters(parameters = stateValue.fields.map { it.parameter }))
            val invalidFields = stateValue.fields.mapNotNull { it.validate() }
            if (invalidFields.isNotEmpty()) {
                val failure = ProcessOutResult.Failure(
                    code = Validation(ValidationCode.general),
                    message = "Invalid fields.",
                    invalidFields = invalidFields
                )
                handlePaymentFailure(failure)
                return@whenNextStep
            }
            _state.update {
                NextStep(
                    stateValue.copy(
                        submitAllowed = true,
                        submitting = true
                    )
                )
            }
            when (val flow = configuration.flow) {
                is Authorization -> authorize(flow)
                is Tokenization -> tokenize(flow)
            }
        }
    }

    private fun redirect(
        stateValue: NextStepStateValue,
        redirectUrl: String
    ) {
        val returnUrl = configuration.returnUrl
        if (returnUrl.isNullOrBlank()) {
            val failure = ProcessOutResult.Failure(
                code = Generic(),
                message = "Return URL is missing in configuration during redirect flow."
            )
            POLogger.warn(
                message = "Failed redirect: %s", failure,
                attributes = logAttributes
            )
            _completion.update { Failure(failure) }
            return
        }
        _state.update {
            NextStep(
                stateValue.copy(
                    submitAllowed = true,
                    submitting = true
                )
            )
        }
        interactorScope.launch {
            _sideEffects.send(
                Redirect(
                    redirectUrl = redirectUrl,
                    returnUrl = returnUrl
                )
            )
        }
    }

    private fun handleRedirect(result: ProcessOutResult<POAlternativePaymentMethodResponse>) {
        result.onSuccess {
            when (val flow = configuration.flow) {
                is Authorization -> authorize(flow)
                is Tokenization -> tokenize(flow)
            }
        }.onFailure { failure ->
            _completion.update { Failure(failure) }
        }
    }

    private fun Field.validate(): InvalidField? {
        when (parameter) {
            is Parameter.PhoneNumber -> if (value is FieldValue.PhoneNumber) {
                val dialingCode = parameter.dialingCodes
                    .find { it.regionCode == value.regionCode.text }?.value
                val number = value.number.text
                if (required && (dialingCode.isNullOrBlank() || number.isBlank())) {
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
                val maxLength = maxLength
                if (maxLength != null && value.length > maxLength) {
                    return if (minLength != null && minLength == maxLength) {
                        InvalidField(
                            name = id,
                            message = app.resources.getQuantityString(
                                R.plurals.po_native_apm_error_invalid_length, maxLength, maxLength
                            )
                        )
                    } else {
                        InvalidField(
                            name = id,
                            message = app.resources.getQuantityString(
                                R.plurals.po_native_apm_error_invalid_max_length, maxLength, maxLength
                            )
                        )
                    }
                }
                val minLength = minLength
                if (minLength != null && value.length < minLength) {
                    return if (maxLength != null && minLength == maxLength) {
                        InvalidField(
                            name = id,
                            message = app.resources.getQuantityString(
                                R.plurals.po_native_apm_error_invalid_length, minLength, minLength
                            )
                        )
                    } else {
                        InvalidField(
                            name = id,
                            message = app.resources.getQuantityString(
                                R.plurals.po_native_apm_error_invalid_min_length, minLength, minLength
                            )
                        )
                    }
                }
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

    private fun NextStepStateValue.areAllFieldsValid() = fields.all { it.isValid }

    private fun authorize(flow: Authorization) {
        _state.whenNextStep { stateValue ->
            interactorScope.launch {
                val request = PONativeAlternativePaymentAuthorizationRequest(
                    invoiceId = flow.invoiceId,
                    gatewayConfigurationId = flow.gatewayConfigurationId,
                    submitData = PONativeAlternativePaymentSubmitData(
                        parameters = stateValue.fields.values()
                    )
                )
                invoicesService.authorize(request)
                    .onSuccess { response ->
                        handlePaymentState(
                            stateValue = stateValue,
                            paymentState = response.state,
                            elements = response.elements,
                            redirect = response.redirect
                        )
                    }.onFailure { failure ->
                        handlePaymentFailure(failure)
                    }
            }
        }
    }

    private fun tokenize(flow: Tokenization) {
        _state.whenNextStep { stateValue ->
            interactorScope.launch {
                val request = PONativeAlternativePaymentTokenizationRequest(
                    customerId = flow.customerId,
                    customerTokenId = flow.customerTokenId,
                    gatewayConfigurationId = flow.gatewayConfigurationId,
                    submitData = PONativeAlternativePaymentSubmitData(
                        parameters = stateValue.fields.values()
                    )
                )
                customerTokensService.tokenize(request)
                    .onSuccess { response ->
                        handlePaymentState(
                            stateValue = stateValue,
                            paymentState = response.state,
                            elements = response.elements,
                            redirect = response.redirect
                        )
                    }.onFailure { failure ->
                        handlePaymentFailure(failure)
                    }
            }
        }
    }

    private fun List<Field>.values() =
        associate { field ->
            field.id to when (val value = field.value) {
                is FieldValue.Text -> string(value = value.value.text)
                is FieldValue.PhoneNumber -> {
                    val dialingCode = when (field.parameter) {
                        is Parameter.PhoneNumber -> field.parameter.dialingCodes
                            .find { it.regionCode == value.regionCode.text }
                            ?.value ?: String()
                        else -> String()
                    }
                    phoneNumber(
                        dialingCode = dialingCode,
                        number = value.number.text
                    )
                }
            }
        }

    //endregion

    //region Handle Failure

    private fun handlePaymentFailure(failure: ProcessOutResult.Failure) {
        _state.whenNextStep { stateValue ->
            val invalidFields = failure.invalidFields
            if (invalidFields.isNullOrEmpty()) {
                POLogger.info("Unrecoverable payment failure: %s", failure)
                _completion.update { Failure(failure) }
                return@whenNextStep
            }
            val updatedFields = stateValue.fields.map { field ->
                invalidFields.find { it.name == field.id }?.let { invalidField ->
                    field.copy(
                        isValid = false,
                        description = invalidField.message
                    )
                } ?: field
            }
            val firstInvalidFieldId = updatedFields.find { !it.isValid }?.id
            _state.update {
                NextStep(
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

    //endregion

    //region Pending

    private fun handlePending(
        stateValue: NextStepStateValue,
        elements: List<Element>
    ) {
        POLogger.info("All payment parameters has been submitted.")
        dispatch(DidSubmitParameters(additionalParametersExpected = false))
        POLogger.info("Waiting for payment confirmation.")
        dispatch(WillWaitForPaymentConfirmation)
        val pendingStateValue = stateValue.toPendingStateValue(
            uuid = UUID.randomUUID().toString(),
            elements = elements
        )
        _state.update { Pending(pendingStateValue) }
        enablePendingSecondaryAction()
        if (pendingStateValue.elements.isNullOrEmpty() ||
            configuration.paymentConfirmation.confirmButton == null
        ) {
            capture()
        }
    }

    private fun NextStepStateValue.toPendingStateValue(
        uuid: String,
        elements: List<Element>
    ) = PendingStateValue(
        uuid = uuid,
        paymentMethod = paymentMethod,
        invoice = invoice,
        stepper = null,
        elements = elements,
        primaryActionId = ActionId.CONFIRM_PAYMENT,
        secondaryAction = NativeAlternativePaymentInteractorState.Action(
            id = ActionId.CANCEL,
            enabled = false
        )
    )

    private fun confirmPayment() {
        POLogger.info("User confirmed that required external action is complete.")
        dispatch(DidConfirmPayment)
        capture()
    }

    private fun capture() {
        if (captureStartTimestamp != 0L) {
            return
        }
        updateStepper(activeStepIndex = 1)
        captureStartTimestamp = System.currentTimeMillis()
        interactorScope.launch {
            val iterator = captureRetryStrategy.iterator
            while (capturePassedTimestamp <= configuration.paymentConfirmation.timeoutSeconds * 1000) {
                val result = when (val flow = configuration.flow) {
                    is Authorization -> invoicesService.authorize(
                        request = PONativeAlternativePaymentAuthorizationRequest(
                            invoiceId = flow.invoiceId,
                            gatewayConfigurationId = flow.gatewayConfigurationId
                        )
                    ).map()
                    is Tokenization -> customerTokensService.tokenize(
                        request = PONativeAlternativePaymentTokenizationRequest(
                            customerId = flow.customerId,
                            customerTokenId = flow.customerTokenId,
                            gatewayConfigurationId = flow.gatewayConfigurationId
                        )
                    ).map()
                }
                POLogger.debug("Attempted to confirm the payment.")
                if (isCaptureRetryable(result)) {
                    delay(iterator.next())
                    capturePassedTimestamp = System.currentTimeMillis() - captureStartTimestamp
                } else {
                    captureStartTimestamp = 0L
                    capturePassedTimestamp = 0L
                    result.onSuccess {
                        _state.whenPending { stateValue ->
                            handleSuccess(
                                stateValue.copy(
                                    uuid = UUID.randomUUID().toString(),
                                    elements = it.elements
                                )
                            )
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

    private fun updateStepper(activeStepIndex: Int) {
        _state.whenPending { stateValue ->
            _state.update {
                val steps = listOf(
                    POStepper.Step(title = app.getString(R.string.po_native_apm_payment_confirmation_step1_title)),
                    POStepper.Step(
                        title = app.getString(R.string.po_native_apm_payment_confirmation_step2_title),
                        countdownTimerDescription = POStepper.Step.CountdownTimerText(
                            textFormat = app.getString(R.string.po_native_apm_payment_confirmation_step2_description_format),
                            timeoutSeconds = configuration.paymentConfirmation.timeoutSeconds
                        )
                    )
                )
                Pending(
                    stateValue.copy(
                        uuid = UUID.randomUUID().toString(),
                        stepper = Stepper(
                            steps = POImmutableList(steps),
                            activeStepIndex = activeStepIndex
                        ),
                        elements = if (configuration.paymentConfirmation.confirmButton == null)
                            stateValue.elements else null,
                        primaryActionId = null
                    )
                )
            }
        }
    }

    @JvmName(name = "mapFromAuthorizationResult")
    private suspend fun ProcessOutResult<PONativeAlternativePaymentAuthorizationResponse>.map() =
        fold(
            onSuccess = {
                ProcessOutResult.Success(
                    ProcessingResponse(
                        state = it.state,
                        elements = it.elements?.map()
                    )
                )
            },
            onFailure = { it }
        )

    @JvmName(name = "mapFromTokenizationResult")
    private suspend fun ProcessOutResult<PONativeAlternativePaymentTokenizationResponse>.map() =
        fold(
            onSuccess = {
                ProcessOutResult.Success(
                    ProcessingResponse(
                        state = it.state,
                        elements = it.elements?.map()
                    )
                )
            },
            onFailure = { it }
        )

    private fun isCaptureRetryable(
        result: ProcessOutResult<ProcessingResponse>
    ): Boolean = result.fold(
        onSuccess = { it.state != SUCCESS },
        onFailure = {
            val retryableCodes = listOf(
                NetworkUnreachable,
                Timeout(),
                Internal()
            )
            retryableCodes.contains(it.code)
        }
    )

    private fun handleSuccess(stateValue: PendingStateValue) {
        POLogger.info("Success: payment completed.")
        dispatch(DidCompletePayment)
        val successConfiguration = configuration.success
        if (successConfiguration == null) {
            _completion.update { Success }
        } else {
            _state.update {
                Completed(stateValue.copy(primaryActionId = ActionId.DONE))
            }
            val displayDurationSeconds = if (stateValue.elements.isNullOrEmpty())
                successConfiguration.displayDurationSeconds
            else successConfiguration.extendedDisplayDurationSeconds
            handler.postDelayed(delayInMillis = displayDurationSeconds * 1000L) {
                _completion.update { Success }
            }
        }
    }

    //endregion

    //region Images

    private suspend fun preloadImages(resources: List<POImageResource>) {
        coroutineScope {
            val urls = resources.flatMap { it.urls() }
            val deferredResults = urls.map { url ->
                async { preloadImage(url) }
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

    private fun POImageResource.urls(): List<String> {
        val urls = mutableListOf(lightUrl.raster)
        darkUrl?.raster?.let { urls.add(it) }
        return urls
    }

    private fun List<Element>.images(): List<POImageResource> =
        flatMap { element ->
            when (element) {
                is Element.Instruction -> when (element.instruction) {
                    is Instruction.Image -> listOf(element.instruction.value)
                    else -> emptyList()
                }
                is Element.InstructionGroup -> element.instructions.mapNotNull { instruction ->
                    when (instruction) {
                        is Instruction.Image -> instruction.value
                        else -> null
                    }
                }
                else -> emptyList()
            }
        }

    //endregion

    //region Features

    private val CancelButton?.disabledForMillis: Long
        get() = this?.disabledForSeconds?.let { it * 1000L } ?: 0

    private fun enableNextStepSecondaryAction() {
        handler.postDelayed(delayInMillis = configuration.cancelButton.disabledForMillis) {
            _state.whenNextStep { stateValue ->
                _state.update {
                    with(stateValue) {
                        NextStep(copy(secondaryAction = secondaryAction.copy(enabled = true)))
                    }
                }
            }
        }
    }

    private fun enablePendingSecondaryAction() {
        handler.postDelayed(delayInMillis = configuration.paymentConfirmation.cancelButton.disabledForMillis) {
            _state.whenPending { stateValue ->
                _state.update {
                    with(stateValue) {
                        Pending(copy(secondaryAction = secondaryAction.copy(enabled = true)))
                    }
                }
            }
        }
    }

    //endregion

    //region Save Barcode

    // TODO(v2): use barcode ID, also inspect instructions group
    private fun saveBarcode() {
        _state.whenNextStep { stateValue ->
            val instructions = stateValue.elements.mapNotNull {
                if (it is Element.Instruction) it else null
            }
            instructions.forEach {
                if (it.instruction is Instruction.Barcode) {
                    saveBarcode(barcode = it.instruction)
                    return@whenNextStep
                }
            }
        }
        _state.whenPending { stateValue ->
            val instructions = stateValue.elements?.mapNotNull {
                if (it is Element.Instruction) it else null
            }
            instructions?.forEach {
                if (it.instruction is Instruction.Barcode) {
                    saveBarcode(barcode = it.instruction)
                    return@whenPending
                }
            }
        }
    }

    private fun saveBarcode(barcode: Instruction.Barcode) {
        interactorScope.launch {
            when (Build.VERSION.SDK_INT) {
                in Build.VERSION_CODES.M..Build.VERSION_CODES.P ->
                    _sideEffects.send(
                        PermissionRequest(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    )
                else -> mediaStorageProvider
                    .saveImage(barcode.bitmap)
                    .onFailure { updateBarcodeState(isError = true) }
            }
        }
    }

    // TODO(v2): use barcode ID, also inspect instructions group
    private fun handlePermission(result: PermissionRequestResult) {
        when (result.permission) {
            Manifest.permission.WRITE_EXTERNAL_STORAGE ->
                if (result.isGranted) {
                    _state.whenNextStep { stateValue ->
                        val instructions = stateValue.elements.mapNotNull {
                            if (it is Element.Instruction) it else null
                        }
                        instructions.forEach {
                            if (it.instruction is Instruction.Barcode) {
                                interactorScope.launch {
                                    mediaStorageProvider
                                        .saveImage(it.instruction.bitmap)
                                        .onFailure { updateBarcodeState(isError = true) }
                                }
                                return@whenNextStep
                            }
                        }
                    }
                    _state.whenPending { stateValue ->
                        val instructions = stateValue.elements?.mapNotNull {
                            if (it is Element.Instruction) it else null
                        }
                        instructions?.forEach {
                            if (it.instruction is Instruction.Barcode) {
                                interactorScope.launch {
                                    mediaStorageProvider
                                        .saveImage(it.instruction.bitmap)
                                        .onFailure { updateBarcodeState(isError = true) }
                                }
                                return@whenPending
                            }
                        }
                    }
                } else {
                    updateBarcodeState(isError = true)
                }
        }
    }

    // TODO(v2): use barcode ID, also inspect instructions group
    private fun updateBarcodeState(isError: Boolean) {
        _state.whenNextStep { stateValue ->
            val updatedStateValue = stateValue.copy(
                elements = stateValue.elements.updateBarcodeState(isError)
            )
            _state.update { NextStep(updatedStateValue) }
        }
        _state.whenPending { stateValue ->
            val updatedStateValue = stateValue.copy(
                elements = stateValue.elements?.updateBarcodeState(isError)
            )
            _state.update { Pending(updatedStateValue) }
        }
    }

    private fun List<Element>.updateBarcodeState(isError: Boolean) =
        map { element ->
            when (element) {
                is Element.Instruction -> {
                    when (element.instruction) {
                        is Instruction.Barcode -> Element.Instruction(
                            instruction = element.instruction.copy(
                                isError = isError
                            )
                        )
                        else -> element
                    }
                }
                else -> element
            }
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
                    dispatch(DidFail(it.failure, paymentState))
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

    private data class ProcessingResponse(
        val state: PONativeAlternativePaymentState,
        val elements: List<Element>?
    )
}
