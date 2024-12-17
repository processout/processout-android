package com.processout.sdk.ui.card.update

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.processout.sdk.R
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.core.ProcessOutActivityResult

/**
 * Launcher that starts [CardUpdateActivity] and provides the result.
 */
class POCardUpdateLauncher private constructor(
    private val launcher: ActivityResultLauncher<POCardUpdateConfiguration>,
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
        ) = POCardUpdateLauncher(
            launcher = from.registerForActivityResult(
                CardUpdateActivityContract(),
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
        ) = POCardUpdateLauncher(
            launcher = from.registerForActivityResult(
                CardUpdateActivityContract(),
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
    fun launch(configuration: POCardUpdateConfiguration) {
        launcher.launch(configuration, activityOptions)
    }
}
