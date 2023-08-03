package com.processout.sdk.ui.apm

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.api.network.ApiConstants
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.web.DefaultWebAuthorizationDelegateCache
import com.processout.sdk.ui.web.WebAuthorizationDelegate
import com.processout.sdk.ui.web.WebAuthorizationDelegateCache
import com.processout.sdk.ui.web.customtab.CustomTabAuthorizationActivityContract
import com.processout.sdk.ui.web.customtab.CustomTabConfiguration
import com.processout.sdk.ui.web.webview.WebViewAuthorizationActivityLauncher
import com.processout.sdk.ui.web.webview.WebViewConfiguration

class POAlternativePaymentMethodCustomTabLauncher private constructor(
    private val delegateCache: WebAuthorizationDelegateCache
) {

    private lateinit var customTabLauncher: ActivityResultLauncher<CustomTabConfiguration>
    private lateinit var webViewFallbackLauncher: WebViewAuthorizationActivityLauncher

    companion object {
        fun create(from: Fragment) = POAlternativePaymentMethodCustomTabLauncher(
            DefaultWebAuthorizationDelegateCache
        ).apply {
            customTabLauncher = from.registerForActivityResult(
                CustomTabAuthorizationActivityContract(),
                activityResultCallback
            )
            webViewFallbackLauncher = WebViewAuthorizationActivityLauncher.create(
                from, activityResultCallback
            )
        }

        fun create(from: ComponentActivity) = POAlternativePaymentMethodCustomTabLauncher(
            DefaultWebAuthorizationDelegateCache
        ).apply {
            customTabLauncher = from.registerForActivityResult(
                CustomTabAuthorizationActivityContract(),
                from.activityResultRegistry,
                activityResultCallback
            )
            webViewFallbackLauncher = WebViewAuthorizationActivityLauncher.create(
                from, activityResultCallback
            )
        }
    }

    fun launch(
        request: POAlternativePaymentMethodRequest,
        returnUrl: String,
        callback: (ProcessOutResult<POAlternativePaymentMethodResponse>) -> Unit
    ) {
        if (delegateCache.isCached()) {
            callback(
                ProcessOutResult.Failure(
                    POFailure.Code.Generic(),
                    "Launcher is already running."
                )
            )
            return
        }

        val delegate: WebAuthorizationDelegate = AlternativePaymentMethodWebAuthorizationDelegate(
            ProcessOut.instance.alternativePaymentMethods,
            request, callback
        )
        delegateCache.delegate = delegate

        if (ProcessOut.instance.browserCapabilities.isCustomTabsSupported()) {
            customTabLauncher.launch(
                CustomTabConfiguration(
                    uri = delegate.uri,
                    timeoutSeconds = null
                )
            )
        } else {
            webViewFallbackLauncher.launch(
                WebViewConfiguration(
                    uri = delegate.uri,
                    returnUris = listOf(
                        Uri.parse(ApiConstants.CHECKOUT_RETURN_URL),
                        Uri.parse(returnUrl)
                    ),
                    sdkVersion = ProcessOut.VERSION,
                    timeoutSeconds = null
                )
            )
        }
    }

    @Deprecated(
        message = "Use function with 'returnUrl'.",
        replaceWith = ReplaceWith("launch(request, returnUrl, callback)")
    )
    fun launch(
        request: POAlternativePaymentMethodRequest,
        callback: (ProcessOutResult<POAlternativePaymentMethodResponse>) -> Unit
    ) {
        launch(request, returnUrl = String(), callback)
    }

    private val activityResultCallback = ActivityResultCallback<ProcessOutActivityResult<Uri>> {
        when (it) {
            is ProcessOutActivityResult.Success -> delegateCache.remove()?.complete(uri = it.value)
            is ProcessOutActivityResult.Failure -> delegateCache.remove()?.complete(
                ProcessOutResult.Failure(it.code, it.message)
            )
        }
    }
}
