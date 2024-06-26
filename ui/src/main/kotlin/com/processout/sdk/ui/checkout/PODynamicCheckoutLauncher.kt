package com.processout.sdk.ui.checkout

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.ui.R
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi

/**
 * Launcher that starts [DynamicCheckoutActivity] and provides the result.
 */
/** @suppress */
@ProcessOutInternalApi
class PODynamicCheckoutLauncher private constructor(
    private val launcher: ActivityResultLauncher<PODynamicCheckoutConfiguration>,
    private val activityOptions: ActivityOptionsCompat
) {

    companion object {
        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: Fragment,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ) = PODynamicCheckoutLauncher(
            launcher = from.registerForActivityResult(
                DynamicCheckoutActivityContract(),
                callback
            ),
            activityOptions = createActivityOptions(from.requireContext())
        )

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: ComponentActivity,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ) = PODynamicCheckoutLauncher(
            launcher = from.registerForActivityResult(
                DynamicCheckoutActivityContract(),
                from.activityResultRegistry,
                callback
            ),
            activityOptions = createActivityOptions(from)
        )

        private fun createActivityOptions(context: Context) =
            ActivityOptionsCompat.makeCustomAnimation(
                context, R.anim.po_slide_in_vertical, 0
            )
    }

    /**
     * Launches the activity.
     */
    fun launch(configuration: PODynamicCheckoutConfiguration) {
        launcher.launch(configuration, activityOptions)
    }
}
