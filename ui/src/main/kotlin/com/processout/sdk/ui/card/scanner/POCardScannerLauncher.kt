package com.processout.sdk.ui.card.scanner

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.processout.sdk.R
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.ui.card.scanner.recognition.POScannedCard

/**
 * Launcher that starts [CardScannerActivity] and provides the result.
 */
class POCardScannerLauncher private constructor(
    private val launcher: ActivityResultLauncher<POCardScannerConfiguration>,
    private val activityOptions: ActivityOptionsCompat
) {

    companion object {
        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: Fragment,
            callback: (ProcessOutActivityResult<POScannedCard>) -> Unit
        ) = POCardScannerLauncher(
            launcher = from.registerForActivityResult(
                CardScannerActivityContract(),
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
            callback: (ProcessOutActivityResult<POScannedCard>) -> Unit
        ) = POCardScannerLauncher(
            launcher = from.registerForActivityResult(
                CardScannerActivityContract(),
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
    fun launch(configuration: POCardScannerConfiguration) {
        launcher.launch(configuration, activityOptions)
    }
}
