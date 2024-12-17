package com.processout.sdk.ui.nativeapm

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.processout.sdk.R

/**
 * Launcher that starts [PONativeAlternativePaymentMethodActivity] and provides the result.
 */
class PONativeAlternativePaymentMethodLauncher private constructor(
    private val launcher: ActivityResultLauncher<PONativeAlternativePaymentMethodConfiguration>,
    private val activityOptions: ActivityOptionsCompat
) {

    companion object {
        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: Fragment,
            callback: PONativeAlternativePaymentMethodResultCallback
        ) = PONativeAlternativePaymentMethodLauncher(
            launcher = from.registerForActivityResult(
                PONativeAlternativePaymentMethodActivityContract(),
                callback::onNativeAlternativePaymentMethodResult
            ),
            activityOptions = createActivityOptions(from.requireContext())
        )

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: ComponentActivity,
            callback: PONativeAlternativePaymentMethodResultCallback
        ) = PONativeAlternativePaymentMethodLauncher(
            launcher = from.registerForActivityResult(
                PONativeAlternativePaymentMethodActivityContract(),
                from.activityResultRegistry,
                callback::onNativeAlternativePaymentMethodResult
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
    fun launch(configuration: PONativeAlternativePaymentMethodConfiguration) {
        launcher.launch(configuration, activityOptions)
    }
}
