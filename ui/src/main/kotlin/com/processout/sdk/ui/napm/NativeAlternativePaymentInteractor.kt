package com.processout.sdk.ui.napm

import android.app.Application
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
import com.processout.sdk.api.dispatcher.napm.PODefaultNativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent.*
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodDefaultValuesRequest
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.*
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterType
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterType.*
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterValue
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodState.*
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
import com.processout.sdk.core.util.POMarkdownUtils.escapedMarkdown
import com.processout.sdk.ui.base.BaseInteractor
import com.processout.sdk.ui.core.state.POAvailableValue
import com.processout.sdk.ui.napm.NativeAlternativePaymentCompletion.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent.Action
import com.processout.sdk.ui.napm.NativeAlternativePaymentInteractorState.*
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.Options
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.SecondaryAction
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.SecondaryAction.Cancel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class NativeAlternativePaymentInteractor(
    private val app: Application,
    private var invoiceId: String,
    private var gatewayConfigurationId: String,
    private val options: Options,
    private val invoicesService: POInvoicesService,
    private val captureRetryStrategy: PORetryStrategy,
    private val eventDispatcher: PODefaultNativeAlternativePaymentMethodEventDispatcher,
    private var logAttributes: Map<String, String> = logAttributes(
        invoiceId = invoiceId,
        gatewayConfigurationId = gatewayConfigurationId
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

    private val handler = Handler(Looper.getMainLooper())

    private var latestDefaultValuesRequest: PONativeAlternativePaymentMethodDefaultValuesRequest? = null

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
        fetchTransactionDetails()
    }

    fun start(
        invoiceId: String,
        gatewayConfigurationId: String
    ) {
        if (_state.value !is Idle) {
            return
        }
        this.invoiceId = invoiceId
        this.gatewayConfigurationId = gatewayConfigurationId
        logAttributes = logAttributes(
            invoiceId = invoiceId,
            gatewayConfigurationId = gatewayConfigurationId
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

    private fun fetchTransactionDetails() {
        interactorScope.launch {
            invoicesService.fetchNativeAlternativePaymentMethodTransactionDetails(
                invoiceId = invoiceId,
                gatewayConfigurationId = gatewayConfigurationId
            ).onSuccess { details ->
                with(details) {
                    handleState(
                        stateValue = toUserInputStateValue(),
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

    private fun handleState(
        stateValue: UserInputStateValue,
        paymentState: PONativeAlternativePaymentMethodState?,
        parameters: List<PONativeAlternativePaymentMethodParameter>?,
        parameterValues: PONativeAlternativePaymentMethodParameterValues?
    ) {
        when (paymentState) {
            CUSTOMER_INPUT, null -> handleCustomerInput(stateValue, parameters)
            PENDING_CAPTURE -> handlePendingCapture(stateValue.toCaptureStateValue(parameterValues))
            CAPTURED -> handleCaptured(stateValue.toCaptureStateValue(parameterValues))
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

    private fun PONativeAlternativePaymentMethodTransactionDetails.toUserInputStateValue() =
        UserInputStateValue(
            invoice = invoice,
            gateway = gateway,
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
        parameterValues: PONativeAlternativePaymentMethodParameterValues?
    ) = CaptureStateValue(
        paymentProviderName = parameterValues?.providerName,
        logoUrl = logoUrl(gateway, parameterValues),
        actionImageUrl = gateway.customerActionImageUrl,
        actionMessage = parameterValues?.customerActionMessage
            ?: gateway.customerActionMessage?.let { escapedMarkdown(it) },
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
        if (options.paymentConfirmation.hideGatewayDetails) {
            return null
        }
        return gateway.logoUrl
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
                    rawType = rawType,
                    type = type(),
                    length = length,
                    displayName = displayName,
                    description = null,
                    required = required,
                    isValid = true
                )
            }
        }

    private fun Field.toParameter() = PONativeAlternativePaymentMethodParameter(
        key = id,
        length = length,
        required = required,
        rawType = rawType,
        displayName = displayName,
        availableValues = availableValues?.map {
            ParameterValue(
                value = it.value,
                displayName = it.text,
                default = null
            )
        }
    )

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
                ActionId.CONFIRM_PAYMENT -> confirmPayment()
            }
            is ActionConfirmationRequested -> {
                POLogger.debug("Requested the user to confirm the action: %s", event.id)
                if (event.id == ActionId.CANCEL) {
                    dispatch(DidRequestCancelConfirmation)
                }
            }
            is Dismiss -> {
                POLogger.warn("Dismissed: %s", event.failure, attributes = logAttributes)
                dispatch(DidFail(event.failure))
            }
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
                updatedStateValue.fields.find { it.id == id }?.let {
                    dispatch(
                        ParametersChanged(
                            parameter = it.toParameter(),
                            value = it.value.text
                        )
                    )
                }
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
            dispatch(
                WillSubmitParameters(
                    parameters = stateValue.fields.map { it.toParameter() },
                    values = stateValue.fields.values()
                )
            )
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

    private fun List<Field>.values(): Map<String, String> {
        val values = mutableMapOf<String, String>()
        forEach {
            values[it.id] = it.value.text
        }
        return values
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
                val request = PONativeAlternativePaymentMethodRequest(
                    invoiceId = invoiceId,
                    gatewayConfigurationId = gatewayConfigurationId,
                    parameters = stateValue.fields.values()
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

    private fun handlePendingCapture(stateValue: CaptureStateValue) {
        POLogger.info("All payment parameters has been submitted.")
        dispatch(DidSubmitParameters(additionalParametersExpected = false))
        if (!options.paymentConfirmation.waitsConfirmation) {
            POLogger.info("Finished: did not wait for capture confirmation.")
            _completion.update { Success }
            return
        }
        interactorScope.launch {
            POLogger.info("Waiting for capture confirmation.")
            val additionalActionExpected = !stateValue.actionMessage.isNullOrBlank()
            dispatch(WillWaitForCaptureConfirmation(additionalActionExpected = additionalActionExpected))
            preloadAllImages(
                stateValue = stateValue,
                coroutineScope = this@launch
            )
            _state.update { Capturing(stateValue) }
            enableCapturingSecondaryAction()
            if (!additionalActionExpected || options.paymentConfirmation.primaryAction == null) {
                capture()
            }
        }
    }

    private fun confirmPayment() {
        // TODO
    }

    private fun capture() {
        if (captureStartTimestamp != 0L) {
            return
        }
        interactorScope.launch {
            captureStartTimestamp = System.currentTimeMillis()
            enableCapturingProgressIndicator()
            val iterator = captureRetryStrategy.iterator
            while (capturePassedTimestamp < options.paymentConfirmation.timeoutSeconds * 1000) {
                val result = invoicesService.captureNativeAlternativePayment(invoiceId, gatewayConfigurationId)
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
        if (!options.paymentConfirmation.waitsConfirmation) {
            _completion.update { Success }
            return
        }
        dispatch(DidCompletePayment)
        if (options.skipSuccessScreen) {
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

    //region Features

    private val SecondaryAction?.disabledForMillis: Long
        get() = when (this) {
            is Cancel -> disabledForSeconds * 1000L
            null -> 0
        }

    private fun enableUserInputSecondaryAction() {
        handler.postDelayed(delayInMillis = options.secondaryAction.disabledForMillis) {
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
        handler.postDelayed(delayInMillis = options.paymentConfirmation.secondaryAction.disabledForMillis) {
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
        options.paymentConfirmation.showProgressIndicatorAfterSeconds?.let { afterSeconds ->
            handler.postDelayed(delayInMillis = afterSeconds * 1000L) {
                _state.whenCapturing { stateValue ->
                    _state.update { Capturing(stateValue.copy(withProgressIndicator = true)) }
                }
            }
        }
    }

    //endregion

    //region Dispatch Events

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

    fun onCleared() {
        handler.removeCallbacksAndMessages(null)
    }
}
