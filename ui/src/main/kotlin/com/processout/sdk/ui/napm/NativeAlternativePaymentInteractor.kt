package com.processout.sdk.ui.napm

import android.app.Application
import androidx.compose.ui.text.input.TextFieldValue
import com.processout.sdk.api.dispatcher.napm.PODefaultNativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent.DidFail
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import com.processout.sdk.core.retry.PORetryStrategy
import com.processout.sdk.ui.base.BaseInteractor
import com.processout.sdk.ui.napm.NativeAlternativePaymentCompletion.Awaiting
import com.processout.sdk.ui.napm.NativeAlternativePaymentCompletion.Failure
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent.*
import com.processout.sdk.ui.napm.NativeAlternativePaymentInteractorState.*
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.Options
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    init {
        POLogger.info("Starting native alternative payment.", attributes = logAttributes)
        dispatch(PONativeAlternativePaymentMethodEvent.WillStart)
        dispatchFailure()
//        collectDefaultValues() TODO
        fetchTransactionDetails()
    }

    private fun fetchTransactionDetails() {
        interactorScope.launch {
            invoicesService.fetchNativeAlternativePaymentMethodTransactionDetails(
                invoiceId = invoiceId,
                gatewayConfigurationId = gatewayConfigurationId
            ).onSuccess { details ->
                // TODO
            }.onFailure { failure ->
                POLogger.info("Failed to fetch transaction details: %s", failure)
                _completion.update { Failure(failure) }
            }
        }
    }

    fun onEvent(event: NativeAlternativePaymentEvent) {
        when (event) {
            is FieldValueChanged -> updateFieldValue(event.id, event.value)
            is FieldFocusChanged -> updateFieldFocus(event.id, event.isFocused)
            is Action -> when (event.id) {
                ActionId.SUBMIT -> submit()
                ActionId.CANCEL -> cancel()
            }
            is Dismiss -> POLogger.info("Dismissed: %s", event.failure, attributes = logAttributes)
        }
    }

    private fun updateFieldValue(id: String, value: TextFieldValue) {
        // TODO
    }

    private fun updateFieldFocus(id: String, isFocused: Boolean) {
        // TODO
    }

    private fun submit() {
        // TODO
        interactorScope.launch {
            _state.update {
                Capturing(
                    CaptureStateValue(
                        logoUrl = null,
                        secondaryActionId = ActionId.CANCEL
                    )
                )
            }
            delay(2000)
            _state.whenCapturing { stateValue ->
                _state.update {
                    Captured(stateValue)
                }
            }
        }
    }

    private fun cancel() {
        _completion.update {
            Failure(
                ProcessOutResult.Failure(
                    code = POFailure.Code.Cancelled,
                    message = "Cancelled by the user with secondary cancel action."
                ).also { POLogger.info("Cancelled: %s", it, attributes = logAttributes) }
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
