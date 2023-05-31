package com.processout.sdk.ui.web

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class POCustomTabRedirectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Intent(this, POCustomTabAuthorizationActivity::class.java).let {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.data?.let { uri -> it.data = uri }
            startActivity(it)
        }
        finish()
    }
}
