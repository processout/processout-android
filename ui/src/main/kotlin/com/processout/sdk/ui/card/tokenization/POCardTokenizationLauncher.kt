package com.processout.sdk.ui.card.tokenization

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi

/**
 * Launcher that starts [CardTokenizationActivity] and provides the result.
 */
/** @suppress */
@ProcessOutInternalApi
class POCardTokenizationLauncher private constructor() {

    private lateinit var launcher: ActivityResultLauncher<POCardTokenizationConfiguration>

    companion object {
        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: Fragment,
            callback: (ProcessOutActivityResult<POCardTokenizationResponse>) -> Unit
        ) = POCardTokenizationLauncher().apply {
            launcher = from.registerForActivityResult(
                CardTokenizationActivityContract(),
                callback
            )
        }

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: ComponentActivity,
            callback: (ProcessOutActivityResult<POCardTokenizationResponse>) -> Unit
        ) = POCardTokenizationLauncher().apply {
            launcher = from.registerForActivityResult(
                CardTokenizationActivityContract(),
                from.activityResultRegistry,
                callback
            )
        }
    }

    /**
     * Launches the activity.
     */
    fun launch(configuration: POCardTokenizationConfiguration) {
        launcher.launch(configuration)
    }
}
