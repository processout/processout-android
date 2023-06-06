package com.processout.sdk.ui.web

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.processout.sdk.ui.web.WebViewAuthorizationActivityContract.Companion.EXTRA_CONFIGURATION

class POWebViewAuthorizationActivity : AppCompatActivity() {

    private var configuration: WebViewConfiguration? = null

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configuration = intent.getParcelableExtra(EXTRA_CONFIGURATION)
        configuration?.let { configuration ->
            val webView = ProcessOutWebView(this, configuration) { result ->
                // TODO
            }
            setContentView(webView)
        }
    }
}
