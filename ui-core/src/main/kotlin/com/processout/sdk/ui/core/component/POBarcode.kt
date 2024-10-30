package com.processout.sdk.ui.core.component

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.BarcodeFormat.*
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.core.extension.dpToPx

private val BARCODE_2D_MIN_SIZE = 250.dp

/** @suppress */
@ProcessOutInternalApi
@Composable
fun POBarcode(
    data: String,
    format: BarcodeFormat = QR_CODE,
    size: DpSize = DpSize(width = BARCODE_2D_MIN_SIZE, height = BARCODE_2D_MIN_SIZE),
    hints: Map<EncodeHintType, Any>? = mapOf(EncodeHintType.MARGIN to 1)
) {
    var width = size.width.dpToPx()
    var height = size.height.dpToPx()
    when (format) {
        AZTEC, DATA_MATRIX, MAXICODE, QR_CODE ->
            minOf(width, height)
                .coerceAtLeast(BARCODE_2D_MIN_SIZE.dpToPx())
                .also {
                    width = it
                    height = it
                }
        else -> {}
    }
    val bitmap = remember(data, format, width, height, hints) {
        barcodeBitmap(
            data = data,
            format = format,
            width = width,
            height = height,
            hints = hints
        )
    }
    if (bitmap != null) {
        val contentDescription = when (format) {
            QR_CODE -> "QR Code"
            else -> "Barcode"
        }
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = Modifier.requiredSize(size),
            alignment = Alignment.Center,
            contentScale = ContentScale.Fit
        )
    }
}

private fun barcodeBitmap(
    data: String,
    format: BarcodeFormat,
    width: Int,
    height: Int,
    hints: Map<EncodeHintType, Any>?
): Bitmap? {
    try {
        val bitMatrix = MultiFormatWriter().encode(data, format, width, height, hints)
        return Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565).also { bitmap ->
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    } catch (_: Exception) {
        return null
    }
}
