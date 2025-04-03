package com.processout.sdk.ui.card.scanner.recognition

import android.app.Application
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.google.android.gms.common.moduleinstall.InstallStatusListener
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.android.gms.common.moduleinstall.ModuleInstallStatusUpdate
import com.google.android.gms.common.moduleinstall.ModuleInstallStatusUpdate.InstallState
import com.google.android.gms.common.moduleinstall.ModuleInstallStatusUpdate.InstallState.*
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.processout.sdk.core.POFailure.Code.Generic
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import java.io.Closeable

internal class CardRecognitionSession(
    app: Application,
    private val numberDetector: CardAttributeDetector<String>,
    private val expirationDetector: CardAttributeDetector<POScannedCard.Expiration>,
    private val cardholderNameDetector: CardAttributeDetector<String>,
    private val shouldScanExpiredCard: Boolean,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
) : Closeable {

    private companion object {
        const val MIN_CONFIDENCE = 0.8f
        const val RECOGNITION_DURATION_MS = 3000L
    }

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    private val _currentCard = Channel<POScannedCard?>()
    val currentCard = _currentCard.receiveAsFlow()

    private val _result = Channel<ProcessOutResult<POScannedCard>>()
    val result = _result.receiveAsFlow()

    private val moduleInstallClient = ModuleInstall.getClient(app)
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private var startTimestamp = 0L
    private val recognizedCards = mutableListOf<POScannedCard>()

    init {
        ensureReadiness()
    }

    private fun ensureReadiness() {
        moduleInstallClient
            .areModulesAvailable(textRecognizer)
            .addOnSuccessListener {
                if (it.areModulesAvailable()) {
                    _isReady.update { true }
                } else {
                    installTextRecognitionModule()
                }
            }.addOnFailureListener {
                send(
                    ProcessOutResult.Failure(
                        code = Generic(),
                        message = "Failed to check text recognition module availability.",
                        cause = it
                    )
                )
            }.addOnCanceledListener {
                send(
                    ProcessOutResult.Failure(
                        code = Generic(),
                        message = "Checking text recognition module availability has been cancelled."
                    )
                )
            }
    }

    private fun installTextRecognitionModule() {
        val request = ModuleInstallRequest.newBuilder()
            .addApi(textRecognizer)
            .setListener(moduleInstallStatusListener)
            .build()

        moduleInstallClient
            .installModules(request)
            .addOnSuccessListener {
                if (it.areModulesAlreadyInstalled()) {
                    POLogger.info("Text recognition module is already installed.")
                    _isReady.update { true }
                }
            }.addOnFailureListener {
                send(
                    ProcessOutResult.Failure(
                        code = Generic(),
                        message = "Request to install text recognition module has failed.",
                        cause = it
                    )
                )
            }.addOnCanceledListener {
                send(
                    ProcessOutResult.Failure(
                        code = Generic(),
                        message = "Request to install text recognition module has been cancelled."
                    )
                )
            }
        POLogger.info("Requested to install text recognition module.")
    }

    private val moduleInstallStatusListener = object : InstallStatusListener {
        override fun onInstallStatusUpdated(status: ModuleInstallStatusUpdate) {
            if (isFinal(status.installState)) {
                moduleInstallClient.unregisterListener(this)
            }
            when (status.installState) {
                STATE_COMPLETED -> {
                    POLogger.info("Text recognition module has been installed successfully.")
                    _isReady.update { true }
                }
                STATE_FAILED -> send(
                    ProcessOutResult.Failure(
                        code = Generic(),
                        message = "Failed to install text recognition module with the error code: ${status.errorCode}."
                    )
                )
                STATE_CANCELED -> send(
                    ProcessOutResult.Failure(
                        code = Generic(),
                        message = "Text recognition module installation has been cancelled."
                    )
                )
            }
        }

        private fun isFinal(@InstallState state: Int): Boolean =
            when (state) {
                STATE_COMPLETED,
                STATE_FAILED,
                STATE_CANCELED -> true
                else -> false
            }
    }

    fun recognize(imageProxy: ImageProxy) {
        if (!_isReady.value) {
            imageProxy.close()
            return
        }
        scope.launch {
            val text = textRecognizer.process(
                imageProxy.croppedBitmap(),
                imageProxy.imageInfo.rotationDegrees
            ).await()
            val candidates = text.candidates(MIN_CONFIDENCE)
            val number = numberDetector.firstMatch(candidates)
            if (number != null) {
                if (startTimestamp == 0L) {
                    startTimestamp = System.currentTimeMillis()
                }
                val card = POScannedCard(
                    number = number,
                    expiration = expirationDetector.firstMatch(candidates),
                    cardholderName = cardholderNameDetector.firstMatch(candidates)
                )
                recognizedCards.add(card)
                if (!shouldScanExpiredCard && card.expiration?.isExpired == true) {
                    _currentCard.send(null)
                } else {
                    _currentCard.send(card)
                }
            }
            if (System.currentTimeMillis() - startTimestamp > RECOGNITION_DURATION_MS) {
                if (recognizedCards.isNotEmpty()) {
                    sendMostFrequentCard()
                    recognizedCards.clear()
                }
                startTimestamp = 0L
            }
            imageProxy.close()
        }
    }

    private fun ImageProxy.croppedBitmap() =
        Bitmap.createBitmap(
            toBitmap(), 0, 0,
            cropRect.width(),
            cropRect.height()
        )

    private fun Text.candidates(minConfidence: Float): List<String> {
        val candidates = mutableListOf<String>()
        textBlocks.forEach { textBlock ->
            textBlock.lines.forEach forEachLine@{ line ->
                if (line.elements.isEmpty()) {
                    return@forEachLine
                }
                val isConfident = line.elements.all { it.confidence >= minConfidence }
                if (isConfident) {
                    candidates.add(line.text)
                }
            }
        }
        return candidates
    }

    private suspend fun sendMostFrequentCard() {
        val reversedCards = recognizedCards.reversed()
        val number = reversedCards.map { it.number }.mostFrequent()
        if (number == null) {
            return
        }
        val card = POScannedCard(
            number = number,
            expiration = reversedCards.mapNotNull { it.expiration }.mostFrequent(),
            cardholderName = reversedCards.mapNotNull { it.cardholderName }.mostFrequent()
        )
        if (!shouldScanExpiredCard && card.expiration?.isExpired == true) {
            return
        }
        _result.send(ProcessOutResult.Success(card))
    }

    private fun <T : Any> List<T>.mostFrequent(): T? =
        this.groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key

    private fun send(failure: ProcessOutResult.Failure) {
        scope.launch {
            POLogger.info("Failure: %s", failure)
            _result.send(failure)
        }
    }

    override fun close() {
        scope.cancel()
    }
}
