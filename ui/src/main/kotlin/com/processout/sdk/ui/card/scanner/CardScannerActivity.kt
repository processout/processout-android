package com.processout.sdk.ui.card.scanner

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.processout.sdk.ui.base.BaseTransparentPortraitActivity

internal class CardScannerActivity : BaseTransparentPortraitActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (savedInstanceState == null) {
            val bottomSheet = supportFragmentManager.findFragmentByTag(CardScannerBottomSheet.tag)
            if (bottomSheet == null) {
                CardScannerBottomSheet().apply {
                    arguments = intent.extras
                    show(supportFragmentManager, CardScannerBottomSheet.tag)
                }
            }
        }
    }
}
