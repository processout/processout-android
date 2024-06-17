package com.processout.sdk.ui.checkout

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutActivityResult

/**
 * Launcher that starts [DynamicCheckoutActivity] and provides the result.
 */
class PODynamicCheckoutLauncher private constructor() {

    private lateinit var launcher: ActivityResultLauncher<PODynamicCheckoutConfiguration>

    companion object {
        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: Fragment,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ) = PODynamicCheckoutLauncher().apply {
            launcher = from.registerForActivityResult(
                DynamicCheckoutActivityContract(),
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
        ) = PODynamicCheckoutLauncher().apply {
            launcher = from.registerForActivityResult(
                DynamicCheckoutActivityContract(),
                from.activityResultRegistry,
                callback
            )
        }
    }

    /**
     * Launches the activity.
     */
    fun launch(configuration: PODynamicCheckoutConfiguration) {
        launcher.launch(configuration)
    }
}
