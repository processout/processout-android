package com.processout.sdk.ui.card.scanner.recognition

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import java.io.Closeable

internal class CardRecognitionSession(
    private val scope: CoroutineScope = MainScope()
) : Closeable {

    private val _currentResult = Channel<POScannedCard>()
    val currentResult = _currentResult.receiveAsFlow()

    private val _bestResult = Channel<POScannedCard>()
    val bestResult = _bestResult.receiveAsFlow()

    fun recognize(imageProxy: ImageProxy) {
        val croppedBitmap = Bitmap.createBitmap(
            imageProxy.toBitmap(), 0, 0,
            imageProxy.cropRect.width(),
            imageProxy.cropRect.height()
        )
        // TODO
    }

    override fun close() {
        scope.cancel()
    }
}
