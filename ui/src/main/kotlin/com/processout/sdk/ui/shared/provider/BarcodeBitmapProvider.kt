package com.processout.sdk.ui.shared.provider

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Size
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.processout.sdk.api.model.response.POBarcode
import com.processout.sdk.api.model.response.POBarcode.BarcodeType.QR_CODE
import com.processout.sdk.api.model.response.POBarcode.BarcodeType.UNSUPPORTED
import com.processout.sdk.core.POFailure.Code.Internal
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class BarcodeBitmapProvider(
    private val workDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun generate(
        barcode: POBarcode,
        size: Size
    ): ProcessOutResult<Bitmap> = withContext(workDispatcher) {
        val format: BarcodeFormat
        val hints: Map<EncodeHintType, Any>?
        when (barcode.type()) {
            QR_CODE -> {
                format = BarcodeFormat.QR_CODE
                hints = mapOf(EncodeHintType.MARGIN to 1)
            }
            UNSUPPORTED -> return@withContext ProcessOutResult.Failure(
                code = Internal(),
                message = "Unsupported barcode type: ${barcode.rawType}"
            ).also { POLogger.error("%s", it) }
        }
        bitmap(
            barcode = barcode,
            format = format,
            width = size.width,
            height = size.height,
            hints = hints
        )
    }

    private fun bitmap(
        barcode: POBarcode,
        format: BarcodeFormat,
        width: Int,
        height: Int,
        hints: Map<EncodeHintType, Any>?
    ): ProcessOutResult<Bitmap> =
        try {
            val bitMatrix = MultiFormatWriter().encode(barcode.value(), format, width, height, hints)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            ProcessOutResult.Success(bitmap)
        } catch (e: Exception) {
            ProcessOutResult.Failure(
                code = Internal(),
                message = "Failed to generate barcode bitmap.",
                cause = e
            ).also { POLogger.error("%s", it) }
        }
}
