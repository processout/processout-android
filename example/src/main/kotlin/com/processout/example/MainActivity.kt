package com.processout.example

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.processout.sdk.api.model.response.POInvoice
import com.processout.sdk.ui.nativeapm.NativeAlternativePaymentMethodActivity

class MainActivity : AppCompatActivity() {

    private val viewModel: ProcessOutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            lifecycleScope.launchWhenCreated {
                viewModel.invoice.collect { startNativeAPM(it) }
            }
            viewModel.createInvoice()
        }
    }

    private fun startNativeAPM(invoice: POInvoice) {
        startActivity(
            NativeAlternativePaymentMethodActivity.buildStartIntent(
                this,
                "gway_conf_1F5fIrgLktm5fUBzrgN4jQA5RQlONhwG.sandbox",
                invoice.id
            )
        )
    }
}
