package com.processout.sdk.ui.googlepay

import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.Wallet.WalletOptions
import com.google.android.gms.wallet.contract.ApiTaskResult
import com.google.android.gms.wallet.contract.TaskResultContracts
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POCardTokenizationRequest
import com.processout.sdk.api.model.request.POCardTokenizationRequest.TokenType.GOOGLE_PAY
import com.processout.sdk.api.model.request.POContact
import com.processout.sdk.api.model.response.POGooglePayCardTokenizationData
import com.processout.sdk.api.model.response.POGooglePayPaymentData
import com.processout.sdk.api.repository.POCardsRepository
import com.processout.sdk.core.POFailure.Code.Cancelled
import com.processout.sdk.core.POFailure.Code.Generic
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.ProcessOutResult.Failure
import com.processout.sdk.core.ProcessOutResult.Success
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.onFailure
import com.processout.sdk.ui.shared.extension.orElse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * Launcher that starts Google Pay payment sheet for card tokenization and provides the result.
 */
class POGooglePayCardTokenizationLauncher private constructor(
    private val launcher: ActivityResultLauncher<Task<PaymentData>>,
    private val service: GooglePayService
) {

    companion object {
        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: Fragment,
            walletOptions: WalletOptions,
            callback: (ProcessOutResult<POGooglePayCardTokenizationData>) -> Unit
        ) = POGooglePayCardTokenizationLauncher(
            launcher = from.registerForActivityResult(
                TaskResultContracts.GetPaymentDataResult(),
                ActivityResultHandler(callback)
            ),
            service = GooglePayService(
                application = from.requireActivity().application,
                walletOptions = walletOptions
            )
        )

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: ComponentActivity,
            walletOptions: WalletOptions,
            callback: (ProcessOutResult<POGooglePayCardTokenizationData>) -> Unit
        ) = POGooglePayCardTokenizationLauncher(
            launcher = from.registerForActivityResult(
                TaskResultContracts.GetPaymentDataResult(),
                from.activityResultRegistry,
                ActivityResultHandler(callback)
            ),
            service = GooglePayService(
                application = from.application,
                walletOptions = walletOptions
            )
        )
    }

    /**
     * Allows to check Google Pay API readiness before showing the Google Pay button.
     * [Follow tutorial](https://developers.google.com/pay/api/android/guides/tutorial)
     * to create [isReadyToPayRequestJson].
     */
    suspend fun isReadyToPay(isReadyToPayRequestJson: JSONObject): Boolean =
        service.isReadyToPay(isReadyToPayRequestJson)

    /**
     * Loads payment data for the given request and shows Google Pay payment sheet.
     * [Follow tutorial](https://developers.google.com/pay/api/android/guides/tutorial)
     * to create [paymentDataRequestJson].
     */
    fun launch(paymentDataRequestJson: JSONObject) {
        with(service.loadPaymentData(paymentDataRequestJson)) {
            addOnCompleteListener(launcher::launch)
        }
    }

    private class ActivityResultHandler(
        private val callback: (ProcessOutResult<POGooglePayCardTokenizationData>) -> Unit,
        private val cards: POCardsRepository = ProcessOut.instance.cards,
        private val mapper: PaymentDataMapper = PaymentDataMapper(),
        private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    ) : ActivityResultCallback<ApiTaskResult<PaymentData>> {

        override fun onActivityResult(result: ApiTaskResult<PaymentData>) {
            scope.launch {
                val launcherResult: ProcessOutResult<POGooglePayCardTokenizationData> =
                    when (result.status.statusCode) {
                        CommonStatusCodes.SUCCESS -> {
                            result.result?.let { paymentData ->
                                val mappedPaymentData = mapper.map(paymentData)
                                mappedPaymentData?.paymentMethodData?.tokenizationData?.token?.let { token ->
                                    val tokenizationResult = cards.tokenize(
                                        POCardTokenizationRequest(
                                            paymentToken = Base64.encodeToString(token.toByteArray(), Base64.NO_WRAP),
                                            tokenType = GOOGLE_PAY,
                                            name = mappedPaymentData.paymentMethodData.info.billingAddress?.name ?: String(),
                                            contact = mappedPaymentData.toContact()
                                        )
                                    )
                                    when (tokenizationResult) {
                                        is Success -> Success(
                                            POGooglePayCardTokenizationData(
                                                card = tokenizationResult.value,
                                                paymentData = mappedPaymentData
                                            )
                                        )
                                        is Failure -> tokenizationResult
                                    }
                                }.orElse { Failure(Generic(), "Google Pay token is missing.") }
                            }.orElse { Failure(Generic(), "Google Pay result is missing.") }
                        }
                        CommonStatusCodes.CANCELED -> Failure(
                            code = Cancelled,
                            message = "Google Pay is cancelled by the user."
                        )
                        else -> Failure(
                            code = Generic(),
                            message = "Google Pay API returned an error: [statusCode=${result.status.statusCode}] [statusMessage=${result.status.statusMessage}]."
                        )
                    }
                launcherResult.onFailure { POLogger.info("%s", it) }
                callback(launcherResult)
            }
        }

        private fun POGooglePayPaymentData.toContact(): POContact? =
            paymentMethodData.info.billingAddress?.let { address ->
                POContact(
                    address1 = address.address1,
                    address2 = address.address2,
                    city = address.locality,
                    state = address.administrativeArea,
                    zip = address.postalCode,
                    countryCode = address.countryCode
                )
            }
    }
}
