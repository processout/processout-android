package com.processout.sdk.ui.googlepay

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.Wallet.WalletOptions
import com.google.android.gms.wallet.contract.ApiTaskResult
import com.google.android.gms.wallet.contract.TaskResultContracts
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.logger.POLogger
import org.json.JSONObject

class POGooglePayLauncher private constructor(
    private val service: GooglePayService
) {

    private lateinit var launcher: ActivityResultLauncher<Task<PaymentData>>

    companion object {
        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: Fragment,
            walletOptions: WalletOptions,
            callback: (ProcessOutActivityResult<POCard>) -> Unit
        ) = POGooglePayLauncher(
            service = GooglePayService(
                application = from.requireActivity().application,
                walletOptions = walletOptions
            )
        ).apply {
            launcher = from.registerForActivityResult(
                TaskResultContracts.GetPaymentDataResult(),
                activityResultCallback
            )
        }

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: ComponentActivity,
            walletOptions: WalletOptions,
            callback: (ProcessOutActivityResult<POCard>) -> Unit
        ) = POGooglePayLauncher(
            service = GooglePayService(
                application = from.application,
                walletOptions = walletOptions
            )
        ).apply {
            launcher = from.registerForActivityResult(
                TaskResultContracts.GetPaymentDataResult(),
                from.activityResultRegistry,
                activityResultCallback
            )
        }
    }

    suspend fun isReadyToPay(isReadyToPayRequestJson: JSONObject): Boolean =
        service.isReadyToPay(isReadyToPayRequestJson)

    fun launch(paymentDataRequestJson: JSONObject) {
        with(service.loadPaymentData(paymentDataRequestJson)) {
            addOnCompleteListener(launcher::launch)
        }
    }

    private val activityResultCallback = ActivityResultCallback<ApiTaskResult<PaymentData>> { result ->
        // TODO
        POLogger.info(result.toString())
    }
}
