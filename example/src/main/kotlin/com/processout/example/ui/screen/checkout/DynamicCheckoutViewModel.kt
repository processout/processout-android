package com.processout.example.ui.screen.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.example.shared.Constants
import com.processout.example.ui.screen.checkout.DynamicCheckoutUiState.*
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POCreateInvoiceRequest
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.api.model.response.toResponse
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class DynamicCheckoutViewModel(
    private val invoices: POInvoicesService
) : ViewModel() {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DynamicCheckoutViewModel(
                ProcessOut.instance.invoices
            ) as T
    }

    private val _uiState = MutableStateFlow<DynamicCheckoutUiState>(Initial)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            with(ProcessOut.instance.dispatchers.dynamicCheckout) {
                invoiceRequest.collect { request ->
                    createInvoice(
                        InvoiceDetails(
                            amount = "3",
                            currency = "PLN"
                        )
                    ).onSuccess {
                        replaceInvoice(
                            request.toResponse(
                                invoiceRequest = POInvoiceRequest(
                                    invoiceId = it.id
                                )
                            )
                        )
                    }.onFailure {
                        replaceInvoice(request.toResponse(invoiceRequest = null))
                    }
                }
            }
        }
    }

    fun submit(details: InvoiceDetails) {
        _uiState.value = Submitting
        viewModelScope.launch {
            createInvoice(details)
                .onFailure { _uiState.value = Failure(it) }
                .onSuccess {
                    _uiState.value = Submitted(
                        DynamicCheckoutUiModel(
                            invoiceId = it.id,
                            cardId = String()
                        )
                    )
                }
        }
    }

    private suspend fun createInvoice(details: InvoiceDetails) =
        invoices.createInvoice(
            POCreateInvoiceRequest(
                name = UUID.randomUUID().toString(),
                amount = details.amount,
                currency = details.currency,
                returnUrl = Constants.RETURN_URL
            )
        )

    fun onLaunched() {
        val uiState = _uiState.value
        if (uiState is Submitted) {
            _uiState.value = Launched(uiState.uiModel)
        }
    }

    fun reset() {
        _uiState.value = Initial
    }
}
