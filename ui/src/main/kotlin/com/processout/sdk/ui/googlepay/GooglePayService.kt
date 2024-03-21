package com.processout.sdk.ui.googlepay

import android.app.Application
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.Wallet.WalletOptions
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

internal class GooglePayService(
    application: Application,
    walletOptions: WalletOptions
) {

    private val client = Wallet.getPaymentsClient(application, walletOptions)

    suspend fun isReadyToPay(isReadyToPayRequestJson: JSONObject): Boolean {
        val request = IsReadyToPayRequest.fromJson(isReadyToPayRequestJson.toString())
        return client.isReadyToPay(request).await()
    }

    fun loadPaymentData(paymentDataRequestJson: JSONObject): Task<PaymentData> {
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        return client.loadPaymentData(request)
    }
}
