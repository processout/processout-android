package com.processout.sdk.ui.card.scanner

import com.processout.sdk.core.POFailure.Code.Cancelled
import com.processout.sdk.core.POFailure.Code.Generic
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.onFailure
import com.processout.sdk.core.onSuccess
import com.processout.sdk.ui.base.BaseInteractor
import com.processout.sdk.ui.card.scanner.CardScannerCompletion.*
import com.processout.sdk.ui.card.scanner.CardScannerEvent.*
import com.processout.sdk.ui.card.scanner.CardScannerSideEffect.CameraPermissionRequest
import com.processout.sdk.ui.card.scanner.recognition.CardRecognitionSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class CardScannerInteractor(
    private val cardRecognitionSession: CardRecognitionSession
) : BaseInteractor() {

    private companion object {
        const val INIT_DELAY_MS = 400L
    }

    private val _completion = MutableStateFlow<CardScannerCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    private val _sideEffects = Channel<CardScannerSideEffect>()
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
        POLogger.info("Starting card scanner.")
        collectSessionState()
        collectRecognizedCards()
    }

    private fun initState() = CardScannerInteractorState(
        loading = false,
        isCameraPermissionGranted = false,
        isTorchEnabled = false,
        currentCard = null
    )

    private fun collectSessionState() {
        interactorScope.launch {
            cardRecognitionSession.isReady.collect { isReady ->
                _state.update { it.copy(loading = !isReady) }
                if (isReady) {
                    delay(INIT_DELAY_MS)
                    _sideEffects.send(CameraPermissionRequest)
                }
            }
        }
    }

    fun onEvent(event: CardScannerEvent) {
        when (event) {
            is CameraPermissionResult -> handle(event)
            is ImageAnalysis -> cardRecognitionSession.recognize(event.imageProxy)
            is TorchToggle -> {
                POLogger.debug("Torch toggle: ${event.isEnabled}.")
                _state.update { it.copy(isTorchEnabled = event.isEnabled) }
            }
            is Cancel -> cancel(
                ProcessOutResult.Failure(
                    code = Cancelled,
                    message = "Cancelled by the user with the cancel action."
                )
            )
            is Dismiss -> POLogger.info("Dismissed: %s", event.failure)
        }
    }

    private fun handle(event: CameraPermissionResult) {
        _state.update { it.copy(isCameraPermissionGranted = event.isGranted) }
        if (event.isGranted) {
            POLogger.info("Started: camera permission is granted.")
        } else {
            cancel(
                ProcessOutResult.Failure(
                    code = Generic(),
                    message = "Camera permission is not granted."
                )
            )
        }
    }

    private fun collectRecognizedCards() {
        interactorScope.launch(Dispatchers.Main.immediate) {
            cardRecognitionSession.currentCard.collect { card ->
                _state.update { it.copy(currentCard = card) }
            }
        }
        interactorScope.launch(Dispatchers.Main.immediate) {
            cardRecognitionSession.result.collect { result ->
                result.onSuccess { card ->
                    _completion.update { Success(card) }
                }.onFailure { failure ->
                    cancel(failure)
                }
            }
        }
    }

    private fun cancel(failure: ProcessOutResult.Failure) {
        POLogger.info("Cancelled: %s", failure)
        _completion.update { Failure(failure) }
    }

    override fun clear() {
        cardRecognitionSession.close()
    }
}
