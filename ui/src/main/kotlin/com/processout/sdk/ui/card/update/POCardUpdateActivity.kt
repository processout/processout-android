package com.processout.sdk.ui.card.update

import android.os.Bundle
import com.processout.sdk.ui.base.POBaseTransparentPortraitActivity

class POCardUpdateActivity : POBaseTransparentPortraitActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val bottomSheet = supportFragmentManager.findFragmentByTag(CardUpdateBottomSheet.tag)
            if (bottomSheet == null) {
                CardUpdateBottomSheet().apply {
                    arguments = intent.extras
                    show(supportFragmentManager, CardUpdateBottomSheet.tag)
                }
            }
        }
    }
}
