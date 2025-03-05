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
    private val textRecognizer: TextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS),
    private val scope: CoroutineScope = MainScope()
) : Closeable {

    private companion object {
        const val MIN_CONFIDENCE = 0.8f
    }

    private val _currentResult = Channel<POScannedCard>()
    val currentResult = _currentResult.receiveAsFlow()

    private val _bestResult = Channel<POScannedCard>()
    val bestResult = _bestResult.receiveAsFlow()

    suspend fun recognize(imageProxy: ImageProxy) {
        val text = textRecognizer.process(
            imageProxy.croppedBitmap(),
            imageProxy.imageInfo.rotationDegrees
        ).await()
        val confidentLines = text.confidentLines(MIN_CONFIDENCE)
        // TODO
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
            textBlock.lines.forEach { line ->
                if (line.confidence >= minConfidence) {
                    confidentLines.add(line.text)
                }
            }
        }
        return confidentLines
    }

    override fun close() {
        scope.cancel()
    }
}
