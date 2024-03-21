package com.processout.sdk.ui.googlepay

import android.app.Application
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.Wallet.WalletOptions
import org.json.JSONObject

internal class GooglePayService(
    application: Application,
    walletOptions: WalletOptions
) {

    private val client = Wallet.getPaymentsClient(application, walletOptions)

    fun loadPaymentData(paymentDataRequestJson: JSONObject): Task<PaymentData> {
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        return client.loadPaymentData(request)
    }
}
