package com.processout.sdk.ui.napm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.ui.napm.NativeAlternativePaymentInteractor.Companion.LOG_ATTRIBUTE_GATEWAY_CONFIGURATION_ID
import com.processout.sdk.ui.napm.NativeAlternativePaymentInteractor.Companion.LOG_ATTRIBUTE_INVOICE_ID
import com.processout.sdk.ui.shared.extension.map

internal class NativeAlternativePaymentViewModel(
    private val app: Application,
    private val configuration: PONativeAlternativePaymentConfiguration,
    private val interactor: NativeAlternativePaymentInteractor
) : ViewModel() {

    class Factory(
        private val app: Application,
        private val configuration: PONativeAlternativePaymentConfiguration
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            NativeAlternativePaymentViewModel(
                app = app,
                configuration = configuration,
                interactor = NativeAlternativePaymentInteractor(
                    app = app,
                    gatewayConfigurationId = configuration.gatewayConfigurationId,
                    invoiceId = configuration.invoiceId,
                    invoicesService = ProcessOut.instance.invoices,
                    logAttributes = mapOf(
                        LOG_ATTRIBUTE_INVOICE_ID to configuration.invoiceId,
                        LOG_ATTRIBUTE_GATEWAY_CONFIGURATION_ID to configuration.gatewayConfigurationId
                    )
                )
            ) as T
    }

    val completion = interactor.completion

    val state = interactor.state.map(viewModelScope, ::map)

    init {
        addCloseable(interactor.interactorScope)
    }

    fun onEvent(event: NativeAlternativePaymentEvent) = interactor.onEvent(event)

    private fun map(state: NativeAlternativePaymentInteractorState) = with(configuration) {
        NativeAlternativePaymentViewModelState(
            focusedFieldId = state.focusedFieldId
        )
    }
}
