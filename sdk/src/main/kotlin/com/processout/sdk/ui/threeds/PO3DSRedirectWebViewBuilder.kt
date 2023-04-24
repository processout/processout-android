package com.processout.sdk.ui.threeds

import android.app.Activity
import android.net.Uri
import android.webkit.WebView
import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.api.network.ApiConstants
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.web.ProcessOutWebView
import com.processout.sdk.ui.web.WebViewDelegate

class PO3DSRedirectWebViewBuilder(
    private val activity: Activity
) {
    private var redirect: PO3DSRedirect? = null
    private var delegate: WebViewDelegate? = null

    fun with(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit) =
        apply {
            this.redirect = redirect
            this.delegate = ThreeDSRedirectWebViewDelegate(
                redirect.url.let { Uri.parse(it.toString()) },
                callback
            )
        }

    fun build(): WebView = ProcessOutWebView(
        activity,
        ProcessOutWebView.Configuration(
            returnUris = listOf(Uri.parse(ApiConstants.CHECKOUT_URL)),
            sdkVersion = ProcessOutApi.VERSION,
            timeoutSeconds = redirect?.timeoutSeconds
        ),
        delegate
    )
}
