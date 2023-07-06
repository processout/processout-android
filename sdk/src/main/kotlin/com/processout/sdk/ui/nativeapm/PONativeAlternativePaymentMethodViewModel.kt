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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.ImageResult
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.dispatcher.PONativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent.*
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodDefaultValuesRequest
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.*
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterType
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterType.*
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodConfiguration.Options.Companion.MAX_PAYMENT_CONFIRMATION_TIMEOUT_SECONDS
import com.processout.sdk.ui.shared.model.InputParameter
import com.processout.sdk.ui.shared.model.SecondaryActionUiModel
import com.processout.sdk.ui.shared.view.button.POButton
import com.processout.sdk.ui.shared.view.input.Input
import com.processout.sdk.utils.escapedMarkdown
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit

internal class PONativeAlternativePaymentMethodViewModel(
    private val app: Application,
    private val gatewayConfigurationId: String,
    private val invoiceId: String,
    val options: PONativeAlternativePaymentMethodConfiguration.Options,
    private val invoicesService: POInvoicesService,
    private val eventDispatcher: PONativeAlternativePaymentMethodEventDispatcher
) : AndroidViewModel(app) {

    internal class Factory(
        private val app: Application,
        private val gatewayConfigurationId: String,
        private val invoiceId: String,
        private val options: PONativeAlternativePaymentMethodConfiguration.Options
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            with(ProcessOut.instance) {
                PONativeAlternativePaymentMethodViewModel(
                    app,
                    gatewayConfigurationId,
                    invoiceId,
                    options.validate(),
                    invoices,
                    nativeAlternativePaymentMethodEventDispatcher
                )
            } as T

        private fun PONativeAlternativePaymentMethodConfiguration.Options.validate() = copy(
            paymentConfirmationTimeoutSeconds =
            if (paymentConfirmationTimeoutSeconds in 0..MAX_PAYMENT_CONFIRMATION_TIMEOUT_SECONDS)
                paymentConfirmationTimeoutSeconds else MAX_PAYMENT_CONFIRMATION_TIMEOUT_SECONDS
        )
    }

    companion object {
        private const val CAPTURE_POLLING_DELAY_MS = 3000L
    }

    private var capturePollingStartTimestamp = 0L

    private val _uiState = MutableStateFlow<PONativeAlternativePaymentMethodUiState>(
        PONativeAlternativePaymentMethodUiState.Loading
    )
    val uiState = _uiState.asStateFlow()

    var animateViewTransition = true

    private val handler by lazy { Handler(Looper.getMainLooper()) }

    private val defaultValuesRequests = mutableSetOf<PONativeAlternativePaymentMethodDefaultValuesRequest>()

    init {
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
                is ProcessOutResult.Success -> {
                    val parameters = result.value.parameters
                    if (parameters.isNullOrEmpty()) {
                        _uiState.value = PONativeAlternativePaymentMethodUiState.Failure(
                            ProcessOutResult.Failure(
                                POFailure.Code.Internal(),
                                "Input field parameters is missing in response."
                            )
                        )
                        return@launch
                    }
                    if (handleInvalidInputParameters(parameters)) return@launch

                    val uiModel = result.value.toUiModel()
                    _uiState.value = PONativeAlternativePaymentMethodUiState.Loaded(uiModel)

                    if (eventDispatcher.subscribedForDefaultValuesRequest())
                        requestDefaultValues(parameters)
                    else startUserInput(uiModel)
                }
                is ProcessOutResult.Failure ->
                    _uiState.value = PONativeAlternativePaymentMethodUiState.Failure(result.copy())
            }
        }
    }

    private fun startUserInput(uiModel: PONativeAlternativePaymentMethodUiModel) {
        _uiState.value = PONativeAlternativePaymentMethodUiState.UserInput(uiModel.copy())
        uiModel.secondaryAction?.let {
            scheduleSecondaryActionEnabling(it) { enableSecondaryAction() }
        }
        dispatch(DidStart)
    }

    private fun handleInvalidInputParameters(
        parameters: List<PONativeAlternativePaymentMethodParameter>
    ): Boolean {
        parameters.find { it.type() == UNKNOWN }?.let {
            _uiState.value = PONativeAlternativePaymentMethodUiState.Failure(
                ProcessOutResult.Failure(
                    POFailure.Code.Internal(),
                    "Unknown input field type: ${it.rawType}"
                )
            )
            return true
        }
        return false
    }

    private fun requestDefaultValues(parameters: List<PONativeAlternativePaymentMethodParameter>) {
        viewModelScope.launch {
            eventDispatcher.send(
                PONativeAlternativePaymentMethodDefaultValuesRequest(
                    gatewayConfigurationId, invoiceId, parameters
                ).also { defaultValuesRequests.add(it) }
            )
        }
    }

    private fun collectDefaultValues() {
        viewModelScope.launch {
            eventDispatcher.defaultValuesResponse.collect { response ->
                if (defaultValuesRequests.removeAll { it.uuid == response.uuid }) {
                    when (val uiState = _uiState.value) {
                        is PONativeAlternativePaymentMethodUiState.Loaded ->
                            startUserInput(
                                uiState.uiModel.withDefaultValues(response.defaultValues)
                            )
                        is PONativeAlternativePaymentMethodUiState.Submitted ->
                            continueUserInput(
                                uiState.uiModel.withDefaultValues(response.defaultValues)
                            )
                        else -> {}
                    }
                }
            }
        }
    }

    private fun PONativeAlternativePaymentMethodUiModel.withDefaultValues(
        defaultValues: Map<String, String>
    ): PONativeAlternativePaymentMethodUiModel {
        val updatedInputParameters = inputParameters.map { inputParameter ->
            defaultValues.entries.find { it.key == inputParameter.parameter.key }?.let {
                val defaultValue = inputParameter.parameter.length?.let { length ->
                    it.value.take(length)
                } ?: it.value
                inputParameter.copy(value = defaultValue)
            } ?: inputParameter.copy()
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
                else it.copy()
            }
            _uiState.value = PONativeAlternativePaymentMethodUiState.UserInput(
                uiModel.copy(
                    inputParameters = updatedInputParameters
                )
            )
            dispatch(ParametersChanged)
        }
    }

    fun updateFocusedInputId(id: Int) {
        _uiState.value.doWhenUserInput { uiModel ->
            if (uiModel.focusedInputId != id) {
                _uiState.value = PONativeAlternativePaymentMethodUiState.UserInput(
                    uiModel.copy(focusedInputId = id)
                )
            }
        }
    }

    fun submitPayment() {
        _uiState.value.doWhenUserInput { uiModel ->
            dispatch(WillSubmitParameters)

            val invalidFields = uiModel.inputParameters.mapNotNull { it.validate() }
            if (invalidFields.isNotEmpty()) {
                val failure = ProcessOutResult.Failure(
                    POFailure.Code.Validation(POFailure.ValidationCode.general),
                    "Invalid fields.",
                    invalidFields
                )
                handlePaymentFailure(failure, uiModel, replaceToLocalMessage = false)
                return@doWhenUserInput
            }

            val updatedUiModel = uiModel.copy(isSubmitting = true)
            _uiState.value = PONativeAlternativePaymentMethodUiState.UserInput(updatedUiModel)
            initiatePayment(updatedUiModel)
        }
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
            NUMERIC -> if (value.isDigitsOnly().not())
                return invalidField(R.string.po_native_apm_error_invalid_number)
            EMAIL -> if (Patterns.EMAIL_ADDRESS.matcher(value).matches().not())
                return invalidField(R.string.po_native_apm_error_invalid_email)
            PHONE -> if (Patterns.PHONE.matcher(value).matches().not())
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

    private fun initiatePayment(uiModel: PONativeAlternativePaymentMethodUiModel) {
        viewModelScope.launch {
            val data = mutableMapOf<String, String>()
            uiModel.inputParameters.forEach {
                data[it.parameter.key] = it.plainValue()
            }
            val request = PONativeAlternativePaymentMethodRequest(
                invoiceId, gatewayConfigurationId, data
            )
            when (val result = invoicesService.initiatePayment(request)) {
                is ProcessOutResult.Success -> handlePaymentSuccess(result, uiModel, coroutineScope = this)
                is ProcessOutResult.Failure -> handlePaymentFailure(
                    result, uiModel, replaceToLocalMessage = true
                )
            }
        }
    }

    private suspend fun handlePaymentSuccess(
        success: ProcessOutResult.Success<PONativeAlternativePaymentMethod>,
        uiModel: PONativeAlternativePaymentMethodUiModel,
        coroutineScope: CoroutineScope
    ) {
        when (success.value.state) {
            PONativeAlternativePaymentMethodState.CUSTOMER_INPUT -> {
                val parameters = success.value.parameterDefinitions
                if (parameters.isNullOrEmpty()) {
                    _uiState.value = PONativeAlternativePaymentMethodUiState.Failure(
                        ProcessOutResult.Failure(
                            POFailure.Code.Internal(),
                            "Input field parameters is missing in response."
                        )
                    )
                    return
                }
                handleCustomerInput(parameters, uiModel)
            }
            PONativeAlternativePaymentMethodState.PENDING_CAPTURE ->
                handlePendingCapture(uiModel, coroutineScope, success.value.parameterValues)
            PONativeAlternativePaymentMethodState.CAPTURED ->
                handleCaptured(uiModel)
        }
    }

    private fun handleCustomerInput(
        parameters: List<PONativeAlternativePaymentMethodParameter>,
        uiModel: PONativeAlternativePaymentMethodUiModel
    ) {
        if (handleInvalidInputParameters(parameters)) return
        val updatedUiModel = uiModel.copy(
            inputParameters = parameters.toInputParameters(),
            focusedInputId = View.NO_ID
        )
        _uiState.value = PONativeAlternativePaymentMethodUiState.Submitted(updatedUiModel)

        if (eventDispatcher.subscribedForDefaultValuesRequest())
            requestDefaultValues(parameters)
        else continueUserInput(updatedUiModel)
    }

    private fun continueUserInput(uiModel: PONativeAlternativePaymentMethodUiModel) {
        _uiState.value = PONativeAlternativePaymentMethodUiState.UserInput(
            uiModel.copy(isSubmitting = false)
        )
        dispatch(DidSubmitParameters(additionalParametersExpected = true))
    }

    private suspend fun handlePendingCapture(
        uiModel: PONativeAlternativePaymentMethodUiModel,
        coroutineScope: CoroutineScope,
        parameterValues: PONativeAlternativePaymentMethodParameterValues?
    ) {
        _uiState.value = PONativeAlternativePaymentMethodUiState.Submitted(
            uiModel.copy(isSubmitting = false)
        )
        dispatch(DidSubmitParameters(additionalParametersExpected = false))

        if (options.waitsPaymentConfirmation) {
            val updatedUiModel = uiModel.copy(
                title = parameterValues?.providerName,
                logoUrl = parameterValues?.providerLogoUrl ?: uiModel.logoUrl,
                customerActionMessageMarkdown = parameterValues?.customerActionMessage
                    ?: uiModel.customerActionMessageMarkdown
            )
            dispatch(
                WillWaitForCaptureConfirmation(
                    additionalActionExpected = updatedUiModel.showCustomerAction()
                )
            )
            preloadAllImages(coroutineScope, updatedUiModel)

            animateViewTransition = true
            _uiState.value = PONativeAlternativePaymentMethodUiState.Capture(updatedUiModel)

            updatedUiModel.paymentConfirmationSecondaryAction?.let {
                scheduleSecondaryActionEnabling(it) { enablePaymentConfirmationSecondaryAction() }
            }

            startCapturePolling()
            return
        }
        _uiState.value = PONativeAlternativePaymentMethodUiState.Success(uiModel.copy())
    }

    private fun handleCaptured(uiModel: PONativeAlternativePaymentMethodUiModel) {
        if (options.waitsPaymentConfirmation) {
            dispatch(DidCompletePayment)
        }
        animateViewTransition = true
        _uiState.value = PONativeAlternativePaymentMethodUiState.Success(uiModel.copy())
    }

    private fun handlePaymentFailure(
        failure: ProcessOutResult.Failure,
        uiModel: PONativeAlternativePaymentMethodUiModel,
        replaceToLocalMessage: Boolean // TODO: Delete this when backend localisation is done.
    ) {
        if (failure.invalidFields.isNullOrEmpty()) {
            _uiState.value = PONativeAlternativePaymentMethodUiState.Failure(failure.copy())
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
            } ?: inputParameter.copy()
        }
        _uiState.value = PONativeAlternativePaymentMethodUiState.UserInput(
            uiModel.copy(
                inputParameters = updatedInputParameters,
                isSubmitting = false
            )
        )
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

    private fun startCapturePolling() {
        if (capturePollingStartTimestamp == 0L) {
            capturePollingStartTimestamp = System.currentTimeMillis()
            capture()
        }
    }

    private fun capture() {
        viewModelScope.launch {
            val timePassed = System.currentTimeMillis() - capturePollingStartTimestamp
            if (timePassed >= options.paymentConfirmationTimeoutSeconds * 1000) {
                capturePollingStartTimestamp = 0L
                _uiState.value = PONativeAlternativePaymentMethodUiState.Failure(
                    ProcessOutResult.Failure(
                        POFailure.Code.Timeout(),
                        "Payment confirmation timed out."
                    )
                )
                return@launch
            }

            val result = invoicesService.captureNativeAlternativePayment(invoiceId, gatewayConfigurationId)
            if (isCaptureRetryable(result)) {
                delay(CAPTURE_POLLING_DELAY_MS)
                capture()
                return@launch
            }

            capturePollingStartTimestamp = 0L
            when (result) {
                is ProcessOutResult.Success ->
                    _uiState.value.doWhenCapture { uiModel ->
                        handleCaptured(uiModel)
                    }
                is ProcessOutResult.Failure ->
                    _uiState.value = PONativeAlternativePaymentMethodUiState.Failure(result.copy())
            }
        }
    }

    private fun isCaptureRetryable(
        result: ProcessOutResult<PONativeAlternativePaymentMethodCapture>
    ) = when (result) {
        is ProcessOutResult.Success ->
            result.value.state != PONativeAlternativePaymentMethodState.CAPTURED
        is ProcessOutResult.Failure -> {
            val retryableCodes = listOf(
                POFailure.Code.NetworkUnreachable,
                POFailure.Code.Timeout(),
                POFailure.Code.Internal()
            )
            retryableCodes.contains(result.code)
        }
    }

    private suspend fun preloadAllImages(
        coroutineScope: CoroutineScope,
        uiModel: PONativeAlternativePaymentMethodUiModel
    ) {
        val deferreds = mutableListOf(coroutineScope.async { preloadImage(uiModel.logoUrl) })
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

    private fun dispatch(event: PONativeAlternativePaymentMethodEvent) {
        viewModelScope.launch {
            eventDispatcher.send(event)
        }
    }

    private fun dispatchFailure() {
        viewModelScope.launch {
            _uiState.collect {
                if (it is PONativeAlternativePaymentMethodUiState.Failure) {
                    eventDispatcher.send(DidFail(it.failure))
                }
            }
        }
    }

    fun onViewFailure(failure: PONativeAlternativePaymentMethodResult.Failure) {
        with(failure) {
            dispatch(DidFail(ProcessOutResult.Failure(code, message, invalidFields)))
        }
    }

    private fun PONativeAlternativePaymentMethodTransactionDetails.toUiModel() =
        PONativeAlternativePaymentMethodUiModel(
            title = options.title
                ?: app.getString(R.string.po_native_apm_title_format, gateway.displayName),
            logoUrl = gateway.logoUrl,
            inputParameters = parameters?.toInputParameters() ?: emptyList(),
            successMessage = options.successMessage
                ?: app.getString(R.string.po_native_apm_success_message),
            customerActionMessageMarkdown = escapedMarkdown(gateway.customerActionMessage),
            customerActionImageUrl = gateway.customerActionImageUrl,
            primaryActionText = options.primaryActionText ?: invoice.formatPrimaryActionText(),
            secondaryAction = options.secondaryAction?.toUiModel(),
            paymentConfirmationSecondaryAction = options.paymentConfirmationSecondaryAction?.toUiModel(),
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
            } else inputParameter.copy()
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
            PHONE -> app.getString(R.string.po_native_apm_input_hint_phone)
            EMAIL -> app.getString(R.string.po_native_apm_input_hint_email)
            else -> null
        }

    private fun PONativeAlternativePaymentMethodTransactionDetails.Invoice.formatPrimaryActionText() =
        try {
            val price = NumberFormat.getCurrencyInstance().apply {
                currency = Currency.getInstance(currencyCode)
            }.format(amount.toDouble())
            app.getString(R.string.po_native_apm_submit_button_text_format, price)
        } catch (_: Exception) {
            app.getString(R.string.po_native_apm_submit_button_default_text)
        }

    private fun PONativeAlternativePaymentMethodConfiguration.SecondaryAction.toUiModel() =
        when (this) {
            is PONativeAlternativePaymentMethodConfiguration.SecondaryAction.Cancel ->
                SecondaryActionUiModel.Cancel(
                    text = text ?: app.getString(R.string.po_native_apm_cancel_button_default_text),
                    state = if (disabledForSeconds == 0) POButton.State.ENABLED else POButton.State.DISABLED,
                    disabledForMillis = TimeUnit.SECONDS.toMillis(disabledForSeconds.toLong())
                )
        }

    private fun scheduleSecondaryActionEnabling(action: SecondaryActionUiModel, enable: () -> Unit) {
        when (action) {
            is SecondaryActionUiModel.Cancel -> if (action.state == POButton.State.DISABLED) {
                handler.postDelayed(delayInMillis = action.disabledForMillis) { enable() }
            }
        }
    }

    private fun enableSecondaryAction() {
        _uiState.value.doWhenUserInput { uiModel ->
            _uiState.value = PONativeAlternativePaymentMethodUiState.UserInput(
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
            _uiState.value = PONativeAlternativePaymentMethodUiState.Capture(
                uiModel.copy(
                    paymentConfirmationSecondaryAction = uiModel.paymentConfirmationSecondaryAction?.copyWith(
                        state = POButton.State.ENABLED
                    )
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }
}
