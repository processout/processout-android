package com.processout.sdk.ui.card.scanner.recognition

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
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
    private val scope: CoroutineScope = MainScope(),
    private val recognizer: TextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
) : Closeable {

    private val _currentResult = Channel<POScannedCard>()
    val currentResult = _currentResult.receiveAsFlow()

    private val _bestResult = Channel<POScannedCard>()
    val bestResult = _bestResult.receiveAsFlow()

    suspend fun recognize(imageProxy: ImageProxy) {
        val croppedBitmap = Bitmap.createBitmap(
            imageProxy.toBitmap(), 0, 0,
            imageProxy.cropRect.width(),
            imageProxy.cropRect.height()
        )
        val text = recognizer.process(
            croppedBitmap,
            imageProxy.imageInfo.rotationDegrees
        ).await()
        imageProxy.close()
    }

    override fun close() {
        scope.cancel()
    }
}
