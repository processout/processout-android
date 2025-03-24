package com.processout.sdk.ui.card.scanner.recognition

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.tasks.await

internal class CardRecognitionSession(
    private val numberDetector: CardAttributeDetector<String>,
    private val expirationDetector: CardAttributeDetector<POScannedCard.Expiration>,
    private val cardholderNameDetector: CardAttributeDetector<String>,
    private val shouldScanExpiredCard: Boolean
) {

    private companion object {
        const val MIN_CONFIDENCE = 0.8f
        const val RECOGNITION_DURATION_MS = 3000L
    }

    private val _currentCard = Channel<POScannedCard?>()
    val currentCard = _currentCard.receiveAsFlow()

    private val _mostFrequentCard = Channel<POScannedCard>()
    val mostFrequentCard = _mostFrequentCard.receiveAsFlow()

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private var startTimestamp = 0L
    private val recognizedCards = mutableListOf<POScannedCard>()

    suspend fun recognize(imageProxy: ImageProxy) {
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
        return _mostFrequentCard.send(card)
    }

    private fun <T : Any> List<T>.mostFrequent(): T? =
        this.groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
}
