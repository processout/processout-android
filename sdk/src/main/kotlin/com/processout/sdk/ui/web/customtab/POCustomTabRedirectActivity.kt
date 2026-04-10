package com.processout.sdk.ui.web.customtab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.processout.sdk.api.ProcessOut

/**
 * Redirect activity that receives deep link and starts [POCustomTabAuthorizationActivity] providing return URL.
 * This ensures that back stack is cleared after redirection and that Custom Chrome Tabs activity is finished.
 */
class POCustomTabRedirectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.data?.let { uri ->
            ProcessOut.instance.processDeepLink(hostActivity = this, uri)
        }
        finish()
    }
}
