package com.processout.example.redirect

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.processout.sdk.api.ProcessOut

class MerchantRedirectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.data?.let { uri ->
            ProcessOut.instance.processDeepLink(hostActivity = this, uri)
        }
        finish()
    }
}
