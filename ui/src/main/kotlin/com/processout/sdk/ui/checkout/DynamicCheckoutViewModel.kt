package com.processout.sdk.ui.checkout

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModel
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.Started
import com.processout.sdk.ui.checkout.DynamicCheckoutViewModelState.Starting
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration.Options
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

internal class DynamicCheckoutViewModel private constructor(
    private val app: Application,
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
        // TODO
        Started
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = Starting
    )

    init {
        addCloseable(interactor.interactorScope)
    }

    fun onEvent(event: DynamicCheckoutEvent) = interactor.onEvent(event)
}
