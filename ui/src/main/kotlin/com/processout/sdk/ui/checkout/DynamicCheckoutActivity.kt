package com.processout.sdk.ui.checkout

import android.os.Bundle
import androidx.activity.compose.setContent
import com.processout.sdk.ui.base.BaseTransparentPortraitActivity
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.theme.ProcessOutTheme

internal class DynamicCheckoutActivity : BaseTransparentPortraitActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProcessOutTheme {
                POText(text = "DynamicCheckoutActivity")
            }
        }
    }
}
