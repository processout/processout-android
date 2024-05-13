package com.processout.sdk.ui.napm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.dispatcher.napm.PODefaultNativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.core.retry.PORetryStrategy.Exponential
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.napm.NativeAlternativePaymentInteractor.Companion.LOG_ATTRIBUTE_GATEWAY_CONFIGURATION_ID
import com.processout.sdk.ui.napm.NativeAlternativePaymentInteractor.Companion.LOG_ATTRIBUTE_INVOICE_ID
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.*
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.Options
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.PaymentConfirmationConfiguration.Companion.DEFAULT_TIMEOUT_SECONDS
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.PaymentConfirmationConfiguration.Companion.MAX_TIMEOUT_SECONDS
import com.processout.sdk.ui.shared.extension.map

internal class NativeAlternativePaymentViewModel(
    private val app: Application,
    private val options: Options,
    private val interactor: NativeAlternativePaymentInteractor
) : ViewModel() {

    class Factory(
        private val app: Application,
        private val invoiceId: String,
        private val gatewayConfigurationId: String,
        private val options: Options
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            NativeAlternativePaymentViewModel(
                app = app,
                options = options,
                interactor = NativeAlternativePaymentInteractor(
                    app = app,
                    invoiceId = invoiceId,
                    gatewayConfigurationId = gatewayConfigurationId,
                    options = options.validated(),
                    invoicesService = ProcessOut.instance.invoices,
                    captureRetryStrategy = Exponential(
                        maxRetries = Int.MAX_VALUE,
                        initialDelay = 150,
                        minDelay = 3 * 1000,
                        maxDelay = 90 * 1000,
                        factor = 1.45
                    ),
                    eventDispatcher = PODefaultNativeAlternativePaymentMethodEventDispatcher,
                    logAttributes = mapOf(
                        LOG_ATTRIBUTE_INVOICE_ID to invoiceId,
                        LOG_ATTRIBUTE_GATEWAY_CONFIGURATION_ID to gatewayConfigurationId
                    )
                )
            ) as T

        private fun Options.validated() = copy(
            paymentConfirmation = with(paymentConfirmation) {
                copy(
                    timeoutSeconds = if (timeoutSeconds in 0..MAX_TIMEOUT_SECONDS)
                        timeoutSeconds else DEFAULT_TIMEOUT_SECONDS
                )
            }
        )
    }

    val completion = interactor.completion

    val state = interactor.state.map(viewModelScope, ::map)

    init {
        addCloseable(interactor.interactorScope)
    }

    fun onEvent(event: NativeAlternativePaymentEvent) = interactor.onEvent(event)

    private fun map(
        state: NativeAlternativePaymentInteractorState
    ): NativeAlternativePaymentViewModelState = with(options) {
        when (state) {
            NativeAlternativePaymentInteractorState.Loading -> Loading
            is NativeAlternativePaymentInteractorState.UserInput ->
                with(state.value) {
                    UserInput(
                        title = "Title",
                        primaryAction = POActionState(
                            id = primaryActionId,
                            text = "Submit",
                            primary = true
                        ),
                        secondaryAction = POActionState(
                            id = secondaryActionId,
                            text = "Cancel",
                            primary = false
                        )
                    )
                }
            is NativeAlternativePaymentInteractorState.Capturing ->
                Capture(
                    secondaryAction = POActionState(
                        id = state.value.secondaryActionId,
                        text = "Cancel",
                        primary = false
                    ),
                    isCaptured = false
                )
            is NativeAlternativePaymentInteractorState.Captured ->
                Capture(
                    secondaryAction = null,
                    isCaptured = true
                )
            else -> this@NativeAlternativePaymentViewModel.state.value
        }
    }
}
