package com.processout.sdk.api.service.googlepay

import android.app.Application
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.Wallet.WalletOptions
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.processout.sdk.core.logger.POLogger
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

/** @suppress */
@ProcessOutInternalApi
class PODefaultGooglePayService(
    application: Application,
    walletOptions: WalletOptions
) : POGooglePayService {

    private val client = Wallet.getPaymentsClient(application, walletOptions)

    override suspend fun isReadyToPay(isReadyToPayRequestJson: JSONObject): Boolean =
        try {
            val request = IsReadyToPayRequest.fromJson(isReadyToPayRequestJson.toString())
            client.isReadyToPay(request).await()
        } catch (e: ApiException) {
            POLogger.warn("Google Pay API exception when checking readiness: %s", e)
            false
        }

    override fun loadPaymentData(paymentDataRequestJson: JSONObject): Task<PaymentData> {
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        return client.loadPaymentData(request)
    }
}
