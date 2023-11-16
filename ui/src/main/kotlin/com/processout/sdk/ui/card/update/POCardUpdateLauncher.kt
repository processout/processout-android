package com.processout.sdk.ui.card.update

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutActivityResult

/**
 * Launcher that starts [CardUpdateActivity] and provides the result.
 */
class POCardUpdateLauncher private constructor() {

    private lateinit var launcher: ActivityResultLauncher<POCardUpdateConfiguration>

    companion object {
        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: Fragment,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ) = POCardUpdateLauncher().apply {
            launcher = from.registerForActivityResult(
                CardUpdateActivityContract(),
                callback
            )
        }

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: ComponentActivity,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ) = POCardUpdateLauncher().apply {
            launcher = from.registerForActivityResult(
                CardUpdateActivityContract(),
                from.activityResultRegistry,
                callback
            )
        }
    }

    /**
     * Launches the activity.
     */
    fun launch(configuration: POCardUpdateConfiguration) {
        launcher.launch(configuration)
    }
}
