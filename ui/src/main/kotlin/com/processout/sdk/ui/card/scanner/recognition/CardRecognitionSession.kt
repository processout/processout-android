package com.processout.sdk.ui.card.scanner.recognition

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.tasks.await
import java.io.Closeable

internal class CardRecognitionSession(
    private val numberDetector: CardAttributeDetector<String>,
    private val expirationDetector: CardAttributeDetector<POScannedCard.Expiration>,
    private val cardholderNameDetector: CardAttributeDetector<String>,
    private val shouldScanExpiredCard: Boolean,
    private val textRecognizer: TextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS),
    private val scope: CoroutineScope = MainScope()
) : Closeable {

    private companion object {
        const val MIN_CONFIDENCE = 0.8f
        const val RECOGNITION_DURATION_MS = 3000L
    }

    private val _currentResult = Channel<POScannedCard>()
    val currentResult = _currentResult.receiveAsFlow()

    private val _bestResult = Channel<POScannedCard>()
    val bestResult = _bestResult.receiveAsFlow()

    private var startTimestamp = 0L
    private val recognizedCards = mutableListOf<POScannedCard>()

    suspend fun recognize(imageProxy: ImageProxy) {
        val text = textRecognizer.process(
            imageProxy.croppedBitmap(),
            imageProxy.imageInfo.rotationDegrees
        ).await()
        val confidentLines = text.confidentLines(MIN_CONFIDENCE)
        val number = numberDetector.firstMatch(confidentLines)
        if (number == null) {
            imageProxy.close()
            return
        }
        if (startTimestamp == 0L) {
            startTimestamp = System.currentTimeMillis()
        }
        val card = POScannedCard(
            number = number,
            expiration = expirationDetector.firstMatch(confidentLines),
            cardholderName = cardholderNameDetector.firstMatch(confidentLines)
        )
        recognizedCards.add(card)
        _currentResult.send(card)
        if (System.currentTimeMillis() - startTimestamp > RECOGNITION_DURATION_MS) {
            bestCard()?.let { _bestResult.send(it) }
            recognizedCards.clear()
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

    private fun Text.confidentLines(minConfidence: Float): List<String> {
        val confidentLines = mutableListOf<String>()
        textBlocks.forEach { textBlock ->
            textBlock.lines.forEach forEachLine@{ line ->
                if (line.elements.isEmpty()) return@forEachLine
                val isConfident = line.elements.all { it.confidence >= minConfidence }
                if (isConfident) {
                    confidentLines.add(line.text)
                }
            }
        }
        return confidentLines
    }

    private fun bestCard(): POScannedCard? {
        val reversedCards = recognizedCards.reversed()
        val number = reversedCards.map { it.number }.mostFrequent()
        if (number != null) {
            val card = POScannedCard(
                number = number,
                expiration = reversedCards.mapNotNull { it.expiration }.mostFrequent(),
                cardholderName = reversedCards.mapNotNull { it.cardholderName }.mostFrequent()
            )
            if (!shouldScanExpiredCard && card.expiration?.isExpired == true) {
                return null
            }
            return card
        }
        return null
    }

    private fun <T : Any> List<T>.mostFrequent(): T? =
        this.groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key

    override fun close() {
        scope.cancel()
    }
}
