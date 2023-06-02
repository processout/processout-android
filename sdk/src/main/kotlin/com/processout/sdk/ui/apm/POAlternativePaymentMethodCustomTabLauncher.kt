package com.processout.sdk.ui.apm

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.web.CustomTabAuthorizationActivityContract
import com.processout.sdk.ui.web.CustomTabAuthorizationConfiguration
import com.processout.sdk.ui.web.WebAuthorizationDelegate

class POAlternativePaymentMethodCustomTabLauncher private constructor() {

    private lateinit var launcher: ActivityResultLauncher<CustomTabAuthorizationConfiguration>
    private lateinit var delegate: WebAuthorizationDelegate

    companion object {
        fun create(from: Fragment) = POAlternativePaymentMethodCustomTabLauncher().apply {
            launcher = from.registerForActivityResult(
                CustomTabAuthorizationActivityContract(),
                activityResultCallback
            )
        }

        fun create(from: ComponentActivity) = POAlternativePaymentMethodCustomTabLauncher().apply {
            launcher = from.registerForActivityResult(
                CustomTabAuthorizationActivityContract(),
                from.activityResultRegistry,
                activityResultCallback
            )
        }
    }

    fun launch(
        request: POAlternativePaymentMethodRequest,
        callback: (ProcessOutResult<POAlternativePaymentMethodResponse>) -> Unit
    ) {
        delegate = AlternativePaymentMethodWebAuthorizationDelegate(
            ProcessOut.instance.alternativePaymentMethods,
            request, callback
        )
        launcher.launch(
            CustomTabAuthorizationConfiguration(
                uri = delegate.uri,
                timeoutSeconds = null
            )
        )
    }

    private val activityResultCallback = ActivityResultCallback<ProcessOutActivityResult<Uri>> {
        when (it) {
            is ProcessOutActivityResult.Success -> delegate.complete(uri = it.value)
            is ProcessOutActivityResult.Failure -> delegate.complete(
                ProcessOutResult.Failure(it.code, it.message)
            )
        }
    }
}
