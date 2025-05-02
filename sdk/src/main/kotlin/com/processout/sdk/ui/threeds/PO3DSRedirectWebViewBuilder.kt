package com.processout.sdk.ui.threeds

import android.app.Activity
import android.net.Uri
import android.webkit.WebView
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.api.network.ApiConstants
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.web.WebAuthorizationDelegate
import com.processout.sdk.ui.web.webview.POWebViewConfiguration
import com.processout.sdk.ui.web.webview.ProcessOutWebView

@Deprecated("Use PO3DSRedirectCustomTabLauncher.")
class PO3DSRedirectWebViewBuilder(
    private val activity: Activity
) {
    private var redirect: PO3DSRedirect? = null
    private var delegate: WebAuthorizationDelegate? = null

    fun with(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit) =
        apply {
            this.redirect = redirect
            this.delegate = ThreeDSRedirectWebAuthorizationDelegate(
                Uri.parse(redirect.url.toString()),
                callback
            )
        }

    fun build(): WebView = ProcessOutWebView(
        activity,
        POWebViewConfiguration(
            uri = delegate?.uri,
            returnUris = listOf(Uri.parse(ApiConstants.CHECKOUT_RETURN_URL)),
            sdkVersion = ProcessOut.VERSION,
            timeoutSeconds = redirect?.timeoutSeconds
        )
    ) { result ->
        when (result) {
            is ProcessOutResult.Success -> delegate?.complete(uri = result.value)
            is ProcessOutResult.Failure -> delegate?.complete(failure = result)
        }
    }
}
