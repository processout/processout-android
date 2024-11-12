package com.processout.sdk.api.model.response

import android.util.Base64
import com.processout.sdk.core.util.findBy
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Defines the data for barcode generation.
 *
 * @param[rawType] Raw barcode type.
 * @param[rawValue] Base64 encoded value.
 */
@JsonClass(generateAdapter = true)
data class POBarcode(
    @Json(name = "type")
    val rawType: String,
    @Json(name = "value")
    val rawValue: String
) {

    /**
     * Returns supported [BarcodeType] or [BarcodeType.UNSUPPORTED] otherwise.
     */
    fun type() = BarcodeType::rawType.findBy(rawType) ?: BarcodeType.UNSUPPORTED

    /**
     * Returns Base64 decoded value.
     */
    fun value() = String(Base64.decode(rawValue, Base64.NO_WRAP))

    /**
     * Defines supported barcode types.
     */
    @JsonClass(generateAdapter = false)
    enum class BarcodeType(val rawType: String) {
        QR_CODE("qr"),
        UNSUPPORTED(String())
    }
}
