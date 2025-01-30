package com.processout.sdk.ui.savedpaymentmethods

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.processout.sdk.ui.base.BaseTransparentPortraitActivity

internal class SavedPaymentMethodsActivity : BaseTransparentPortraitActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (savedInstanceState == null) {
            val bottomSheet = supportFragmentManager.findFragmentByTag(SavedPaymentMethodsBottomSheet.tag)
            if (bottomSheet == null) {
                SavedPaymentMethodsBottomSheet().apply {
                    arguments = intent.extras
                    show(supportFragmentManager, SavedPaymentMethodsBottomSheet.tag)
                }
            }
        }
    }
}
