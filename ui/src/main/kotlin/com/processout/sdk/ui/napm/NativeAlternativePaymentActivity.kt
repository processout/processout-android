package com.processout.sdk.ui.napm

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.processout.sdk.ui.base.POBaseTransparentPortraitActivity

internal class NativeAlternativePaymentActivity : POBaseTransparentPortraitActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (savedInstanceState == null) {
            val bottomSheet = supportFragmentManager.findFragmentByTag(NativeAlternativePaymentBottomSheet.tag)
            if (bottomSheet == null) {
                NativeAlternativePaymentBottomSheet().apply {
                    arguments = intent.extras
                    show(supportFragmentManager, NativeAlternativePaymentBottomSheet.tag)
                }
            }
        }
    }
}
