package com.processout.sdk.ui.nativeapm

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.annotation.StringRes
import androidx.core.os.postDelayed
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.ImageResult
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.dispatcher.PODefaultEventDispatchers
import com.processout.sdk.api.dispatcher.napm.PODefaultNativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent.*
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodDefaultValuesRequest
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.*
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterType
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterType.*
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodState.*
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.POFailure.Code.*
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogAttribute
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.retry.PORetryStrategy
import com.processout.sdk.core.retry.PORetryStrategy.Exponential
import com.processout.sdk.core.util.POMarkdownUtils.escapedMarkdown
import com.processout.sdk.ui.nativeapm.NativeAlternativePaymentMethodUiState.*
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodConfiguration.*
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodConfiguration.Options.Companion.DEFAULT_PAYMENT_CONFIRMATION_TIMEOUT_SECONDS
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodConfiguration.Options.Companion.MAX_PAYMENT_CONFIRMATION_TIMEOUT_SECONDS
import com.processout.sdk.ui.shared.model.InputParameter
import com.processout.sdk.ui.shared.view.button.POButton
import com.processout.sdk.ui.shared.view.input.Input
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.NumberFormat
import java.util.Currency
import java.util.concurrent.TimeUnit

internal class NativeAlternativePaymentMethodViewModel private constructor(
    private val app: Application,
    private val gatewayConfigurationId: String,
    private val invoiceId: String,
    private val invoicesService: POInvoicesService,
    private val eventDispatcher: PODefaultNativeAlternativePaymentMethodEventDispatcher,
    private val captureRetryStrategy: PORetryStrategy,
    val options: Options,
    val logAttributes: Map<String, String>
) : ViewModel() {

    class Factory(
        private val app: Application,
        private val gatewayConfigurationId: String,
        private val invoiceId: String,
        private val options: Options
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            with(ProcessOut.instance) {
                NativeAlternativePaymentMethodViewModel(
                    app = app,
                    gatewayConfigurationId = gatewayConfigurationId,
                    invoiceId = invoiceId,
                    invoicesService = invoices,
                    eventDispatcher = PODefaultEventDispatchers.defaultNativeAlternativePaymentMethod,
                    captureRetryStrategy = Exponential(
                        maxRetries = Int.MAX_VALUE,
                        initialDelay = 150,
                        minDelay = 3 * 1000,
                        maxDelay = 90 * 1000,
                        factor = 1.45
                    ),
                    options = options.validate(),
                    logAttributes = mapOf(
                        POLogAttribute.INVOICE_ID to invoiceId,
                        POLogAttribute.GATEWAY_CONFIGURATION_ID to gatewayConfigurationId
                    )
                )
            } as T

        private fun Options.validate() = copy(
            paymentConfirmationTimeoutSeconds =
            if (paymentConfirmationTimeoutSeconds in 0..MAX_PAYMENT_CONFIRMATION_TIMEOUT_SECONDS)
                paymentConfirmationTimeoutSeconds else DEFAULT_PAYMENT_CONFIRMATION_TIMEOUT_SECONDS
        )
    }

    private val _uiState = MutableStateFlow<NativeAlternativePaymentMethodUiState>(Loading)
    val uiState = _uiState.asStateFlow()

    var animateViewTransition = true

    private var captureStartTimestamp = 0L
    private var capturePassedTimestamp = 0L

    private val handler by lazy { Handler(Looper.getMainLooper()) }

    private val defaultValuesRequests = mutableSetOf<PONativeAlternativePaymentMethodDefaultValuesRequest>()

    init {
        POLogger.info("Starting native alternative payment.", attributes = logAttributes)
        dispatch(WillStart)
        dispatchFailure()
        collectDefaultValues()
        fetchTransactionDetails()
    }

    private fun fetchTransactionDetails() {
        viewModelScope.launch {
            val result = invoicesService.fetchNativeAlternativePaymentMethodTransactionDetails(
                invoiceId, gatewayConfigurationId
            )
            when (result) {
                is ProcessOutResult.Success -> with(result.value) {
                    handleState(
                        toUiModel(),
                        state,
                        parameters,
                        parameterValues,
                        isInitial = true,
                        coroutineScope = this@launch
                    )
                }
                is ProcessOutResult.Failure -> {
                    POLogger.info("Failed to fetch transaction details: %s", result)
                    _uiState.value = Failure(result)
                }
            }
        }
    }

    private suspend fun handleState(
        uiModel: NativeAlternativePaymentMethodUiModel,
        state: PONativeAlternativePaymentMethodState?,
        parameters: List<PONativeAlternativePaymentMethodParameter>?,
        parameterValues: PONativeAlternativePaymentMethodParameterValues?,
        isInitial: Boolean,
        coroutineScope: CoroutineScope
    ) {
        when (state) {
            CUSTOMER_INPUT, null -> handleCustomerInput(uiModel, parameters, isInitial)
            PENDING_CAPTURE -> handlePendingCapture(uiModel, parameterValues, coroutineScope)
            CAPTURED -> handleCaptured(uiModel)
            FAILED -> _uiState.value = Failure(
                ProcessOutResult.Failure(Generic(), "Payment has failed.")
                    .also { POLogger.info("%s", it, attributes = logAttributes) }
            )
        }
    }

    private fun handleCustomerInput(
        uiModel: NativeAlternativePaymentMethodUiModel,
        parameters: List<PONativeAlternativePaymentMethodParameter>?,
        isInitial: Boolean
    ) {
        if (parameters.isNullOrEmpty()) {
            _uiState.value = Failure(
                ProcessOutResult.Failure(
                    Internal(), "Customer input parameters is missing in response."
                ).also { POLogger.error("%s", it, attributes = logAttributes) }
            )
            return
        }
        if (handleInvalidInputParameters(parameters)) {
            return
        }
        val updatedUiModel = uiModel.copy(
            inputParameters = parameters.toInputParameters(),
            focusedInputId = View.NO_ID
        )
        if (isInitial) {
            _uiState.value = Loaded(updatedUiModel)
        } else {
            _uiState.value = Submitted(updatedUiModel)
        }
        if (eventDispatcher.subscribedForDefaultValuesRequest()) {
            requestDefaultValues(parameters)
        } else if (isInitial) {
            startUserInput(updatedUiModel)
        } else {
            continueUserInput(updatedUiModel)
        }
    }

    private fun handleInvalidInputParameters(
        parameters: List<PONativeAlternativePaymentMethodParameter>
    ): Boolean {
        parameters.find { it.type() == UNKNOWN }?.let {
            _uiState.value = Failure(
                ProcessOutResult.Failure(
                    Internal(), "Unknown input field type: ${it.rawType}"
                ).also { failure -> POLogger.error("%s", failure, attributes = logAttributes) }
            )
            return true
        }
        return false
    }

    private fun startUserInput(uiModel: NativeAlternativePaymentMethodUiModel) {
        _uiState.value = UserInput(uiModel)
        uiModel.secondaryAction?.let {
            scheduleSecondaryActionEnabling(it) { enableSecondaryAction() }
        }
        POLogger.info("Started. Waiting for payment parameters.")
        dispatch(DidStart)
    }

    private fun continueUserInput(uiModel: NativeAlternativePaymentMethodUiModel) {
        _uiState.value = UserInput(
            uiModel.copy(isSubmitting = false)
        )
        POLogger.info("Submitted. Waiting for additional payment parameters.")
        dispatch(DidSubmitParameters(additionalParametersExpected = true))
    }

    private fun requestDefaultValues(parameters: List<PONativeAlternativePaymentMethodParameter>) {
        viewModelScope.launch {
            eventDispatcher.send(
                PONativeAlternativePaymentMethodDefaultValuesRequest(
                    gatewayConfigurationId, invoiceId, parameters
                ).also {
                    defaultValuesRequests.add(it)
                    POLogger.debug("Waiting for default values for payment parameters: %s", it)
                }
            )
        }
    }

    private fun collectDefaultValues() {
        viewModelScope.launch {
            eventDispatcher.defaultValuesResponse.collect { response ->
                if (defaultValuesRequests.removeAll { it.uuid == response.uuid }) {
                    POLogger.debug("Collected default values for payment parameters: %s", response)
                    when (val uiState = _uiState.value) {
                        is Loaded -> startUserInput(
                            uiState.uiModel.withDefaultValues(response.defaultValues)
                        )
                        is Submitted -> continueUserInput(
                            uiState.uiModel.withDefaultValues(response.defaultValues)
                        )
                        else -> {}
                    }
                }
            }
        }
    }

    private fun NativeAlternativePaymentMethodUiModel.withDefaultValues(
        defaultValues: Map<String, String>
    ): NativeAlternativePaymentMethodUiModel {
        val updatedInputParameters = inputParameters.map { inputParameter ->
            defaultValues.entries.find { it.key == inputParameter.parameter.key }?.let {
                val defaultValue = inputParameter.parameter.length?.let { length ->
                    it.value.take(length)
                } ?: it.value
                inputParameter.copy(value = defaultValue)
            } ?: inputParameter
        }
        return copy(
            inputParameters = updatedInputParameters
        )
    }

    fun updateInputValue(key: String, newValue: String) {
        _uiState.value.doWhenUserInput { uiModel ->
            val updatedInputParameters = uiModel.inputParameters.map {
                if (it.parameter.key == key)
                    it.copy(value = newValue, state = Input.State.Default())
                else it
            }
            _uiState.value = UserInput(
                uiModel.copy(
                    inputParameters = updatedInputParameters
                )
            )
            updatedInputParameters.find { it.parameter.key == key }?.let {
                dispatch(
                    ParametersChanged(
                        parameter = it.parameter,
                        value = it.plainValue()
                    )
                )
            }
            POLogger.debug("Payment parameters updated: %s", updatedInputParameters)
        }
    }

    fun updateFocusedInputId(id: Int) {
        _uiState.value.doWhenUserInput { uiModel ->
            if (uiModel.focusedInputId != id) {
                _uiState.value = UserInput(
                    uiModel.copy(focusedInputId = id)
                )
            }
        }
    }

    fun submitPayment() {
        _uiState.value.doWhenUserInput { uiModel ->
            POLogger.info("Will submit payment parameters.")
            dispatch(
                WillSubmitParameters(
                    parameters = uiModel.inputParameters.map { it.parameter },
                    values = uiModel.inputParameters.values()
                )
            )

            val invalidFields = uiModel.inputParameters.mapNotNull { it.validate() }
            if (invalidFields.isNotEmpty()) {
                val failure = ProcessOutResult.Failure(
                    Validation(POFailure.ValidationCode.general),
                    "Invalid fields.",
                    invalidFields
                )
                handlePaymentFailure(uiModel, failure, replaceToLocalMessage = false)
                return@doWhenUserInput
            }

            val updatedUiModel = uiModel.copy(isSubmitting = true)
            _uiState.value = UserInput(updatedUiModel)
            initiatePayment(updatedUiModel)
        }
    }

    private fun List<InputParameter>.values(): Map<String, String> {
        val values = mutableMapOf<String, String>()
        forEach {
            values[it.parameter.key] = it.plainValue()
        }
        return values
    }

    private fun InputParameter.validate(): POFailure.InvalidField? {
        val value = plainValue()
        if (parameter.required && value.isBlank())
            return invalidField(R.string.po_native_apm_error_required_parameter)

        parameter.length?.let {
            if (value.length != it) {
                return POFailure.InvalidField(
                    name = parameter.key,
                    message = app.resources.getQuantityString(
                        R.plurals.po_native_apm_error_invalid_length, it, it
                    )
                )
            }
        }

        when (parameter.type()) {
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

    private fun InputParameter.invalidField(@StringRes resId: Int) =
        POFailure.InvalidField(
            name = parameter.key,
            message = app.getString(resId)
        )

    private fun initiatePayment(uiModel: NativeAlternativePaymentMethodUiModel) {
        viewModelScope.launch {
            val request = PONativeAlternativePaymentMethodRequest(
                invoiceId = invoiceId,
                gatewayConfigurationId = gatewayConfigurationId,
                parameters = uiModel.inputParameters.values()
            )
            when (val result = invoicesService.initiatePayment(request)) {
                is ProcessOutResult.Success -> with(result.value) {
                    handleState(
                        uiModel,
                        state,
                        parameterDefinitions,
                        parameterValues,
                        isInitial = false,
                        coroutineScope = this@launch
                    )
                }
                is ProcessOutResult.Failure -> handlePaymentFailure(
                    uiModel, result, replaceToLocalMessage = true
                )
            }
        }
    }

    private suspend fun handlePendingCapture(
        uiModel: NativeAlternativePaymentMethodUiModel,
        parameterValues: PONativeAlternativePaymentMethodParameterValues?,
        coroutineScope: CoroutineScope
    ) {
        _uiState.value = Submitted(uiModel.copy(isSubmitting = false))
        POLogger.info("All payment parameters has been submitted.")
        dispatch(DidSubmitParameters(additionalParametersExpected = false))

        if (options.waitsPaymentConfirmation) {
            val customerActionMessage = parameterValues?.customerActionMessage ?: uiModel.customerActionMessageMarkdown
            val updatedUiModel = uiModel.copy(
                title = parameterValues?.providerName,
                logoUrl = if (parameterValues?.providerName != null)
                    parameterValues.providerLogoUrl else uiModel.logoUrl,
                customerActionMessageMarkdown = customerActionMessage,
                paymentConfirmationPrimaryActionText = if (!customerActionMessage.isNullOrBlank())
                    uiModel.paymentConfirmationPrimaryActionText else null
            )
            POLogger.info("Waiting for capture confirmation.")
            dispatch(
                WillWaitForCaptureConfirmation(
                    additionalActionExpected = updatedUiModel.showCustomerAction()
                )
            )
            preloadAllImages(updatedUiModel, coroutineScope)

            animateViewTransition = true
            _uiState.value = Capture(updatedUiModel)

            updatedUiModel.paymentConfirmationSecondaryAction?.let {
                scheduleSecondaryActionEnabling(it) { enablePaymentConfirmationSecondaryAction() }
            }
            if (updatedUiModel.paymentConfirmationPrimaryActionText == null) {
                capture()
            }
            return
        }
        POLogger.info("Finished. Did not wait for capture confirmation.")
        _uiState.value = Success(uiModel)
    }

    private fun handleCaptured(uiModel: NativeAlternativePaymentMethodUiModel) {
        if (options.waitsPaymentConfirmation) {
            POLogger.info("Success. Invoice is captured.")
            dispatch(DidCompletePayment)
        }
        animateViewTransition = true
        _uiState.value = Success(uiModel)
    }

    private fun handlePaymentFailure(
        uiModel: NativeAlternativePaymentMethodUiModel,
        failure: ProcessOutResult.Failure,
        replaceToLocalMessage: Boolean // TODO: Delete this when backend localisation is done.
    ) {
        if (failure.invalidFields.isNullOrEmpty()) {
            POLogger.info("Unrecoverable payment failure: %s", failure)
            _uiState.value = Failure(failure)
            return
        }
        val updatedInputParameters = uiModel.inputParameters.map { inputParameter ->
            failure.invalidFields.find { it.name == inputParameter.parameter.key }?.let {
                inputParameter.copy(
                    state = Input.State.Error(
                        resolveInputErrorMessage(
                            replaceToLocalMessage, inputParameter.type(), it.message
                        )
                    )
                )
            } ?: inputParameter
        }
        _uiState.value = UserInput(
            uiModel.copy(
                inputParameters = updatedInputParameters,
                isSubmitting = false
            )
        )
        POLogger.debug("Invalid payment parameters: %s", failure.invalidFields)
        dispatch(DidFailToSubmitParameters(failure))
    }

    // TODO: Delete this when backend localisation is done.
    private fun resolveInputErrorMessage(
        replaceToLocalMessage: Boolean,
        type: ParameterType,
        originalMessage: String?
    ) = if (replaceToLocalMessage)
        when (type) {
            NUMERIC -> app.getString(R.string.po_native_apm_error_invalid_number)
            TEXT -> app.getString(R.string.po_native_apm_error_invalid_text)
            EMAIL -> app.getString(R.string.po_native_apm_error_invalid_email)
            PHONE -> app.getString(R.string.po_native_apm_error_invalid_phone)
            else -> null
        }
    else originalMessage

    fun confirmPayment() {
        _uiState.value.doWhenCapture { uiModel ->
            POLogger.info("User confirmed that required external action is complete.")
            dispatch(DidConfirmPayment)
            _uiState.value = Capture(
                uiModel.copy(paymentConfirmationPrimaryActionText = null)
            )
            capture()
        }
    }

    private fun capture() {
        if (captureStartTimestamp != 0L) {
            return
        }
        captureStartTimestamp = System.currentTimeMillis()
        options.showPaymentConfirmationProgressIndicatorAfterSeconds?.let { afterSeconds ->
            showPaymentConfirmationProgressIndicator(
                afterMillis = TimeUnit.SECONDS.toMillis(afterSeconds.toLong())
            )
        }
        viewModelScope.launch {
            val iterator = captureRetryStrategy.iterator
            while (capturePassedTimestamp < options.paymentConfirmationTimeoutSeconds * 1000) {
                val result = invoicesService.captureNativeAlternativePayment(invoiceId, gatewayConfigurationId)
                POLogger.debug("Attempted to capture invoice.")
                if (isCaptureRetryable(result)) {
                    delay(iterator.next())
                    capturePassedTimestamp = System.currentTimeMillis() - captureStartTimestamp
                } else {
                    captureStartTimestamp = 0L
                    capturePassedTimestamp = 0L
                    when (result) {
                        is ProcessOutResult.Success ->
                            _uiState.value.doWhenCapture { uiModel ->
                                handleCaptured(uiModel)
                            }
                        is ProcessOutResult.Failure ->
                            _uiState.value = Failure(result)
                    }
                    return@launch
                }
            }
            captureStartTimestamp = 0L
            capturePassedTimestamp = 0L
            _uiState.value = Failure(
                ProcessOutResult.Failure(
                    Timeout(), "Payment confirmation timed out."
                )
            )
        }
    }

    private fun isCaptureRetryable(
        result: ProcessOutResult<PONativeAlternativePaymentMethodCapture>
    ) = when (result) {
        is ProcessOutResult.Success -> result.value.state != CAPTURED
        is ProcessOutResult.Failure -> {
            val retryableCodes = listOf(
                NetworkUnreachable,
                Timeout(),
                Internal()
            )
            retryableCodes.contains(result.code)
        }
    }

    private suspend fun preloadAllImages(
        uiModel: NativeAlternativePaymentMethodUiModel,
        coroutineScope: CoroutineScope
    ) {
        val deferreds = mutableListOf<Deferred<ImageResult>>()
        uiModel.logoUrl?.let {
            deferreds.add(coroutineScope.async { preloadImage(it) })
        }
        uiModel.customerActionImageUrl?.let {
            deferreds.add(coroutineScope.async { preloadImage(it) })
        }
        deferreds.awaitAll()
    }

    private suspend fun preloadImage(url: String): ImageResult {
        val request = ImageRequest.Builder(app)
            .data(url)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build()
        return app.imageLoader.execute(request)
    }

    fun dispatch(event: PONativeAlternativePaymentMethodEvent) {
        viewModelScope.launch {
            eventDispatcher.send(event)
            POLogger.debug("Event has been sent: %s", event)
        }
    }

    private fun dispatchFailure() {
        viewModelScope.launch {
            _uiState.collect {
                if (it is Failure) {
                    POLogger.warn("%s", it.failure, attributes = logAttributes)
                    dispatch(DidFail(it.failure))
                }
            }
        }
    }

    fun onViewFailure(failure: PONativeAlternativePaymentMethodResult.Failure) {
        with(failure) {
            dispatch(
                DidFail(ProcessOutResult.Failure(code, message, invalidFields)
                    .also { POLogger.warn("View failed: %s", it, attributes = logAttributes) })
            )
        }
    }

    private fun PONativeAlternativePaymentMethodTransactionDetails.toUiModel() =
        NativeAlternativePaymentMethodUiModel(
            title = options.title
                ?: app.getString(R.string.po_native_apm_title_format, gateway.displayName),
            logoUrl = gateway.logoUrl,
            inputParameters = parameters?.toInputParameters() ?: emptyList(),
            successMessage = options.successMessage
                ?: app.getString(R.string.po_native_apm_success_message),
            customerActionMessageMarkdown = gateway.customerActionMessage?.let { escapedMarkdown(it) },
            customerActionImageUrl = gateway.customerActionImageUrl,
            primaryActionText = options.primaryActionText ?: invoice.formatPrimaryActionText(),
            secondaryAction = options.secondaryAction?.toUiModel(),
            paymentConfirmationPrimaryActionText = options.paymentConfirmationPrimaryAction?.let {
                it.text ?: app.getString(R.string.po_native_apm_confirm_payment_button_text)
            },
            paymentConfirmationSecondaryAction = options.paymentConfirmationSecondaryAction?.toUiModel(),
            isPaymentConfirmationProgressIndicatorVisible = false,
            isSubmitting = false
        )

    private fun List<PONativeAlternativePaymentMethodParameter>.toInputParameters() =
        map { parameter ->
            InputParameter(
                parameter = parameter,
                value = parameter.availableValues?.find { it.default == true }?.value ?: String(),
                hint = getInputHint(parameter.type()),
                centered = size == 1 && find { it.type() == NUMERIC }?.let { true } ?: false
            )
        }.let { resolveInputsKeyboardAction(it) }

    private fun resolveInputsKeyboardAction(inputParameters: List<InputParameter>) =
        inputParameters.mapIndexed { index, inputParameter ->
            if (isInputKeyboardActionSupported(inputParameter.type())) {
                val nextIndex = nextFocusableInputIndex(index, inputParameters)
                inputParameter.copy(
                    keyboardAction = InputParameter.KeyboardAction(
                        imeOptions = if (nextIndex != -1)
                            EditorInfo.IME_ACTION_NEXT
                        else EditorInfo.IME_ACTION_DONE,
                        nextFocusForwardId = inputParameters.getOrNull(nextIndex)
                            ?.focusableViewId ?: View.NO_ID
                    )
                )
            } else inputParameter
        }

    private fun isInputKeyboardActionSupported(type: ParameterType) =
        when (type) {
            SINGLE_SELECT,
            UNKNOWN -> false
            else -> true
        }

    private fun nextFocusableInputIndex(
        currentIndex: Int,
        inputParameters: List<InputParameter>
    ): Int {
        for (index in currentIndex + 1..inputParameters.lastIndex) {
            if (isInputKeyboardActionSupported(inputParameters[index].type()))
                return index else continue
        }
        return -1
    }

    private fun getInputHint(type: ParameterType) =
        when (type) {
            EMAIL -> app.getString(R.string.po_native_apm_email_placeholder)
            PHONE -> app.getString(R.string.po_native_apm_phone_placeholder)
            else -> null
        }

    private fun PONativeAlternativePaymentMethodTransactionDetails.Invoice.formatPrimaryActionText() =
        try {
            val price = NumberFormat.getCurrencyInstance().apply {
                currency = Currency.getInstance(currencyCode)
            }.format(amount.toDouble())
            app.getString(R.string.po_native_apm_submit_button_text_format, price)
        } catch (_: Exception) {
            app.getString(R.string.po_native_apm_submit_button_text)
        }

    private fun SecondaryAction.toUiModel() = when (this) {
        is SecondaryAction.Cancel ->
            SecondaryActionUiModel.Cancel(
                text = text ?: app.getString(R.string.po_native_apm_cancel_button_text),
                state = if (disabledForSeconds == 0) POButton.State.ENABLED else POButton.State.DISABLED,
                disabledForMillis = TimeUnit.SECONDS.toMillis(disabledForSeconds.toLong()),
                confirmation = with(confirmation) {
                    ActionConfirmation(
                        enabled = enabled,
                        title = title ?: app.getString(R.string.po_cancel_payment_confirmation_title),
                        message = message,
                        confirmActionText = confirmActionText
                            ?: app.getString(R.string.po_cancel_payment_confirmation_confirm),
                        dismissActionText = dismissActionText
                            ?: app.getString(R.string.po_cancel_payment_confirmation_dismiss)
                    )
                }
            )
    }

    private fun scheduleSecondaryActionEnabling(
        action: SecondaryActionUiModel,
        enable: () -> Unit
    ) {
        when (action) {
            is SecondaryActionUiModel.Cancel -> if (action.state == POButton.State.DISABLED) {
                handler.postDelayed(delayInMillis = action.disabledForMillis) { enable() }
            }
        }
    }

    private fun enableSecondaryAction() {
        _uiState.value.doWhenUserInput { uiModel ->
            _uiState.value = UserInput(
                uiModel.copy(
                    secondaryAction = uiModel.secondaryAction?.copyWith(
                        state = POButton.State.ENABLED
                    )
                )
            )
        }
    }

    private fun enablePaymentConfirmationSecondaryAction() {
        _uiState.value.doWhenCapture { uiModel ->
            _uiState.value = Capture(
                uiModel.copy(
                    paymentConfirmationSecondaryAction = uiModel.paymentConfirmationSecondaryAction?.copyWith(
                        state = POButton.State.ENABLED
                    )
                )
            )
        }
    }

    private fun showPaymentConfirmationProgressIndicator(afterMillis: Long) {
        handler.postDelayed(delayInMillis = afterMillis) {
            _uiState.value.doWhenCapture { uiModel ->
                _uiState.value = Capture(
                    uiModel.copy(isPaymentConfirmationProgressIndicatorVisible = true)
                )
            }
        }
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(null)
    }
}
