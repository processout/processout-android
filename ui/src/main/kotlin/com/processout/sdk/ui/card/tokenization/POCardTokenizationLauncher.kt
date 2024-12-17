package com.processout.sdk.ui.card.tokenization

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.processout.sdk.R
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.core.ProcessOutActivityResult

/**
 * Launcher that starts [CardTokenizationActivity] and provides the result.
 */
class POCardTokenizationLauncher private constructor(
    private val launcher: ActivityResultLauncher<POCardTokenizationConfiguration>,
    private val activityOptions: ActivityOptionsCompat
) {

    companion object {
        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: Fragment,
            callback: (ProcessOutActivityResult<POCard>) -> Unit
        ) = POCardTokenizationLauncher(
            launcher = from.registerForActivityResult(
                CardTokenizationActivityContract(),
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
            callback: (ProcessOutActivityResult<POCard>) -> Unit
        ) = POCardTokenizationLauncher(
            launcher = from.registerForActivityResult(
                CardTokenizationActivityContract(),
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
    fun launch(configuration: POCardTokenizationConfiguration) {
        launcher.launch(configuration, activityOptions)
    }
}
