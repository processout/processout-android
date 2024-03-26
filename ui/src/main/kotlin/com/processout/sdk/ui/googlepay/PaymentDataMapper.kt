package com.processout.sdk.ui.googlepay

import com.google.android.gms.wallet.PaymentData
import com.processout.sdk.api.model.response.POGooglePayPaymentData
import com.processout.sdk.core.logger.POLogger
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import java.io.IOException

internal class PaymentDataMapper(
    moshi: Moshi = Moshi.Builder().build()
) {

    private val adapter = moshi.adapter(POGooglePayPaymentData::class.java)

    fun map(paymentData: PaymentData): POGooglePayPaymentData? {
        return try {
            adapter.fromJson(paymentData.toJson())
        } catch (e: Exception) {
            when (e) {
                is JsonDataException,
                is IOException -> POLogger.error("Failed to parse Google Pay PaymentData json.")
            }
            null
        }
    }
}
