package com.processout.sdk.ui.card.update

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.processout.sdk.ui.base.POBaseTransparentPortraitActivity

internal class CardUpdateActivity : POBaseTransparentPortraitActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
