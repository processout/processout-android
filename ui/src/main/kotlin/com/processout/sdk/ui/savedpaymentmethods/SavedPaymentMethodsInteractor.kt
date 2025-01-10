package com.processout.sdk.ui.savedpaymentmethods

import android.app.Application
import com.processout.sdk.api.service.POCustomerTokensService
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.logger.POLogAttribute
import com.processout.sdk.ui.base.BaseInteractor
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsCompletion.Awaiting
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsEvent.Action
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsEvent.Dismiss
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsInteractorState.ActionId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class SavedPaymentMethodsInteractor(
    private val app: Application,
    private val configuration: POSavedPaymentMethodsConfiguration,
    private val invoicesService: POInvoicesService,
    private val customerTokensService: POCustomerTokensService,
    private var logAttributes: Map<String, String> = logAttributes(
        invoiceId = configuration.invoiceRequest.invoiceId
    )
) : BaseInteractor() {

    private companion object {
        fun logAttributes(invoiceId: String): Map<String, String> =
            mapOf(POLogAttribute.INVOICE_ID to invoiceId)
    }

    private val _completion = MutableStateFlow<SavedPaymentMethodsCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    init {
        interactorScope.launch {
            // TODO
        }
    }

    private fun initState() = SavedPaymentMethodsInteractorState(
        loading = true,
        invoice = null,
        paymentMethods = emptyList(),
        deleteActionId = ActionId.DELETE,
        cancelActionId = ActionId.CANCEL
    )

    fun onEvent(event: SavedPaymentMethodsEvent) {
        when (event) {
            is Action -> when (event.actionId) {
                ActionId.DELETE -> {}
                ActionId.CANCEL -> {}
            }
            is Dismiss -> {}
        }
    }
}
