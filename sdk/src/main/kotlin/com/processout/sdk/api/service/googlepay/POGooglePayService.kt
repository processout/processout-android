package com.processout.sdk.api.service.googlepay

import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import org.json.JSONObject

/** @suppress */
@ProcessOutInternalApi
interface POGooglePayService {

    suspend fun isReadyToPay(isReadyToPayRequestJson: JSONObject): Boolean

    fun loadPaymentData(paymentDataRequestJson: JSONObject): Task<PaymentData>
}
