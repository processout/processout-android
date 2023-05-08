package com.processout.sdk.ui.threeds

import android.app.Activity
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.model.threeds.PO3DS2AuthenticationRequest
import com.processout.sdk.api.model.threeds.PO3DS2Challenge
import com.processout.sdk.api.model.threeds.PO3DS2Configuration
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.api.service.PO3DSService
import com.processout.sdk.core.ProcessOutResult

class POTest3DSService(activity: Activity) : PO3DSService {

    private val rootLayout: FrameLayout = activity.findViewById(android.R.id.content)
    private val dialogBuilder = AlertDialog.Builder(activity)
    private val webViewBuilder = PO3DSRedirectWebViewBuilder(activity)
    private var webView: WebView? = null

    override fun authenticationRequest(
        configuration: PO3DS2Configuration,
        callback: (ProcessOutResult<PO3DS2AuthenticationRequest>) -> Unit
    ) {
        val request = PO3DS2AuthenticationRequest(
            deviceData = String(),
            sdkAppId = String(),
            sdkEphemeralPublicKey = String(),
            sdkReferenceNumber = String(),
            sdkTransactionId = String()
        )
        callback(ProcessOutResult.Success(request))
    }

    override fun handle(challenge: PO3DS2Challenge, callback: (ProcessOutResult<Boolean>) -> Unit) {
        with(dialogBuilder) {
            setTitle(ProcessOut.NAME)
            setMessage("Validate mobile 3DS2 challenge?")
            setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                callback(ProcessOutResult.Success(true))
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
                callback(ProcessOutResult.Success(false))
            }
        }.also { it.show() }
    }

    override fun handle(redirect: PO3DSRedirect, callback: (ProcessOutResult<String>) -> Unit) {
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
            rootLayout.removeView(this)
            destroy()
        }.also { webView = null }
    }
}
