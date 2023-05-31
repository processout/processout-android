package com.processout.sdk.ui.apm

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.processout.sdk.api.model.request.POAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult

class POAlternativePaymentMethodCustomTabLauncher private constructor() {

    private lateinit var launcher: ActivityResultLauncher<POAlternativePaymentMethodRequest>

    private var callback: ((ProcessOutResult<POAlternativePaymentMethodResponse>) -> Unit)? = null

    companion object {
        fun create(from: Fragment) = POAlternativePaymentMethodCustomTabLauncher().apply {
            launcher = from.registerForActivityResult(
                CustomTabAPMAuthorizationActivityContract(),
                activityResultCallback
            )
        }

        fun create(from: ComponentActivity) = POAlternativePaymentMethodCustomTabLauncher().apply {
            launcher = from.registerForActivityResult(
                CustomTabAPMAuthorizationActivityContract(),
                from.activityResultRegistry,
                activityResultCallback
            )
        }
    }

    fun launch(
        request: POAlternativePaymentMethodRequest,
        callback: (ProcessOutResult<POAlternativePaymentMethodResponse>) -> Unit
    ) {
        this.callback = callback
        launcher.launch(request)
    }

    private val activityResultCallback =
        ActivityResultCallback<ProcessOutActivityResult<POAlternativePaymentMethodResponse>> {
            when (it) {
                is ProcessOutActivityResult.Success ->
                    callback?.invoke(ProcessOutResult.Success(it.value))
                is ProcessOutActivityResult.Failure ->
                    callback?.invoke(ProcessOutResult.Failure(it.code, it.message))
            }
        }
}
