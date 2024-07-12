package com.processout.sdk.ui.checkout

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.response.POBillingAddressCollectionMode
import com.processout.sdk.api.model.response.POBillingAddressCollectionMode.*
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.CardConfiguration
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.Display
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModel
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModelState
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration.BillingAddressConfiguration.CollectionMode
import com.processout.sdk.ui.checkout.DynamicCheckoutCompletion.Awaiting
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent.FieldFocusChanged
import com.processout.sdk.ui.checkout.DynamicCheckoutEvent.FieldValueChanged
import com.processout.sdk.ui.checkout.DynamicCheckoutExtendedEvent.PaymentMethodSelected
import com.processout.sdk.ui.checkout.DynamicCheckoutInteractorState.PaymentMethod.*
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.*
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.RegularPayment.Content
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration.Options
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent
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

    val completion: StateFlow<DynamicCheckoutCompletion> = combine(
        interactor.completion,
        cardTokenization.completion,
        nativeAlternativePayment.completion
    ) { interactorCompletion, cardTokenizationCompletion, nativeAlternativePaymentCompletion ->
        // TODO: combine completions
        interactorCompletion
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = Awaiting
    )

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
            is PaymentMethodSelected -> onPaymentMethodSelected(event)
            is FieldValueChanged -> onFieldValueChanged(event)
            is FieldFocusChanged -> onFieldFocusChanged(event)
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
                        invoiceId = invoiceId,
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
        isCardholderNameFieldVisible = configuration.requireCardholderName,
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

    private fun combine(
        interactorState: DynamicCheckoutInteractorState,
        cardTokenizationState: CardTokenizationViewModelState,
        nativeAlternativePaymentState: NativeAlternativePaymentViewModelState
    ): DynamicCheckoutViewModelState =
        if (interactorState.loading) {
            Starting
        } else {
            Started(
                expressPayments = expressPayments(interactorState),
                regularPayments = regularPayments(interactorState, cardTokenizationState, nativeAlternativePaymentState)
            )
        }

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
                    content = Content.Card(cardTokenizationState),
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
