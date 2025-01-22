package com.processout.sdk.ui.savedpaymentmethods

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.ui.core.shared.image.PODrawableImage
import com.processout.sdk.ui.core.shared.image.POImageRenderingMode
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POActionState.Confirmation
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsViewModelState.Content.PaymentMethods
import com.processout.sdk.ui.shared.extension.map

internal class SavedPaymentMethodsViewModel(
    private val app: Application,
    private val configuration: POSavedPaymentMethodsConfiguration,
    private val interactor: SavedPaymentMethodsInteractor
) : ViewModel() {

    class Factory(
        private val app: Application,
        private val configuration: POSavedPaymentMethodsConfiguration
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SavedPaymentMethodsViewModel(
                app = app,
                configuration = configuration,
                interactor = SavedPaymentMethodsInteractor(
                    app = app,
                    configuration = configuration,
                    invoicesService = ProcessOut.instance.invoices,
                    customerTokensService = ProcessOut.instance.customerTokens
                )
            ) as T
    }

    val completion = interactor.completion

    val state = interactor.state.map(viewModelScope, ::map)

    init {
        addCloseable(interactor.interactorScope)
    }

    fun onEvent(event: SavedPaymentMethodsEvent) = interactor.onEvent(event)

    private fun map(state: SavedPaymentMethodsInteractorState) = with(configuration) {
        SavedPaymentMethodsViewModelState(
            title = title ?: app.getString(R.string.po_saved_payment_methods_title),
            cancelAction = cancelAction(id = state.cancelActionId),
            content = PaymentMethods(
                loading = state.loading,
                paymentMethods = POImmutableList(state.paymentMethods)
            ),
            draggable = cancellation.dragDown
        )
    }

    private fun cancelAction(id: String): POActionState? =
        configuration.cancelButton?.let {
            POActionState(
                id = id,
                text = it.text ?: String(),
                primary = false,
                icon = it.icon ?: PODrawableImage(
                    resId = com.processout.sdk.ui.R.drawable.po_icon_close,
                    renderingMode = POImageRenderingMode.ORIGINAL
                ),
                confirmation = it.confirmation?.run {
                    Confirmation(
                        title = title ?: app.getString(R.string.po_cancel_confirmation_title),
                        message = message,
                        confirmActionText = confirmActionText
                            ?: app.getString(R.string.po_cancel_confirmation_confirm),
                        dismissActionText = dismissActionText
                            ?: app.getString(R.string.po_cancel_confirmation_dismiss)
                    )
                }
            )
        }
}
