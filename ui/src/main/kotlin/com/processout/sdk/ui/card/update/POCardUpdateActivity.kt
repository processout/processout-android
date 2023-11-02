package com.processout.sdk.ui.card.update

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.processout.sdk.ui.core.theme.ProcessOutTheme

class POCardUpdateActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProcessOutTheme {

            }
        }
    }
}
