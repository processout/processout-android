package com.processout.sdk.ui.card.tokenization

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.processout.sdk.ui.base.POBaseTransparentPortraitActivity

/**
 * Activity that handles card tokenization.
 */
class POCardTokenizationActivity : POBaseTransparentPortraitActivity() {

    companion object {
        var instance: POCardTokenizationActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        enableEdgeToEdge()
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

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}
