package com.processout.sdk.ui.card.scanner

import com.processout.sdk.core.POFailure.Code.Cancelled
import com.processout.sdk.core.POFailure.Code.Generic
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.base.BaseInteractor
import com.processout.sdk.ui.card.scanner.CardScannerCompletion.*
import com.processout.sdk.ui.card.scanner.CardScannerEvent.*
import com.processout.sdk.ui.card.scanner.CardScannerSideEffect.CameraPermissionRequest
import com.processout.sdk.ui.card.scanner.recognition.CardRecognitionSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class CardScannerInteractor(
    private val cardRecognitionSession: CardRecognitionSession
) : BaseInteractor() {

    private val _completion = MutableStateFlow<CardScannerCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    private val _sideEffects = Channel<CardScannerSideEffect>()
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
        collectRecognizedCards()
        interactorScope.launch {
            _sideEffects.send(CameraPermissionRequest)
        }
    }

    private fun initState() = CardScannerInteractorState(
        currentCard = null,
        isTorchEnabled = false
    )

    fun onEvent(event: CardScannerEvent) {
        when (event) {
            is CameraPermissionResult -> if (!event.isGranted) {
                cancel(
                    ProcessOutResult.Failure(
                        code = Generic(),
                        message = "Camera permission is not granted."
                    )
                )
            }
            is ImageAnalysis -> interactorScope.launch {
                cardRecognitionSession.recognize(event.imageProxy)
            }
            is TorchToggle -> _state.update { it.copy(isTorchEnabled = event.isEnabled) }
            is Cancel -> cancel(
                ProcessOutResult.Failure(
                    code = Cancelled,
                    message = "Cancelled by the user with the cancel action."
                )
            )
            is Dismiss -> POLogger.info("Dismissed: %s", event.failure)
        }
    }

    private fun collectRecognizedCards() {
        interactorScope.launch(Dispatchers.Main.immediate) {
            cardRecognitionSession.currentCard.collect { card ->
                _state.update { it.copy(currentCard = card) }
            }
        }
        interactorScope.launch(Dispatchers.Main.immediate) {
            cardRecognitionSession.mostFrequentCard.collect { card ->
                _completion.update { Success(card) }
            }
        }
    }

    private fun cancel(failure: ProcessOutResult.Failure) {
        POLogger.info("Cancelled: %s", failure)
        _completion.update { Failure(failure) }
    }
}
