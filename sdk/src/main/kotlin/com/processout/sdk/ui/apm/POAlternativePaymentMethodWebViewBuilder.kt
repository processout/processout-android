package com.processout.sdk.ui.apm

import android.app.Activity
import android.net.Uri
import android.webkit.WebView
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.api.network.ApiConstants
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.web.WebAuthorizationDelegate
import com.processout.sdk.ui.web.webview.ProcessOutWebView
import com.processout.sdk.ui.web.webview.WebViewConfiguration

@Deprecated("Use POAlternativePaymentMethodCustomTabLauncher.")
class POAlternativePaymentMethodWebViewBuilder(
    private val activity: Activity
) {
    private var delegate: WebAuthorizationDelegate? = null

    fun with(
        request: POAlternativePaymentMethodRequest,
        callback: (ProcessOutResult<POAlternativePaymentMethodResponse>) -> Unit
    ) = apply {
        delegate = AlternativePaymentMethodWebAuthorizationDelegate(
            ProcessOut.instance.alternativePaymentMethods,
            request, callback
        )
    }

    fun build(): WebView = ProcessOutWebView(
        activity,
        WebViewConfiguration(
            uri = delegate?.uri,
            returnUris = listOf(Uri.parse(ApiConstants.CHECKOUT_RETURN_URL)),
            sdkVersion = ProcessOut.VERSION,
            timeoutSeconds = null
        )
    ) { result ->
        when (result) {
            is ProcessOutResult.Success -> delegate?.complete(uri = result.value)
            is ProcessOutResult.Failure -> delegate?.complete(failure = result)
        }
    }
}
