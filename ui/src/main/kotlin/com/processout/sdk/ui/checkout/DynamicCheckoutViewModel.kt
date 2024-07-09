package com.processout.sdk.ui.checkout

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModel
import com.processout.sdk.ui.checkout.PODynamicCheckoutConfiguration.Options
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModel
import com.processout.sdk.ui.shared.extension.map

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

    val state = interactor.state.map(viewModelScope, ::map)

    init {
        addCloseable(interactor.interactorScope)
    }

    fun onEvent(event: DynamicCheckoutEvent) = interactor.onEvent(event)

    private fun map(state: DynamicCheckoutInteractorState): DynamicCheckoutViewModelState =
        DynamicCheckoutViewModelState.Starting
}
