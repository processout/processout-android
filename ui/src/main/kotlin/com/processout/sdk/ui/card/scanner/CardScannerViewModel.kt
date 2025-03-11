package com.processout.sdk.ui.card.scanner

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.processout.sdk.ui.card.scanner.recognition.CardExpirationDetector
import com.processout.sdk.ui.card.scanner.recognition.CardNumberDetector
import com.processout.sdk.ui.card.scanner.recognition.CardRecognitionSession
import com.processout.sdk.ui.shared.extension.map

internal class CardScannerViewModel(
    private val app: Application,
    private val configuration: POCardScannerConfiguration,
    private val interactor: CardScannerInteractor
) : ViewModel() {

    class Factory(
        private val app: Application,
        private val configuration: POCardScannerConfiguration
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CardScannerViewModel(
                app = app,
                configuration = configuration,
                interactor = CardScannerInteractor(
                    app = app,
                    configuration = configuration,
                    cardRecognitionSession = CardRecognitionSession(
                        numberDetector = CardNumberDetector(),
                        expirationDetector = CardExpirationDetector(),
                        shouldScanExpiredCard = configuration.shouldScanExpiredCard
                    )
                )
            ) as T
    }

    val completion = interactor.completion

    val state = interactor.state.map(viewModelScope, ::map)

    val sideEffects = interactor.sideEffects

    init {
        addCloseable(interactor.interactorScope)
    }

    fun onEvent(event: CardScannerEvent) = interactor.onEvent(event)

    private fun map(state: CardScannerInteractorState) = with(configuration) {
        CardScannerViewModelState(
            title = "Title",
            description = "Description",
            cancelAction = null
        )
    }

    override fun onCleared() {
        interactor.onCleared()
    }
}
