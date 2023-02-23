package com.processout.sdk.ui.nativeapm

import android.app.Application
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
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
import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.dispatcher.NativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent.*
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodDefaultValuesRequest
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethod
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodState
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodTransactionDetails
import com.processout.sdk.api.repository.InvoicesRepository
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodConfiguration.Options.Companion.MAX_PAYMENT_CONFIRMATION_TIMEOUT_SECONDS
import com.processout.sdk.ui.shared.model.InputParameter
import com.processout.sdk.ui.shared.view.input.Input
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.NumberFormat
import java.util.*

internal class PONativeAlternativePaymentMethodViewModel(
    private val app: Application,
    private val gatewayConfigurationId: String,
    private val invoiceId: String,
    val options: PONativeAlternativePaymentMethodConfiguration.Options,
    private val invoicesRepository: InvoicesRepository,
    private val eventDispatcher: NativeAlternativePaymentMethodEventDispatcher
) : AndroidViewModel(app) {

    internal class Factory(
        private val app: Application,
        private val gatewayConfigurationId: String,
        private val invoiceId: String,
        private val options: PONativeAlternativePaymentMethodConfiguration.Options
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            with(ProcessOutApi.instance) {
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

    private val _uiState = MutableStateFlow<PONativeAlternativePaymentMethodUiState>(
        PONativeAlternativePaymentMethodUiState.Loading
    )
    val uiState = _uiState.asStateFlow()

    var animateViewTransition = true

    private var capturePollingStartTimestamp = 0L

    private val defaultValuesRequests = mutableSetOf<PONativeAlternativePaymentMethodDefaultValuesRequest>()

    init {
        dispatch(WillStart)
        dispatchFailure()
        collectDefaultValues()
        fetchTransactionDetails()
    }

    private fun fetchTransactionDetails() {
        viewModelScope.launch {
            val result = invoicesRepository.fetchNativeAlternativePaymentMethodTransactionDetails(
                invoiceId, gatewayConfigurationId
            )
            when (result) {
                is ProcessOutResult.Success -> {
                    if (result.value.parameters.isNullOrEmpty()) {
                        handleInputParametersFailure()
                        return@launch
                    }
                    val uiModel = result.value.toUiModel()
                    if (options.waitsPaymentConfirmation) {
                        preloadAllImages(coroutineScope = this, uiModel)
                    }
                    _uiState.value = PONativeAlternativePaymentMethodUiState.Loaded(uiModel)

                    if (eventDispatcher.subscribedForDefaultValuesRequest())
                        requestDefaultValues(result.value.parameters)
                    else startUserInput(uiModel)
                }
                is ProcessOutResult.Failure ->
                    _uiState.value = PONativeAlternativePaymentMethodUiState.Failure(result)
            }
        }
    }

    private fun startUserInput(uiModel: PONativeAlternativePaymentMethodUiModel) {
        _uiState.value = PONativeAlternativePaymentMethodUiState.UserInput(uiModel.copy())
        dispatch(DidStart)
    }

    private fun handleInputParametersFailure() {
        _uiState.value = PONativeAlternativePaymentMethodUiState.Failure(
            ProcessOutResult.Failure(
                "Input field parameters is missing in response.",
                POFailure.Code.Internal
            )
        )
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
            inputParameters = updatedInputParameters,
            isSubmitAllowed = isSubmitAllowed(updatedInputParameters)
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
                    inputParameters = updatedInputParameters,
                    isSubmitAllowed = isSubmitAllowed(updatedInputParameters)
                )
            )
            dispatch(ParametersChanged)
        }
    }

    private fun isSubmitAllowed(parameters: List<InputParameter>) =
        parameters.map { isInputValid(it) }.all { it }

    private fun isInputValid(input: InputParameter): Boolean {
        if (input.parameter.required.not()) return true
        if (input.state is Input.State.Error) return false

        val isLengthValid = input.parameter.length?.let {
            input.value.length == it
        } ?: run {
            input.value.isNotBlank()
        }

        return when (input.parameter.type) {
            PONativeAlternativePaymentMethodParameter.ParameterType.numeric ->
                isLengthValid && input.value.isDigitsOnly()
            PONativeAlternativePaymentMethodParameter.ParameterType.text -> isLengthValid
            PONativeAlternativePaymentMethodParameter.ParameterType.email ->
                Patterns.EMAIL_ADDRESS.matcher(input.value).matches()
            PONativeAlternativePaymentMethodParameter.ParameterType.phone ->
                isLengthValid && Patterns.PHONE.matcher(input.value).matches()
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

    fun submitPayment(data: Map<String, String>) {
        _uiState.value.doWhenUserInput { uiModel ->
            val updatedUiModel = uiModel.copy(isSubmitting = true)
            _uiState.value = PONativeAlternativePaymentMethodUiState.UserInput(updatedUiModel)
            dispatch(WillSubmitParameters)
            initiatePayment(updatedUiModel, data)
        }
    }

    private fun initiatePayment(
        uiModel: PONativeAlternativePaymentMethodUiModel,
        data: Map<String, String>
    ) {
        viewModelScope.launch {
            val request = PONativeAlternativePaymentMethodRequest(
                invoiceId, gatewayConfigurationId, data
            )
            when (val result = invoicesRepository.initiatePayment(request)) {
                is ProcessOutResult.Success -> handlePaymentSuccess(result, uiModel)
                is ProcessOutResult.Failure -> handlePaymentFailure(result, uiModel)
            }
        }
    }

    private fun handlePaymentSuccess(
        success: ProcessOutResult.Success<PONativeAlternativePaymentMethod>,
        uiModel: PONativeAlternativePaymentMethodUiModel
    ) {
        when (success.value.state) {
            PONativeAlternativePaymentMethodState.CUSTOMER_INPUT ->
                handleCustomerInput(success.value.parameterDefinitions, uiModel)
            PONativeAlternativePaymentMethodState.PENDING_CAPTURE ->
                handlePendingCapture(uiModel)
        }
    }

    private fun handleCustomerInput(
        parameters: List<PONativeAlternativePaymentMethodParameter>?,
        uiModel: PONativeAlternativePaymentMethodUiModel
    ) {
        if (parameters.isNullOrEmpty()) {
            handleInputParametersFailure()
            return
        }
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
            uiModel.copy(
                isSubmitAllowed = false,
                isSubmitting = false
            )
        )
        dispatch(DidSubmitParameters(additionalParametersExpected = true))
    }

    private fun handlePendingCapture(uiModel: PONativeAlternativePaymentMethodUiModel) {
        _uiState.value = PONativeAlternativePaymentMethodUiState.Submitted(
            uiModel.copy(
                isSubmitAllowed = false,
                isSubmitting = false
            )
        )
        dispatch(DidSubmitParameters(additionalParametersExpected = false))

        if (options.waitsPaymentConfirmation) {
            dispatch(
                WillWaitForCaptureConfirmation(
                    additionalActionExpected = uiModel.showCustomerAction
                )
            )
            animateViewTransition = true
            _uiState.value = PONativeAlternativePaymentMethodUiState.Capture(uiModel.copy())
            startCapturePolling()
            return
        }
        _uiState.value = PONativeAlternativePaymentMethodUiState.Success(uiModel.copy())
    }

    private fun handlePaymentFailure(
        failure: ProcessOutResult.Failure,
        uiModel: PONativeAlternativePaymentMethodUiModel
    ) {
        if (failure.invalidFields.isNullOrEmpty()) {
            _uiState.value = PONativeAlternativePaymentMethodUiState.Failure(failure)
            return
        }
        val updatedInputParameters = uiModel.inputParameters.map { inputParameter ->
            failure.invalidFields.find { it.name == inputParameter.parameter.key }?.let {
                inputParameter.copy(state = Input.State.Error(it.message))
            } ?: inputParameter.copy()
        }
        _uiState.value = PONativeAlternativePaymentMethodUiState.UserInput(
            uiModel.copy(
                inputParameters = updatedInputParameters,
                isSubmitAllowed = false,
                isSubmitting = false
            )
        )
        dispatch(DidFailToSubmitParameters(failure))
    }

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
                        "Payment confirmation timed out.",
                        POFailure.Code.Timeout
                    )
                )
                return@launch
            }

            when (invoicesRepository.capture(invoiceId, gatewayConfigurationId)) {
                is ProcessOutResult.Success -> {
                    capturePollingStartTimestamp = 0L
                    dispatch(DidCompletePayment)
                    _uiState.value.doWhenCapture { uiModel ->
                        animateViewTransition = true
                        _uiState.value = PONativeAlternativePaymentMethodUiState.Success(
                            uiModel.copy()
                        )
                    }
                }
                is ProcessOutResult.Failure -> {
                    delay(CAPTURE_POLLING_DELAY_MS)
                    capture()
                }
            }
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
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
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

    private fun PONativeAlternativePaymentMethodTransactionDetails.toUiModel() =
        PONativeAlternativePaymentMethodUiModel(
            title = options.title
                ?: app.getString(R.string.po_native_apm_title_format, gateway.displayName),
            logoUrl = gateway.logoUrl,
            inputParameters = parameters?.toInputParameters() ?: emptyList(),
            successMessage = options.successMessage
                ?: app.getString(R.string.po_native_apm_success_message),
            customerActionMessage = gateway.customerActionMessage,
            customerActionImageUrl = gateway.customerActionImageUrl,
            submitButtonText = options.submitButtonText
                ?: invoice.submitButtonTextWithFormattedPrice(),
            isSubmitAllowed = false,
            isSubmitting = false
        )

    private fun List<PONativeAlternativePaymentMethodParameter>.toInputParameters() =
        map { InputParameter(parameter = it) }.let { inputParameters ->
            inputParameters.mapIndexed { index, inputParameter ->
                if (index == inputParameters.lastIndex) {
                    inputParameter.copy(
                        keyboardAction = InputParameter.KeyboardAction(
                            imeOptions = EditorInfo.IME_ACTION_DONE
                        )
                    )
                } else {
                    inputParameter.copy(
                        keyboardAction = InputParameter.KeyboardAction(
                            imeOptions = EditorInfo.IME_ACTION_NEXT,
                            nextFocusForwardId = inputParameters.getOrNull(index + 1)
                                ?.focusableViewId ?: View.NO_ID
                        )
                    )
                }
            }
        }

    private fun PONativeAlternativePaymentMethodTransactionDetails.Invoice.submitButtonTextWithFormattedPrice() =
        try {
            val price = NumberFormat.getCurrencyInstance().apply {
                currency = Currency.getInstance(currencyCode)
            }.format(amount.toDouble())
            app.getString(R.string.po_native_apm_submit_button_text_format, price)
        } catch (_: Exception) {
            app.getString(R.string.po_native_apm_submit_button_default_text)
        }
}
