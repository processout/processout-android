package com.processout.sdk.ui.web

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.processout.sdk.api.service.POBrowserCapabilitiesService.Companion.CHROME_PACKAGE
import com.processout.sdk.ui.web.CustomTabAuthorizationActivityContract.Companion.EXTRA_CONFIGURATION

class POCustomTabAuthorizationActivity : AppCompatActivity() {

    private var configuration: CustomTabConfiguration? = null

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configuration = intent.getParcelableExtra(EXTRA_CONFIGURATION)

        configuration?.let {
            val customTabsIntent: CustomTabsIntent = CustomTabsIntent.Builder()
                .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
                .build()
            customTabsIntent.intent.setPackage(CHROME_PACKAGE)
            customTabsIntent.launchUrl(this, it.uri)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // TODO: handle activity result
        finish()
    }

    override fun onResume() {
        super.onResume()
        // TODO: handle cancel
    }
}
