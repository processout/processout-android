package com.processout.sdk.ui.checkout

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wallet.Wallet.WalletOptions
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.dispatcher.card.tokenization.PODefaultCardTokenizationEventDispatcher
import com.processout.sdk.api.dispatcher.napm.PODefaultNativeAlternativePaymentMethodEventDispatcher
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.api.model.response.PODynamicCheckoutPaymentMethod.Display
import com.processout.sdk.api.model.response.POGooglePayCardTokenizationData
import com.processout.sdk.api.service.googlepay.PODefaultGooglePayService
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
                    googlePayService = PODefaultGooglePayService(
                        application = app,
                        walletOptions = WalletOptions.Builder()
                            .setEnvironment(configuration.googlePay.environment.value)
                            .build()
                    ),
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

    fun handleGooglePay(
        result: ProcessOutResult<POGooglePayCardTokenizationData>
    ) = interactor.handleGooglePay(result)

    fun handleAlternativePayment(
        result: ProcessOutResult<POAlternativePaymentMethodResponse>
    ) = interactor.handleAlternativePayment(result)

    private fun combine(
        interactorState: DynamicCheckoutInteractorState,
        cardTokenizationState: CardTokenizationViewModelState,
        nativeAlternativePaymentState: NativeAlternativePaymentViewModelState
    ): DynamicCheckoutViewModelState {
        val cancelAction = cancelAction(interactorState, cardTokenizationState, nativeAlternativePaymentState)
        return if (interactorState.delayedSuccess) {
            Success(
                message = configuration.paymentSuccess?.message
                    ?: app.getString(R.string.po_dynamic_checkout_success_message)
            )
        } else if (interactorState.loading) {
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
        val defaultText = app.getString(R.string.po_dynamic_checkout_button_cancel)
        val defaultCancelAction = configuration.cancelButton?.toActionState(interactorState, defaultText)
        val defaultCancelActionText = defaultCancelAction?.text ?: defaultText
        return when (interactorState.selectedPaymentMethod()) {
            is Card -> cardTokenizationState.secondaryAction?.copy(
                text = defaultCancelActionText,
                confirmation = defaultCancelAction?.confirmation
            )
            is NativeAlternativePayment -> {
                val paymentConfirmationCancelAction = configuration.alternativePayment.paymentConfirmation
                    .cancelButton?.toActionState(interactorState, defaultText)
                val paymentConfirmationCancelActionText = paymentConfirmationCancelAction?.text ?: defaultText
                when (nativeAlternativePaymentState) {
                    is Loading -> nativeAlternativePaymentState.secondaryAction?.copy(
                        text = paymentConfirmationCancelActionText,
                        confirmation = paymentConfirmationCancelAction?.confirmation
                    ) ?: defaultCancelAction
                    is UserInput -> nativeAlternativePaymentState.secondaryAction?.copy(
                        text = defaultCancelActionText,
                        confirmation = defaultCancelAction?.confirmation
                    )
                    is Capture -> nativeAlternativePaymentState.secondaryAction?.copy(
                        text = paymentConfirmationCancelActionText,
                        confirmation = paymentConfirmationCancelAction?.confirmation
                    )
                }
            }
            else -> defaultCancelAction
        }?.copy(id = interactorState.cancelActionId)
    }

    private fun CancelButton.toActionState(
        interactorState: DynamicCheckoutInteractorState,
        defaultText: String
    ) = POActionState(
        id = interactorState.cancelActionId,
        text = text ?: defaultText,
        primary = false,
        enabled = interactorState.processingPaymentMethodId == null,
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
                is GooglePay -> ExpressPayment.GooglePay(
                    id = id,
                    allowedPaymentMethods = paymentMethod.allowedPaymentMethods,
                    submitAction = POActionState(
                        id = interactorState.submitActionId,
                        text = String(),
                        primary = true,
                        enabled = id != interactorState.processingPaymentMethodId
                    )
                )
                is AlternativePayment -> if (paymentMethod.isExpress)
                    expressPayment(
                        id = id,
                        display = paymentMethod.display,
                        interactorState = interactorState
                    ) else null
                is CustomerToken -> if (paymentMethod.isExpress)
                    expressPayment(
                        id = id,
                        display = paymentMethod.display,
                        interactorState = interactorState
                    ) else null
                else -> null
            }
        }.let { POImmutableList(it) }

    private fun expressPayment(
        id: String,
        display: Display,
        interactorState: DynamicCheckoutInteractorState
    ) = ExpressPayment.Express(
        id = id,
        logoResource = display.logo,
        brandColor = display.brandColor,
        submitAction = POActionState(
            id = interactorState.submitActionId,
            text = display.name,
            primary = true,
            enabled = id != interactorState.processingPaymentMethodId
        )
    )

    private fun regularPayments(
        interactorState: DynamicCheckoutInteractorState,
        cardTokenizationState: CardTokenizationViewModelState,
        nativeAlternativePaymentState: NativeAlternativePaymentViewModelState
    ): POImmutableList<RegularPayment> =
        interactorState.paymentMethods.mapNotNull { paymentMethod ->
            val id = paymentMethod.id
            val selected = id == interactorState.selectedPaymentMethodId
            val submitButtonText = configuration.submitButtonText ?: app.getString(R.string.po_dynamic_checkout_button_pay)
            when (paymentMethod) {
                is Card -> RegularPayment(
                    id = id,
                    state = regularPaymentState(
                        display = paymentMethod.display,
                        loading = !interactorState.isInvoiceValid,
                        selected = selected
                    ),
                    content = if (selected) Content.Card(cardTokenizationState) else null,
                    submitAction = if (selected) cardTokenizationState.primaryAction.copy(text = submitButtonText) else null
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
                            text = submitButtonText,
                            primary = true,
                            loading = interactorState.processingPaymentMethodId != null
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
                        nativeAlternativePaymentState.primaryAction.copy(text = submitButtonText) else null
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
