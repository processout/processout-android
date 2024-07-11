package com.processout.sdk.ui.checkout

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.Display
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModel
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent.PaymentMethodSelected
import com.processout.sdk.ui.checkout.DynamicCheckoutInteractorState.PaymentMethod.*
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.*
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.RegularPayment.Content
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration.Options
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModel
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.Loading
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

internal class DynamicCheckoutViewModel private constructor(
    private val app: Application,
    private val invoiceId: String,
    private val options: Options,
    private val interactor: DynamicCheckoutInteractor,
    private val cardTokenization: CardTokenizationViewModel,
    private val nativeAlternativePayment: NativeAlternativePaymentViewModel
) : ViewModel() {

    class Factory(
        private val app: Application,
        private val invoiceId: String,
        private val options: Options,
        private val cardTokenization: CardTokenizationViewModel,
        private val nativeAlternativePayment: NativeAlternativePaymentViewModel
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DynamicCheckoutViewModel(
                app = app,
                invoiceId = invoiceId,
                options = options,
                interactor = DynamicCheckoutInteractor(
                    app = app,
                    invoiceId = invoiceId,
                    invoicesService = ProcessOut.instance.invoices
                ),
                cardTokenization = cardTokenization,
                nativeAlternativePayment = nativeAlternativePayment
            ) as T
    }

    val completion = interactor.completion

    val state: StateFlow<DynamicCheckoutViewModelState> = combine(
        interactor.state,
        cardTokenization.state,
        nativeAlternativePayment.state
    ) { interactorState, cardTokenizationState, nativeAlternativePaymentState ->
        combine(interactorState, cardTokenizationState, nativeAlternativePaymentState)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = Starting
    )

    init {
        addCloseable(interactor.interactorScope)
    }

    fun onEvent(event: DynamicCheckoutEvent) {
        when (event) {
            is PaymentMethodSelected -> select(event)
            else -> {} // TODO
        }
    }

    private fun select(event: PaymentMethodSelected) {
        if (event.id != interactor.state.value.selectedPaymentMethodId) {
            interactor.paymentMethod(event.id)?.let { paymentMethod ->
                cardTokenization.reset()
                nativeAlternativePayment.reset()
                when (paymentMethod) {
                    is Card -> cardTokenization.start(POCardTokenizationConfiguration()) // TODO: apply config
                    is NativeAlternativePayment -> nativeAlternativePayment.start(
                        invoiceId = invoiceId,
                        gatewayConfigurationId = paymentMethod.gatewayConfigurationId
                    )
                    else -> {}
                }
                interactor.onEvent(event)
            }
        }
    }

    private fun combine(
        interactorState: DynamicCheckoutInteractorState,
        cardTokenizationState: CardTokenizationViewModelState,
        nativeAlternativePaymentState: NativeAlternativePaymentViewModelState
    ): DynamicCheckoutViewModelState =
        if (interactorState.loading) {
            Starting
        } else {
            Started(
                expressPayments = POImmutableList(emptyList()),
                regularPayments = regularPayments(interactorState, cardTokenizationState, nativeAlternativePaymentState)
            )
        }

    private fun regularPayments(
        interactorState: DynamicCheckoutInteractorState,
        cardTokenizationState: CardTokenizationViewModelState,
        nativeAlternativePaymentState: NativeAlternativePaymentViewModelState
    ): POImmutableList<RegularPayment> =
        interactorState.paymentMethods.mapNotNull { paymentMethod ->
            val id = paymentMethod.id
            val selected = id == interactorState.selectedPaymentMethodId
            when (paymentMethod) {
                is Card -> RegularPayment(
                    id = id,
                    state = regularPaymentState(
                        display = paymentMethod.display,
                        selected = selected
                    ),
                    content = Content.Card(cardTokenizationState),
                    action = null
                )
                is AlternativePayment -> if (!paymentMethod.isExpress)
                    RegularPayment(
                        id = id,
                        state = regularPaymentState(
                            display = paymentMethod.display,
                            description = "This is redirect.",
                            selected = selected
                        ),
                        content = null,
                        action = POActionState(
                            id = id,
                            text = "Pay",
                            primary = true
                        )
                    ) else null
                is NativeAlternativePayment -> RegularPayment(
                    id = id,
                    state = regularPaymentState(
                        display = paymentMethod.display,
                        loading = nativeAlternativePaymentState is Loading,
                        selected = selected
                    ),
                    content = Content.NativeAlternativePayment(nativeAlternativePaymentState),
                    action = null
                )
                else -> null
            }
        }.let { POImmutableList(it) }

    private fun regularPaymentState(
        display: Display,
        description: String? = null,
        loading: Boolean = false,
        selected: Boolean
    ) = RegularPayment.State(
        name = display.name,
        logoResource = display.logo,
        description = description,
        loading = loading,
        selectable = true, // TODO
        selected = selected
    )
}
