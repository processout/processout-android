package com.processout.sdk.api.model.response

import com.processout.sdk.core.util.findBy
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Defines the data for barcode generation.
 *
 * @param[rawType] Raw barcode type.
 * @param[value] Data to encode in the barcode.
 */
@JsonClass(generateAdapter = true)
data class POBarcode(
    @Json(name = "type")
    val rawType: String,
    val value: String
) {

    /**
     * Returns supported [BarcodeType] or [BarcodeType.UNSUPPORTED] otherwise.
     */
    fun type() = BarcodeType::rawType.findBy(rawType) ?: BarcodeType.UNSUPPORTED

    /**
     * Defines supported barcode types.
     */
    @JsonClass(generateAdapter = false)
    enum class BarcodeType(val rawType: String) {
        QR_CODE("qr"),
        UNSUPPORTED(String())
    }
}
