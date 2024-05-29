package com.processout.sdk.ui.napm

import android.app.Application
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.text.isDigitsOnly
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.ImageResult
import com.processout.sdk.R
import com.processout.sdk.api.dispatcher.napm.PODefaultNativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent.*
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodDefaultValuesRequest
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterType
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterType.*
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameterValues
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodState
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodState.*
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodTransactionDetails
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.POFailure.Code.*
import com.processout.sdk.core.POFailure.InvalidField
import com.processout.sdk.core.POFailure.ValidationCode
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import com.processout.sdk.core.retry.PORetryStrategy
import com.processout.sdk.ui.base.BaseInteractor
import com.processout.sdk.ui.core.state.POAvailableValue
import com.processout.sdk.ui.napm.NativeAlternativePaymentCompletion.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentInteractorState.*
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.Options
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class NativeAlternativePaymentInteractor(
    private val app: Application,
    private val invoiceId: String,
    private val gatewayConfigurationId: String,
    private val options: Options,
    private val invoicesService: POInvoicesService,
    private val captureRetryStrategy: PORetryStrategy,
    private val eventDispatcher: PODefaultNativeAlternativePaymentMethodEventDispatcher,
    private val logAttributes: Map<String, String>
) : BaseInteractor() {

    companion object {
        const val LOG_ATTRIBUTE_INVOICE_ID = "InvoiceId"
        const val LOG_ATTRIBUTE_GATEWAY_CONFIGURATION_ID = "GatewayConfigurationId"
    }

    private val _completion = MutableStateFlow<NativeAlternativePaymentCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow<NativeAlternativePaymentInteractorState>(Loading)
    val state = _state.asStateFlow()

    private var latestDefaultValuesRequest: PONativeAlternativePaymentMethodDefaultValuesRequest? = null

    //region Initialization

    init {
        POLogger.info("Starting native alternative payment.")
        dispatch(WillStart)
        dispatchFailure()
        collectDefaultValues()
        fetchTransactionDetails()
    }

    private fun fetchTransactionDetails() {
        interactorScope.launch {
            invoicesService.fetchNativeAlternativePaymentMethodTransactionDetails(
                invoiceId = invoiceId,
                gatewayConfigurationId = gatewayConfigurationId
            ).onSuccess { details ->
                with(details) {
                    handleState(
                        stateValue = toStateValue(),
                        paymentState = state,
                        parameters = parameters,
                        parameterValues = parameterValues
                    )
                }
            }.onFailure { failure ->
                POLogger.info("Failed to fetch transaction details: %s", failure)
                _completion.update { Failure(failure) }
            }
        }
    }

    private fun PONativeAlternativePaymentMethodTransactionDetails.toStateValue() =
        UserInputStateValue(
            invoice = invoice,
            gateway = gateway,
            fields = emptyList(),
            focusedFieldId = null,
            primaryActionId = ActionId.SUBMIT,
            secondaryActionId = ActionId.CANCEL,
            submitAllowed = true,
            submitting = false
        )

    //endregion

    private fun handleState(
        stateValue: UserInputStateValue,
        paymentState: PONativeAlternativePaymentMethodState?,
        parameters: List<PONativeAlternativePaymentMethodParameter>?,
        parameterValues: PONativeAlternativePaymentMethodParameterValues?
    ) {
        when (paymentState) {
            CUSTOMER_INPUT, null -> handleCustomerInput(stateValue, parameters)
            PENDING_CAPTURE -> handlePendingCapture(stateValue, parameterValues)
            CAPTURED -> handleCaptured(stateValue)
            FAILED -> _completion.update {
                Failure(
                    ProcessOutResult.Failure(
                        code = Generic(),
                        message = "Payment has failed."
                    ).also { POLogger.info("%s", it) }
                )
            }
        }
    }

    //region User Input

    private fun handleCustomerInput(
        stateValue: UserInputStateValue,
        parameters: List<PONativeAlternativePaymentMethodParameter>?
    ) {
        if (parameters.isNullOrEmpty()) {
            _completion.update {
                Failure(
                    ProcessOutResult.Failure(
                        code = Internal(),
                        message = "Input parameters is missing in response."
                    ).also { POLogger.error("%s", it, attributes = logAttributes) }
                )
            }
            return
        }
        if (failWithUnknownInputParameter(parameters)) {
            return
        }
        val fields = parameters.toFields()
        val focusedFieldId = fields.getOrNull(0)?.let { field ->
            if (field.type != SINGLE_SELECT) field.id else null
        }
        val updatedStateValue = stateValue.copy(
            fields = fields,
            focusedFieldId = focusedFieldId
        )
        val isLoading = _state.value is Loading
        if (eventDispatcher.subscribedForDefaultValuesRequest()) {
            _state.update {
                if (isLoading) {
                    Loaded(updatedStateValue)
                } else {
                    Submitted(updatedStateValue)
                }
            }
            requestDefaultValues(parameters)
        } else if (isLoading) {
            startUserInput(updatedStateValue)
        } else {
            continueUserInput(updatedStateValue)
        }
    }

    private fun failWithUnknownInputParameter(
        parameters: List<PONativeAlternativePaymentMethodParameter>
    ): Boolean {
        parameters.find { it.type() == UNKNOWN }?.let { parameter ->
            _completion.update {
                Failure(
                    ProcessOutResult.Failure(
                        code = Internal(),
                        message = "Unknown input parameter type: ${parameter.rawType}"
                    ).also { POLogger.error("%s", it, attributes = logAttributes) }
                )
            }
            return true
        }
        return false
    }

    private fun List<PONativeAlternativePaymentMethodParameter>.toFields() =
        map { parameter ->
            with(parameter) {
                val defaultValue = availableValues?.find { it.default == true }?.value ?: String()
                Field(
                    id = key,
                    value = TextFieldValue(
                        text = defaultValue,
                        selection = TextRange(defaultValue.length)
                    ),
                    availableValues = availableValues?.map {
                        POAvailableValue(
                            value = it.value,
                            text = it.displayName
                        )
                    },
                    type = type(),
                    length = length,
                    displayName = displayName,
                    description = null,
                    required = required,
                    isValid = true
                )
            }
        }

    private fun startUserInput(stateValue: UserInputStateValue) {
        _state.update { UserInput(stateValue) }

        // TODO
//        uiModel.secondaryAction?.let {
//            scheduleSecondaryActionEnabling(it) { enableSecondaryAction() }
//        }

        POLogger.info("Started. Waiting for payment parameters.")
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
        POLogger.info("Submitted. Waiting for additional payment parameters.")
        dispatch(DidSubmitParameters(additionalParametersExpected = true))
    }

    //endregion

    //region Default Values

    private fun requestDefaultValues(parameters: List<PONativeAlternativePaymentMethodParameter>) {
        interactorScope.launch {
            val request = PONativeAlternativePaymentMethodDefaultValuesRequest(
                gatewayConfigurationId = gatewayConfigurationId,
                invoiceId = invoiceId,
                parameters = parameters
            )
            latestDefaultValuesRequest = request
            eventDispatcher.send(request)
            POLogger.debug("Requested to provide default values for payment parameters: %s", request)
        }
    }

    private fun collectDefaultValues() {
        interactorScope.launch {
            eventDispatcher.defaultValuesResponse.collect { response ->
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
    }

    private fun UserInputStateValue.updateFieldValues(
        values: Map<String, String>
    ): UserInputStateValue {
        val updatedFields = fields.map { field ->
            values.entries.find { it.key == field.id }?.let {
                val value = field.length?.let { length ->
                    it.value.take(length)
                } ?: it.value
                field.copy(
                    value = TextFieldValue(
                        text = value,
                        selection = TextRange(value.length)
                    )
                )
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
            }
            is Dismiss -> POLogger.info("Dismissed: %s", event.failure)
        }
    }

    //region Update Field

    private fun updateFieldValue(id: String, value: TextFieldValue) {
        _state.whenUserInput { stateValue ->
            val previousValue = stateValue.fields.find { it.id == id }?.value ?: TextFieldValue()
            val isTextChanged = value.text != previousValue.text
            val updatedStateValue = stateValue.copy(
                fields = stateValue.fields.map { field ->
                    updatedField(id, value, field, isTextChanged)
                }
            )
            _state.update { UserInput(updatedStateValue) }
            if (isTextChanged) {
                POLogger.debug("Field is edited by the user: %s", id)
                dispatch(ParametersChanged)
                if (updatedStateValue.areAllFieldsValid()) {
                    _state.update { UserInput(updatedStateValue.copy(submitAllowed = true)) }
                }
            }
        }
    }

    private fun updatedField(
        id: String,
        value: TextFieldValue,
        field: Field,
        isTextChanged: Boolean
    ): Field {
        if (field.id != id) {
            return field
        }
        return if (isTextChanged) {
            field.copy(value = value, description = null, isValid = true)
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
            dispatch(WillSubmitParameters)
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
        val value = value.text
        if (required && value.isBlank()) {
            return invalidField(R.string.po_native_apm_error_required_parameter)
        }
        length?.let {
            if (value.length != it) {
                return InvalidField(
                    name = id,
                    message = app.resources.getQuantityString(
                        R.plurals.po_native_apm_error_invalid_length, it, it
                    )
                )
            }
        }
        when (type) {
            NUMERIC -> if (!value.isDigitsOnly())
                return invalidField(R.string.po_native_apm_error_invalid_number)
            EMAIL -> if (!Patterns.EMAIL_ADDRESS.matcher(value).matches())
                return invalidField(R.string.po_native_apm_error_invalid_email)
            PHONE -> if (!Patterns.PHONE.matcher(value).matches())
                return invalidField(R.string.po_native_apm_error_invalid_phone)
            else -> {}
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
                val parameters = mutableMapOf<String, String>()
                stateValue.fields.forEach {
                    parameters[it.id] = it.value.text
                }
                val request = PONativeAlternativePaymentMethodRequest(
                    invoiceId = invoiceId,
                    gatewayConfigurationId = gatewayConfigurationId,
                    parameters = parameters
                )
                invoicesService.initiatePayment(request)
                    .onSuccess { payment ->
                        with(payment) {
                            handleState(
                                stateValue = stateValue,
                                paymentState = state,
                                parameters = parameterDefinitions,
                                parameterValues = parameterValues
                            )
                        }
                    }
                    .onFailure { failure ->
                        handlePaymentFailure(
                            failure = failure,
                            replaceWithLocalMessage = true
                        )
                    }
            }
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
                        description = fieldErrorMessage(
                            originalMessage = invalidField.message,
                            replaceWithLocalMessage = replaceWithLocalMessage,
                            type = field.type
                        ),
                        isValid = false
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
    private fun fieldErrorMessage(
        originalMessage: String?,
        replaceWithLocalMessage: Boolean,
        type: ParameterType
    ): String? =
        if (replaceWithLocalMessage)
            when (type) {
                NUMERIC -> app.getString(R.string.po_native_apm_error_invalid_number)
                TEXT -> app.getString(R.string.po_native_apm_error_invalid_text)
                EMAIL -> app.getString(R.string.po_native_apm_error_invalid_email)
                PHONE -> app.getString(R.string.po_native_apm_error_invalid_phone)
                else -> null
            }
        else originalMessage

    //endregion

    //region Capture

    private fun handlePendingCapture(
        stateValue: UserInputStateValue,
        parameterValues: PONativeAlternativePaymentMethodParameterValues?
    ) {
        POLogger.info("All payment parameters has been submitted.")
        dispatch(DidSubmitParameters(additionalParametersExpected = false))
        if (!options.paymentConfirmation.waitsConfirmation) {
            POLogger.info("Finished. Did not wait for capture confirmation.")
            _completion.update { Success }
            return
        }
        interactorScope.launch {
            val captureStateValue = CaptureStateValue(
                paymentProviderName = parameterValues?.providerName,
                logoUrl = if (parameterValues?.providerName != null)
                    parameterValues.providerLogoUrl else stateValue.gateway.logoUrl,
                actionImageUrl = stateValue.gateway.customerActionImageUrl,
                actionMessage = parameterValues?.customerActionMessage
                    ?: stateValue.gateway.customerActionMessage,
                secondaryActionId = ActionId.CANCEL
            )
            POLogger.info("Waiting for capture confirmation.")
            dispatch(
                WillWaitForCaptureConfirmation(
                    additionalActionExpected = !captureStateValue.actionMessage.isNullOrBlank()
                )
            )
            preloadAllImages(
                stateValue = captureStateValue,
                coroutineScope = this@launch
            )
            _state.update { Capturing(captureStateValue) }

            // TODO: schedule enabling of progress indicator

            // TODO: schedule enabling of secondary action

            capture()
        }
    }

    private fun capture() {
        // TODO
    }

    private fun handleCaptured(stateValue: UserInputStateValue) {
        // TODO
    }

    //endregion

    //region Images

    private suspend fun preloadAllImages(
        stateValue: CaptureStateValue,
        coroutineScope: CoroutineScope
    ) {
        val deferredResults = mutableListOf<Deferred<ImageResult>>()
        stateValue.logoUrl?.let {
            deferredResults.add(coroutineScope.async { preloadImage(it) })
        }
        stateValue.actionImageUrl?.let {
            deferredResults.add(coroutineScope.async { preloadImage(it) })
        }
        deferredResults.awaitAll()
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

    private fun dispatch(event: PONativeAlternativePaymentMethodEvent) {
        interactorScope.launch {
            eventDispatcher.send(event)
            POLogger.debug("Event has been sent: %s", event)
        }
    }

    private fun dispatchFailure() {
        interactorScope.launch {
            _completion.collect {
                if (it is Failure) {
                    dispatch(DidFail(it.failure))
                }
            }
        }
    }
}
