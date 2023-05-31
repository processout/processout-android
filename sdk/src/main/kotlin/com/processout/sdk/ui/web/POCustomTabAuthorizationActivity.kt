package com.processout.sdk.ui.web

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.processout.sdk.api.model.threeds.PO3DSRedirect
import com.processout.sdk.ui.threeds.CustomTab3DSAuthorizationActivityContract

class POCustomTabAuthorizationActivity : AppCompatActivity() {

    private var redirect: PO3DSRedirect? = null

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        redirect = intent.getParcelableExtra(
            CustomTab3DSAuthorizationActivityContract.EXTRA_CONFIGURATION
        )
        redirect?.let {
            val intent: CustomTabsIntent = CustomTabsIntent.Builder()
                .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
                .build()
            intent.launchUrl(this, Uri.parse(it.url.toString()))
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
