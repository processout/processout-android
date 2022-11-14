package com.processout.sdk.ui.nativeapm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf

class NativeAlternativePaymentMethodActivity : AppCompatActivity(), BottomSheetCallback {

    companion object {
        fun buildStartIntent(
            context: Context,
            gatewayConfigurationId: String,
            invoiceId: String
        ) = Intent(context, NativeAlternativePaymentMethodActivity::class.java)
            .apply {
                putExtras(
                    bundleOf(
                        NativeAPMBundleKey.GATEWAY_CONFIGURATION_ID to gatewayConfigurationId,
                        NativeAPMBundleKey.INVOICE_ID to invoiceId
                    )
                )
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val bottomSheet = supportFragmentManager.findFragmentByTag(
                NativeAlternativePaymentMethodBottomSheet.TAG
            )
            if (bottomSheet == null) {
                NativeAlternativePaymentMethodBottomSheet().apply {
                    arguments = intent.extras
                    isCancelable = false
                    show(supportFragmentManager, NativeAlternativePaymentMethodBottomSheet.TAG)
                }
            }
        }
    }

    override fun onBottomSheetBackPressed() {
        finish()
    }
}

internal interface BottomSheetCallback {
    fun onBottomSheetBackPressed()
}
