package com.processout.sdk.ui.web.customtab

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Redirect activity that receives deep link and starts [POCustomTabAuthorizationActivity] providing return URL.
 * This ensures that back stack is cleared after redirection and that Custom Chrome Tabs activity is finished.
 */
class POCustomTabRedirectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Intent(this, POCustomTabAuthorizationActivity::class.java).let {
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.data?.let { uri -> it.data = uri }
            startActivity(it)
        }
        finish()
    }
}
