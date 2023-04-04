package com.processout.example.ui.shared

import android.app.Activity
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import com.processout.sdk.api.ProcessOutApi
import com.processout.sdk.api.model.request.PO3DS2AuthenticationRequest
import com.processout.sdk.api.model.response.PO3DS2Challenge
import com.processout.sdk.api.model.response.PO3DS2Configuration
import com.processout.sdk.api.model.response.PO3DSRedirect
import com.processout.sdk.api.service.PO3DSHandler
import com.processout.sdk.api.service.PO3DSResult
import com.processout.sdk.ui.threeds.PO3DSWebView

class Default3DSHandler(activity: Activity) : PO3DSHandler {

    private val rootLayout: FrameLayout = activity.findViewById(android.R.id.content)
    private val dialogBuilder = AlertDialog.Builder(activity)
    private val webViewBuilder = PO3DSWebView.Builder(activity)
    private var webView: WebView? = null

    override fun authenticationRequest(
        configuration: PO3DS2Configuration,
        callback: (PO3DSResult<PO3DS2AuthenticationRequest>) -> Unit
    ) {
        val request = PO3DS2AuthenticationRequest(
            deviceData = String(),
            sdkAppId = String(),
            sdkEphemeralPublicKey = String(),
            sdkReferenceNumber = String(),
            sdkTransactionId = String()
        )
        callback(PO3DSResult.Success(request))
    }

    override fun handle(challenge: PO3DS2Challenge, callback: (PO3DSResult<Boolean>) -> Unit) {
        with(dialogBuilder) {
            setTitle(ProcessOutApi.NAME)
            setMessage("Validate mobile 3DS2 challenge?")
            setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                callback(PO3DSResult.Success(true))
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
                callback(PO3DSResult.Success(false))
            }
        }.also { it.show() }
    }

    override fun handle(redirect: PO3DSRedirect, callback: (PO3DSResult<String>) -> Unit) {
        webView = webViewBuilder.with(redirect) { result ->
            destroyWebView()
            callback(result)
        }.build()
        if (redirect.isHeadlessModeAllowed.not()) {
            rootLayout.addView(webView)
        }
    }

    private fun destroyWebView() {
        webView?.run {
            loadUrl("about:blank")
            clearHistory()
            rootLayout.removeView(this)
            destroy()
        }.also { webView = null }
    }
}
