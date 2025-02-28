package com.processout.sdk.ui.card.scanner

import android.app.Application
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.processout.sdk.core.POFailure.Code.Cancelled
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.base.BaseInteractor
import com.processout.sdk.ui.card.scanner.CardScannerCompletion.Awaiting
import com.processout.sdk.ui.card.scanner.CardScannerCompletion.Failure
import com.processout.sdk.ui.card.scanner.CardScannerEvent.*
import com.processout.sdk.ui.card.scanner.CardScannerInteractorState.ActionId
import com.processout.sdk.ui.card.scanner.CardScannerSideEffect.CameraPermissionRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class CardScannerInteractor(
    private val app: Application,
    private val configuration: POCardScannerConfiguration
) : BaseInteractor() {

    private val _completion = MutableStateFlow<CardScannerCompletion>(Awaiting)
    val completion = _completion.asStateFlow()

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    private val _sideEffects = Channel<CardScannerSideEffect>()
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
        interactorScope.launch {
            _sideEffects.send(CameraPermissionRequest)
        }
    }

    private fun initState() = CardScannerInteractorState(
        card = null
    )

    fun onEvent(event: CardScannerEvent) {
        when (event) {
            is CameraPermissionResult -> if (event.isGranted) {
                // TODO
            } else {
                cancel(message = "Camera permission is not granted.")
            }
            is ImageAnalysis -> analyze(event.imageProxy)
            is Action -> when (event.id) {
                ActionId.CANCEL -> cancel(message = "Cancelled by the user with cancel action.")
            }
            is Dismiss -> POLogger.info("Dismissed: %s", event.failure)
        }
    }

    private fun analyze(imageProxy: ImageProxy) {
        val croppedBitmap = Bitmap.createBitmap(
            imageProxy.toBitmap(), 0, 0,
            imageProxy.cropRect.width(),
            imageProxy.cropRect.height()
        )
        // TODO
    }

    private fun cancel(message: String) {
        val failure = ProcessOutResult.Failure(code = Cancelled, message = message)
        POLogger.info("Cancelled: %s", failure)
        _completion.update { Failure(failure) }
    }
}
