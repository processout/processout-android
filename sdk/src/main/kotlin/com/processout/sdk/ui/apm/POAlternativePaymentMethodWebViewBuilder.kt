package com.processout.sdk.ui.apm

import android.app.Activity
import android.net.Uri
import android.webkit.WebView
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.request.POAlternativePaymentMethodRequest
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.api.network.ApiConstants
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.web.ProcessOutWebView
import com.processout.sdk.ui.web.WebViewDelegate

class POAlternativePaymentMethodWebViewBuilder(
    private val activity: Activity
) {
    private var request: POAlternativePaymentMethodRequest? = null
    private var delegate: WebViewDelegate? = null

    fun with(
        request: POAlternativePaymentMethodRequest,
        callback: (ProcessOutResult<POAlternativePaymentMethodResponse>) -> Unit
    ) = apply {
        this.request = request
        this.delegate = AlternativePaymentMethodWebViewDelegate(
            ProcessOut.instance.alternativePaymentMethods,
            request, callback
        )
    }

    fun build(): WebView = ProcessOutWebView(
        activity,
        ProcessOutWebView.Configuration(
            returnUris = listOf(Uri.parse(ApiConstants.CHECKOUT_URL)),
            sdkVersion = ProcessOut.VERSION,
            timeoutSeconds = null
        ),
        delegate
    )
}
