package com.processout.example.ui.screen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.processout.example.R
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodConfiguration
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodLauncher
import com.processout.sdk.ui.nativeapm.PONativeAlternativePaymentMethodResult

class MainActivity : AppCompatActivity() {

    private lateinit var launcher: PONativeAlternativePaymentMethodLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        launcher = PONativeAlternativePaymentMethodLauncher.create(
            this, ::onNativeAlternativePaymentMethodResult
        )
    }

    private fun onNativeAlternativePaymentMethodResult(result: PONativeAlternativePaymentMethodResult) {
        when (result) {
            PONativeAlternativePaymentMethodResult.Success -> TODO()
            is PONativeAlternativePaymentMethodResult.Failure -> TODO()
        }
    }

    private fun startDefault(gatewayConfigurationId: String, invoiceId: String) {
        launcher.launch(
            PONativeAlternativePaymentMethodConfiguration(gatewayConfigurationId, invoiceId)
        )
    }
}
