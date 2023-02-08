package com.processout.sdk.ui.nativeapm

import android.app.Application
import android.util.Patterns
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
    private val invoicesRepository: InvoicesRepository
) : AndroidViewModel(app) {

    internal class Factory(
        private val app: Application,
        private val gatewayConfigurationId: String,
        private val invoiceId: String,
        private val options: PONativeAlternativePaymentMethodConfiguration.Options
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PONativeAlternativePaymentMethodViewModel(
                app,
                gatewayConfigurationId,
                invoiceId,
                options.validate(),
                ProcessOutApi.instance.invoices
            ) as T

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

    init {
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
                    _uiState.value = PONativeAlternativePaymentMethodUiState.UserInput(uiModel)
                }
                is ProcessOutResult.Failure ->
                    _uiState.value = PONativeAlternativePaymentMethodUiState.Failure(result)
            }
        }
    }

    private fun handleInputParametersFailure() {
        _uiState.value = PONativeAlternativePaymentMethodUiState.Failure(
            ProcessOutResult.Failure(
                "Input field parameters is missing in response.",
                POFailure.Code.Internal
            )
        )
    }

    fun updateInputValue(key: String, newValue: String) {
        _uiState.value.doWhenUserInput { uiModel ->
            val updatedInputParameters = uiModel.inputParameters.map {
                if (it.parameter.key == key)
                    it.copy(value = newValue, state = Input.State.Default)
                else it.copy()
            }
            _uiState.value = PONativeAlternativePaymentMethodUiState.UserInput(
                uiModel.copy(
                    inputParameters = updatedInputParameters,
                    isSubmitAllowed = updatedInputParameters.map { isInputValid(it) }.all { it }
                )
            )
        }
    }

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

    fun submitPayment(data: Map<String, String>) {
        _uiState.value.doWhenUserInput { uiModel ->
            _uiState.value = PONativeAlternativePaymentMethodUiState.UserInput(
                uiModel.copy(isSubmitting = true)
            )
            initiatePayment(uiModel, data)
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
        parameterDefinitions: List<PONativeAlternativePaymentMethodParameter>?,
        uiModel: PONativeAlternativePaymentMethodUiModel
    ) {
        if (parameterDefinitions.isNullOrEmpty()) {
            handleInputParametersFailure()
            return
        }
        _uiState.value = PONativeAlternativePaymentMethodUiState.UserInput(
            uiModel.copy(
                inputParameters = parameterDefinitions.toInputParameters(),
                isSubmitAllowed = false,
                isSubmitting = false
            )
        )
    }

    private fun handlePendingCapture(uiModel: PONativeAlternativePaymentMethodUiModel) {
        if (options.waitsPaymentConfirmation) {
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
        map { InputParameter(parameter = it) }

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
