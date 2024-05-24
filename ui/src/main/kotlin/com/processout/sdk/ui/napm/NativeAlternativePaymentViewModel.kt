package com.processout.sdk.ui.napm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.dispatcher.napm.PODefaultNativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodTransactionDetails.Invoice
import com.processout.sdk.core.retry.PORetryStrategy.Exponential
import com.processout.sdk.core.util.POMarkdownUtils.escapedMarkdown
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.napm.NativeAlternativePaymentInteractor.Companion.LOG_ATTRIBUTE_GATEWAY_CONFIGURATION_ID
import com.processout.sdk.ui.napm.NativeAlternativePaymentInteractor.Companion.LOG_ATTRIBUTE_INVOICE_ID
import com.processout.sdk.ui.napm.NativeAlternativePaymentInteractorState.*
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.Options
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.PaymentConfirmationConfiguration.Companion.DEFAULT_TIMEOUT_SECONDS
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.PaymentConfirmationConfiguration.Companion.MAX_TIMEOUT_SECONDS
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.SecondaryAction
import com.processout.sdk.ui.shared.extension.map
import java.text.NumberFormat
import java.util.Currency

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
    ): NativeAlternativePaymentViewModelState = when (state) {
        Loading -> NativeAlternativePaymentViewModelState.Loading
        is UserInput -> state.toUserInput()
        is Capturing -> state.toCapture()
        is Captured -> state.toCapture()
        else -> this@NativeAlternativePaymentViewModel.state.value
    }

    private fun UserInput.toUserInput() = with(value) {
        NativeAlternativePaymentViewModelState.UserInput(
            title = options.title ?: app.getString(R.string.po_native_apm_title_format, gateway.displayName),
            fields = fields.map(),
            focusedFieldId = focusedFieldId,
            primaryAction = POActionState(
                id = primaryActionId,
                text = options.primaryActionText ?: invoice.formatPrimaryActionText(),
                primary = true,
                enabled = submitAllowed,
                loading = submitting
            ),
            secondaryAction = options.secondaryAction?.toActionState(
                id = secondaryActionId,
                enabled = !submitting
            ),
            actionMessageMarkdown = gateway.customerActionMessage?.let { escapedMarkdown(it) },
            actionImageUrl = gateway.customerActionImageUrl,
            successMessage = options.successMessage ?: app.getString(R.string.po_native_apm_success_message)
        )
    }

    private fun Capturing.toCapture() = with(value) {
        NativeAlternativePaymentViewModelState.Capture(
            paymentProviderName = paymentProviderName,
            logoUrl = logoUrl,
            secondaryAction = options.paymentConfirmation.secondaryAction?.toActionState(
                id = secondaryActionId,
                enabled = true
            ),
            isCaptured = false
        )
    }

    private fun Captured.toCapture() = with(value) {
        NativeAlternativePaymentViewModelState.Capture(
            paymentProviderName = paymentProviderName,
            logoUrl = logoUrl,
            secondaryAction = null,
            isCaptured = true
        )
    }

    private fun List<Field>.map(): POImmutableList<NativeAlternativePaymentViewModelState.Field> {
        return POImmutableList(emptyList())
    }

    private fun Invoice.formatPrimaryActionText() =
        try {
            val price = NumberFormat.getCurrencyInstance().apply {
                currency = Currency.getInstance(currencyCode)
            }.format(amount.toDouble())
            app.getString(R.string.po_native_apm_submit_button_text_format, price)
        } catch (_: Exception) {
            app.getString(R.string.po_native_apm_submit_button_text)
        }

    private fun SecondaryAction.toActionState(
        id: String,
        enabled: Boolean
    ): POActionState = when (this) {
        is SecondaryAction.Cancel -> POActionState(
            id = id,
            text = text ?: app.getString(R.string.po_native_apm_cancel_button_text),
            primary = false,
            enabled = enabled
        )
    }
}
