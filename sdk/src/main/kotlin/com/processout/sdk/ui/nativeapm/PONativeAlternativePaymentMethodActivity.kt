package com.processout.sdk.ui.nativeapm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PONativeAlternativePaymentMethodActivity : AppCompatActivity(), BottomSheetCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val bottomSheet = supportFragmentManager.findFragmentByTag(
                PONativeAlternativePaymentMethodBottomSheet.TAG
            )
            if (bottomSheet == null) {
                PONativeAlternativePaymentMethodBottomSheet().apply {
                    arguments = intent.extras
                    isCancelable = false
                    show(supportFragmentManager, PONativeAlternativePaymentMethodBottomSheet.TAG)
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
