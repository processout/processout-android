package com.processout.sdk.ui.card.tokenization

import android.os.Bundle
import com.processout.sdk.ui.base.BaseTransparentPortraitActivity

internal class CardTokenizationActivity : BaseTransparentPortraitActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val bottomSheet = supportFragmentManager.findFragmentByTag(CardTokenizationBottomSheet.tag)
            if (bottomSheet == null) {
                CardTokenizationBottomSheet().apply {
                    arguments = intent.extras
                    show(supportFragmentManager, CardTokenizationBottomSheet.tag)
                }
            }
        }
    }
}
