package com.processout.sdk.ui.checkout

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.dispatcher.card.tokenization.PODefaultCardTokenizationEventDispatcher
import com.processout.sdk.api.dispatcher.napm.PODefaultNativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.Display
import com.processout.sdk.api.service.proxy3ds.PODefaultProxy3DSService
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModel
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState
import com.processout.sdk.ui.checkout.DynamicCheckoutInteractorState.PaymentMethod
import com.processout.sdk.ui.checkout.DynamicCheckoutInteractorState.PaymentMethod.*
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.*
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.RegularPayment.Content
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration.CancelButton
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POActionState.Confirmation
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModel
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.*
import com.processout.sdk.ui.shared.configuration.POActionConfirmationConfiguration
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

internal class DynamicCheckoutViewModel private constructor(
    private val app: Application,
    private val configuration: PODynamicCheckoutConfiguration,
    private val interactor: DynamicCheckoutInteractor
) : ViewModel() {

    class Factory(
        private val app: Application,
        private val configuration: PODynamicCheckoutConfiguration,
        private val cardTokenization: CardTokenizationViewModel,
        private val cardTokenizationEventDispatcher: PODefaultCardTokenizationEventDispatcher,
        private val nativeAlternativePayment: NativeAlternativePaymentViewModel,
        private val nativeAlternativePaymentEventDispatcher: PODefaultNativeAlternativePaymentMethodEventDispatcher
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DynamicCheckoutViewModel(
                app = app,
                configuration = configuration,
                interactor = DynamicCheckoutInteractor(
                    app = app,
                    configuration = configuration,
                    invoicesService = ProcessOut.instance.invoices,
                    threeDSService = PODefaultProxy3DSService(),
                    cardTokenization = cardTokenization,
                    cardTokenizationEventDispatcher = cardTokenizationEventDispatcher,
                    nativeAlternativePayment = nativeAlternativePayment,
                    nativeAlternativePaymentEventDispatcher = nativeAlternativePaymentEventDispatcher
                )
            ) as T
    }

    val completion = interactor.completion

    val state: StateFlow<DynamicCheckoutViewModelState> = combine(
        interactor.state,
        interactor.cardTokenizationState,
        interactor.nativeAlternativePaymentState
    ) { interactorState, cardTokenizationState, nativeAlternativePaymentState ->
        combine(interactorState, cardTokenizationState, nativeAlternativePaymentState)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = Starting(cancelAction = null)
    )

    val submitEvents = interactor.submitEvents

    init {
        addCloseable(interactor.interactorScope)
    }

    fun onEvent(event: DynamicCheckoutEvent) = interactor.onEvent(event)

    fun handle(result: ProcessOutResult<POAlternativePaymentMethodResponse>) = interactor.handle(result)

    private fun combine(
        interactorState: DynamicCheckoutInteractorState,
        cardTokenizationState: CardTokenizationViewModelState,
        nativeAlternativePaymentState: NativeAlternativePaymentViewModelState
    ): DynamicCheckoutViewModelState {
        val cancelAction = cancelAction(interactorState, cardTokenizationState, nativeAlternativePaymentState)
        return if (interactorState.loading) {
            Starting(cancelAction = cancelAction)
        } else {
            Started(
                expressPayments = expressPayments(interactorState),
                regularPayments = regularPayments(interactorState, cardTokenizationState, nativeAlternativePaymentState),
                cancelAction = cancelAction,
                errorMessage = interactorState.errorMessage
            )
        }
    }

    private fun DynamicCheckoutInteractorState.selectedPaymentMethod(): PaymentMethod? =
        paymentMethods.find { it.id == selectedPaymentMethodId }

    private fun cancelAction(
        interactorState: DynamicCheckoutInteractorState,
        cardTokenizationState: CardTokenizationViewModelState,
        nativeAlternativePaymentState: NativeAlternativePaymentViewModelState
    ): POActionState? {
        val cancelAction = configuration.cancelButton?.toActionState(interactorState)
        return when (interactorState.selectedPaymentMethod()) {
            is Card -> cardTokenizationState.secondaryAction?.copy(
                confirmation = configuration.cancelButton?.confirmation?.map()
            )
            is NativeAlternativePayment -> when (nativeAlternativePaymentState) {
                is Loading -> nativeAlternativePaymentState.secondaryAction ?: cancelAction
                is UserInput -> nativeAlternativePaymentState.secondaryAction
                is Capture -> nativeAlternativePaymentState.secondaryAction
            }
            else -> cancelAction
        }?.copy(id = interactorState.cancelActionId)
    }

    private fun CancelButton.toActionState(
        interactorState: DynamicCheckoutInteractorState
    ) = POActionState(
        id = interactorState.cancelActionId,
        text = text ?: app.getString(R.string.po_dynamic_checkout_button_cancel),
        primary = false,
        enabled = !interactorState.processingPayment,
        confirmation = confirmation?.map()
    )

    private fun POActionConfirmationConfiguration.map() = Confirmation(
        title = title ?: app.getString(R.string.po_cancel_payment_confirmation_title),
        message = message,
        confirmActionText = confirmActionText
            ?: app.getString(R.string.po_cancel_payment_confirmation_confirm),
        dismissActionText = dismissActionText
            ?: app.getString(R.string.po_cancel_payment_confirmation_dismiss)
    )

    private fun expressPayments(
        interactorState: DynamicCheckoutInteractorState
    ): POImmutableList<ExpressPayment> =
        interactorState.paymentMethods.mapNotNull { paymentMethod ->
            val id = paymentMethod.id
            when (paymentMethod) {
                is GooglePay -> ExpressPayment.GooglePay(id = id)
                is AlternativePayment -> if (paymentMethod.isExpress) {
                    ExpressPayment.Express(
                        id = id,
                        name = paymentMethod.display.name,
                        logoResource = paymentMethod.display.logo,
                        brandColor = paymentMethod.display.brandColor
                    )
                } else null
                else -> null
            }
        }.let { POImmutableList(it) }

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
                        loading = !interactorState.isInvoiceValid,
                        selected = selected
                    ),
                    content = if (selected) Content.Card(cardTokenizationState) else null,
                    submitAction = if (selected) cardTokenizationState.primaryAction else null
                )
                is AlternativePayment -> if (!paymentMethod.isExpress)
                    RegularPayment(
                        id = id,
                        state = regularPaymentState(
                            display = paymentMethod.display,
                            description = app.getString(R.string.po_dynamic_checkout_warning_redirect),
                            loading = !interactorState.isInvoiceValid,
                            selected = selected
                        ),
                        content = null,
                        submitAction = POActionState(
                            id = interactorState.submitActionId,
                            text = configuration.submitButtonText ?: app.getString(R.string.po_dynamic_checkout_button_pay),
                            primary = true,
                            loading = interactorState.processingPayment
                        )
                    ) else null
                is NativeAlternativePayment -> RegularPayment(
                    id = id,
                    state = regularPaymentState(
                        display = paymentMethod.display,
                        loading = !interactorState.isInvoiceValid || nativeAlternativePaymentState is Loading,
                        selected = selected
                    ),
                    content = if (selected) Content.NativeAlternativePayment(nativeAlternativePaymentState) else null,
                    submitAction = if (selected && nativeAlternativePaymentState is UserInput)
                        nativeAlternativePaymentState.primaryAction else null
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
        selected = selected
    )

    override fun onCleared() {
        interactor.onCleared()
    }
}
