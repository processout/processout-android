package com.processout.sdk.ui.checkout

import android.app.Application
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.Flow.express
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.POFailure.Code.Cancelled
import com.processout.sdk.core.POFailure.Code.Generic
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import com.processout.sdk.ui.base.BaseInteractor
import com.processout.sdk.ui.checkout.DynamicCheckoutCompletion.Awaiting
import com.processout.sdk.ui.checkout.DynamicCheckoutCompletion.Failure
import com.processout.sdk.ui.checkout.DynamicCheckoutExtendedEvent.*
import com.processout.sdk.ui.checkout.DynamicCheckoutInteractorState.ActionId
import com.processout.sdk.ui.checkout.DynamicCheckoutInteractorState.PaymentMethod
import com.processout.sdk.ui.checkout.DynamicCheckoutInteractorState.PaymentMethod.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DynamicCheckoutInteractor(
    private val app: Application,
    private val invoiceRequest: POInvoiceRequest,
    private val invoicesService: POInvoicesService
) : BaseInteractor() {

    private val _completion = MutableStateFlow<DynamicCheckoutCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    init {
        fetchConfiguration()
    }

    private fun initState() = DynamicCheckoutInteractorState(
        loading = true,
        paymentMethods = emptyList(),
        selectedPaymentMethodId = null,
        cancelActionId = ActionId.CANCEL
    )

    private fun fetchConfiguration() {
        interactorScope.launch {
            invoicesService.invoice(invoiceRequest)
                .onSuccess { invoice ->
                    val paymentMethods = invoice.paymentMethods
                    if (paymentMethods.isNullOrEmpty()) {
                        _completion.update {
                            Failure(
                                ProcessOutResult.Failure(
                                    code = Generic(),
                                    message = "Missing remote configuration."
                                )
                            )
                        }
                        return@launch
                    }
                    _state.update {
                        it.copy(
                            loading = false,
                            paymentMethods = paymentMethods.map()
                        )
                    }
                }.onFailure { failure ->
                    _completion.update { Failure(failure) }
                }
        }
    }

    private fun List<PODynamicCheckoutPaymentMethod>.map(): List<PaymentMethod> =
        mapNotNull {
            when (it) {
                is PODynamicCheckoutPaymentMethod.Card -> Card(
                    configuration = it.configuration,
                    display = it.display
                )
                is PODynamicCheckoutPaymentMethod.GooglePay -> GooglePay(
                    configuration = it.configuration
                )
                is PODynamicCheckoutPaymentMethod.AlternativePayment -> {
                    val redirectUrl = it.configuration.redirectUrl
                    if (redirectUrl != null) {
                        AlternativePayment(
                            redirectUrl = redirectUrl,
                            display = it.display,
                            isExpress = it.flow == express
                        )
                    } else {
                        NativeAlternativePayment(
                            gatewayConfigurationId = it.configuration.gatewayConfigurationId,
                            display = it.display
                        )
                    }
                }
                else -> null
            }
        }

    fun paymentMethod(id: String): PaymentMethod? =
        _state.value.paymentMethods.find { it.id == id }

    fun onEvent(event: DynamicCheckoutExtendedEvent) {
        when (event) {
            is PaymentMethodSelected ->
                _state.update {
                    it.copy(selectedPaymentMethodId = event.id)
                }
            is Action -> when (event.id) {
                ActionId.CANCEL -> cancel()
            }
            is ActionConfirmationRequested -> {
                // TODO
            }
            is Dismiss -> {
                POLogger.warn("Dismissed: %s", event.failure)
                _completion.update { Failure(event.failure) }
            }
        }
    }

    private fun cancel() {
        _completion.update {
            Failure(
                ProcessOutResult.Failure(
                    code = Cancelled,
                    message = "Cancelled by the user with cancel action."
                ).also { POLogger.info("Cancelled: %s", it) }
            )
        }
    }
}
