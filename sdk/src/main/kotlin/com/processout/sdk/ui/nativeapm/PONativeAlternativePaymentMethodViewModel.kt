package com.processout.sdk.ui.nativeapm

import android.util.Patterns
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.model.request.POGatewayConfigurationRequest
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodState
import com.processout.sdk.api.repository.GatewayConfigurationsRepository
import com.processout.sdk.api.repository.InvoicesRepository
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.shared.model.InputParameter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class PONativeAlternativePaymentMethodViewModel(
    private val gatewayConfigurationId: String,
    private val invoiceId: String,
    private val gatewayConfigurationsRepository: GatewayConfigurationsRepository,
    private val invoicesRepository: InvoicesRepository
) : ViewModel() {

    internal class Factory(
        private val gatewayConfigurationId: String,
        private val invoiceId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ProcessOutApi.instance.run {
                PONativeAlternativePaymentMethodViewModel(
                    gatewayConfigurationId,
                    invoiceId,
                    gatewayConfigurations,
                    invoices
                ) as T
            }
    }

    private val _uiState = MutableStateFlow<PONativeAlternativePaymentMethodUiState>(
        PONativeAlternativePaymentMethodUiState.Initial
    )
    val uiState = _uiState.asStateFlow()

    init {
        loadGatewayConfiguration()
    }

    private fun loadGatewayConfiguration() {
        viewModelScope.launch {
            _uiState.value = PONativeAlternativePaymentMethodUiState.Loading

            val request = POGatewayConfigurationRequest(gatewayConfigurationId, withGateway = true)
            when (val result = gatewayConfigurationsRepository.find(request)) {
                is ProcessOutResult.Success -> {
                    val parameters = result.value.gateway?.nativeApmConfig?.parameters
                    if (parameters.isNullOrEmpty()) {
                        _uiState.value = PONativeAlternativePaymentMethodUiState.Failure
                    } else {
                        _uiState.value = PONativeAlternativePaymentMethodUiState.UserInput(
                            PONativeAlternativePaymentMethodUiModel(
                                logoUrl = result.value.gateway.logoUrl,
                                promptMessage = result.value.gateway.displayName,
                                inputParameters = parameters.toInputParameters(),
                                isSubmitAllowed = false,
                                isSubmitting = false
                            )
                        )
                    }
                }
                is ProcessOutResult.Failure -> _uiState.value = PONativeAlternativePaymentMethodUiState.Failure
            }
        }
    }

    fun updateInputValue(id: Int, newValue: String) {
        _uiState.value.doWhenUserInput { uiModel ->
            uiModel.inputParameters.find { it.id == id }?.apply { value = newValue }
            _uiState.value = PONativeAlternativePaymentMethodUiState.UserInput(
                uiModel.copy(
                    isSubmitAllowed = uiModel.inputParameters.map { isInputValid(it) }.all { it }
                )
            )
        }
    }

    private fun isInputValid(input: InputParameter): Boolean {
        if (input.parameter.required.not()) return true

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

    private fun initiatePayment(uiModel: PONativeAlternativePaymentMethodUiModel, data: Map<String, String>) {
        viewModelScope.launch {
            val request = PONativeAlternativePaymentMethodRequest(
                invoiceId, gatewayConfigurationId, data
            )
            when (val result = invoicesRepository.initiatePayment(request)) {
                is ProcessOutResult.Success ->
                    when (result.value.state) {
                        PONativeAlternativePaymentMethodState.CUSTOMER_INPUT -> {
                            val parameters = result.value.parameterDefinitions
                            if (parameters.isNullOrEmpty()) {
                                _uiState.value = PONativeAlternativePaymentMethodUiState.Failure
                            } else {
                                _uiState.value = PONativeAlternativePaymentMethodUiState.UserInput(
                                    uiModel.copy(
                                        promptMessage = result.value.parameterValues?.message,
                                        inputParameters = parameters.toInputParameters(),
                                        isSubmitAllowed = false,
                                        isSubmitting = false
                                    )
                                )
                            }
                        }
                        PONativeAlternativePaymentMethodState.PENDING_CAPTURE ->
                            _uiState.value = PONativeAlternativePaymentMethodUiState.Success
                    }
                is ProcessOutResult.Failure -> _uiState.value = PONativeAlternativePaymentMethodUiState.Failure
            }
        }
    }
}

private fun List<PONativeAlternativePaymentMethodParameter>.toInputParameters() =
    map { InputParameter(parameter = it) }
