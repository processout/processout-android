package com.processout.sdk.ui.card.tokenization.v2

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.dispatcher.card.tokenization.PODefaultCardTokenizationEventDispatcher
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration
import com.processout.sdk.ui.core.state.POActionState
import com.processout.sdk.ui.core.state.POImmutableList
import com.processout.sdk.ui.shared.extension.map
import com.processout.sdk.ui.shared.provider.CardSchemeProvider
import com.processout.sdk.ui.shared.provider.address.AddressSpecificationProvider

internal class CardTokenizationViewModel(
    private val app: Application,
    private val configuration: POCardTokenizationConfiguration,
    private val interactor: CardTokenizationInteractor
) : ViewModel() {

    class Factory(
        private val app: Application,
        private val configuration: POCardTokenizationConfiguration
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CardTokenizationViewModel(
                app = app,
                configuration = configuration,
                interactor = CardTokenizationInteractor(
                    configuration = configuration,
                    cardsRepository = ProcessOut.instance.cards,
                    cardSchemeProvider = CardSchemeProvider(),
                    addressSpecificationProvider = AddressSpecificationProvider(app),
                    eventDispatcher = PODefaultCardTokenizationEventDispatcher
                )
            ) as T
    }

    val completion = interactor.completion

    val state = interactor.state.map(viewModelScope, ::map)

    fun onEvent(event: CardTokenizationEvent) = interactor.onEvent(event)

    private fun map(state: CardTokenizationInteractorState) = with(configuration) {
        CardTokenizationViewModelState(
            title = title ?: app.getString(R.string.po_card_tokenization_title),
            sections = POImmutableList(emptyList()),
            primaryAction = POActionState(
                id = state.primaryActionId,
                text = primaryActionText ?: app.getString(R.string.po_card_tokenization_button_submit),
                primary = true
            ),
            secondaryAction = if (cancellation.secondaryAction) POActionState(
                id = state.secondaryActionId,
                text = secondaryActionText ?: app.getString(R.string.po_card_tokenization_button_cancel),
                primary = false
            ) else null,
            focusedFieldId = state.focusedFieldId,
            draggable = cancellation.dragDown
        )
    }
}
