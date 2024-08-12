package com.processout.sdk.ui.checkout

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.dispatcher.card.tokenization.PODefaultCardTokenizationEventDispatcher
import com.processout.sdk.api.dispatcher.checkout.PODefaultDynamicCheckoutEventDispatcher
import com.processout.sdk.api.dispatcher.napm.PODefaultNativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.model.request.PODynamicCheckoutInvoiceInvalidationReason
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.api.model.response.POBillingAddressCollectionMode
import com.processout.sdk.api.model.response.POBillingAddressCollectionMode.*
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.CardConfiguration
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.Display
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModel
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration.BillingAddressConfiguration.CollectionMode
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent.*
import com.processout.sdk.ui.checkout.DynamicCheckoutExtendedEvent.PaymentMethodSelected
import com.processout.sdk.ui.checkout.DynamicCheckoutInteractorState.PaymentMethod.*
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.*
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.RegularPayment.Content
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration.CancelButton
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration.Options
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POActionState.Confirmation
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModel
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.Loading
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class DynamicCheckoutViewModel private constructor(
    private val app: Application,
    private var invoiceRequest: POInvoiceRequest,
    private val options: Options,
    private val interactor: DynamicCheckoutInteractor,
    private val cardTokenization: CardTokenizationViewModel,
    private val nativeAlternativePayment: NativeAlternativePaymentViewModel
) : ViewModel() {

    class Factory(
        private val app: Application,
        private val invoiceRequest: POInvoiceRequest,
        private val options: Options,
        private val cardTokenization: CardTokenizationViewModel,
        private val nativeAlternativePayment: NativeAlternativePaymentViewModel,
        private val cardTokenizationEventDispatcher: PODefaultCardTokenizationEventDispatcher,
        private val nativeAlternativePaymentEventDispatcher: PODefaultNativeAlternativePaymentMethodEventDispatcher
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DynamicCheckoutViewModel(
                app = app,
                invoiceRequest = invoiceRequest,
                options = options,
                interactor = DynamicCheckoutInteractor(
                    app = app,
                    invoiceRequest = invoiceRequest,
                    invoicesService = ProcessOut.instance.invoices,
                    eventDispatcher = PODefaultDynamicCheckoutEventDispatcher,
                    cardTokenizationEventDispatcher = cardTokenizationEventDispatcher,
                    nativeAlternativePaymentEventDispatcher = nativeAlternativePaymentEventDispatcher
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
        initialValue = Starting(cancelAction = null)
    )

    init {
        addCloseable(interactor.interactorScope)
        interactor.onInvoiceChanged = ::onInvoiceChanged
        handleCompletions()
    }

    private fun handleCompletions() {
        viewModelScope.launch {
            cardTokenization.completion.collect {
                interactor.onCardTokenization(it)
            }
        }
        viewModelScope.launch {
            nativeAlternativePayment.completion.collect {
                interactor.onNativeAlternativePayment(it)
            }
        }
    }

    private fun onInvoiceChanged(
        invoiceRequest: POInvoiceRequest,
        reason: PODynamicCheckoutInvoiceInvalidationReason
    ) {
        this.invoiceRequest = invoiceRequest
        nativeAlternativePayment.reset()
        interactor.restart(invoiceRequest, reason)
    }

    fun onEvent(event: DynamicCheckoutEvent) {
        when (event) {
            is PaymentMethodSelected -> onPaymentMethodSelected(event)
            is FieldValueChanged -> onFieldValueChanged(event)
            is FieldFocusChanged -> onFieldFocusChanged(event)
            is Action -> onAction(event)
            else -> {}
        }
        if (event is DynamicCheckoutExtendedEvent) {
            interactor.onEvent(event)
        }
    }

    private fun onPaymentMethodSelected(event: PaymentMethodSelected) {
        if (event.id != interactor.state.value.selectedPaymentMethodId) {
            interactor.paymentMethod(event.id)?.let { paymentMethod ->
                cardTokenization.reset()
                nativeAlternativePayment.reset()
                when (paymentMethod) {
                    is Card -> cardTokenization.start(
                        configuration = cardTokenization.configuration
                            .apply(paymentMethod.configuration)
                    )
                    is NativeAlternativePayment -> nativeAlternativePayment.start(
                        invoiceId = invoiceRequest.invoiceId,
                        gatewayConfigurationId = paymentMethod.gatewayConfigurationId
                    )
                    else -> {}
                }
            }
        }
    }

    private fun POCardTokenizationConfiguration.apply(
        configuration: CardConfiguration
    ) = copy(
        isCardholderNameFieldVisible = configuration.cardholderNameRequired,
        billingAddress = billingAddress.copy(
            mode = configuration.billingAddress.collectionMode.map(),
            countryCodes = configuration.billingAddress.restrictToCountryCodes
        )
    )

    private fun POBillingAddressCollectionMode.map() = when (this) {
        full -> CollectionMode.Full
        automatic -> CollectionMode.Automatic
        never -> CollectionMode.Never
    }

    private fun onFieldValueChanged(event: FieldValueChanged) {
        val paymentMethod = interactor.paymentMethod(event.paymentMethodId)
        when (paymentMethod) {
            is Card -> cardTokenization.onEvent(
                CardTokenizationEvent.FieldValueChanged(event.fieldId, event.value)
            )
            is NativeAlternativePayment -> nativeAlternativePayment.onEvent(
                NativeAlternativePaymentEvent.FieldValueChanged(event.fieldId, event.value)
            )
            else -> {}
        }
    }

    private fun onFieldFocusChanged(event: FieldFocusChanged) {
        val paymentMethod = interactor.paymentMethod(event.paymentMethodId)
        when (paymentMethod) {
            is Card -> cardTokenization.onEvent(
                CardTokenizationEvent.FieldFocusChanged(event.fieldId, event.isFocused)
            )
            is NativeAlternativePayment -> nativeAlternativePayment.onEvent(
                NativeAlternativePaymentEvent.FieldFocusChanged(event.fieldId, event.isFocused)
            )
            else -> {}
        }
    }

    private fun onAction(event: Action) {
        val paymentMethod = interactor.paymentMethod(event.paymentMethodId)
        when (paymentMethod) {
            is Card -> cardTokenization.onEvent(
                CardTokenizationEvent.Action(event.actionId)
            )
            is NativeAlternativePayment -> nativeAlternativePayment.onEvent(
                NativeAlternativePaymentEvent.Action(event.actionId)
            )
            else -> interactor.onEvent(
                DynamicCheckoutExtendedEvent.Action(id = event.actionId)
            )
        }
    }

    private fun combine(
        interactorState: DynamicCheckoutInteractorState,
        cardTokenizationState: CardTokenizationViewModelState,
        nativeAlternativePaymentState: NativeAlternativePaymentViewModelState
    ): DynamicCheckoutViewModelState {
        val cancelAction = options.cancelButton?.toActionState(
            id = interactorState.cancelActionId,
            enabled = true // TODO
        )
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

    private fun CancelButton.toActionState(
        id: String,
        enabled: Boolean
    ) = POActionState(
        id = id,
        text = text ?: app.getString(R.string.po_dynamic_checkout_button_cancel),
        primary = false,
        enabled = enabled,
        confirmation = confirmation?.run {
            Confirmation(
                title = title ?: app.getString(R.string.po_cancel_payment_confirmation_title),
                message = message,
                confirmActionText = confirmActionText
                    ?: app.getString(R.string.po_cancel_payment_confirmation_confirm),
                dismissActionText = dismissActionText
                    ?: app.getString(R.string.po_cancel_payment_confirmation_dismiss)
            )
        }
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
                        selected = selected
                    ),
                    content = if (selected) Content.Card(cardTokenizationState) else null,
                    action = null
                )
                is AlternativePayment -> if (!paymentMethod.isExpress)
                    RegularPayment(
                        id = id,
                        state = regularPaymentState(
                            display = paymentMethod.display,
                            description = app.getString(R.string.po_dynamic_checkout_warning_redirect),
                            selected = selected
                        ),
                        content = null,
                        action = POActionState(
                            id = id,
                            text = app.getString(R.string.po_dynamic_checkout_button_pay),
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
                    content = if (selected) Content.NativeAlternativePayment(nativeAlternativePaymentState) else null,
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
