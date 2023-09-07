package com.processout.sdk.ui.nativeapm

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment

/**
 * Launcher that starts [PONativeAlternativePaymentMethodActivity] and provides the result.
 */
class PONativeAlternativePaymentMethodLauncher private constructor() {

    private lateinit var launcher: ActivityResultLauncher<PONativeAlternativePaymentMethodConfiguration>

    companion object {
        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: Fragment,
            callback: PONativeAlternativePaymentMethodResultCallback
        ) = PONativeAlternativePaymentMethodLauncher().apply {
            launcher = from.registerForActivityResult(
                PONativeAlternativePaymentMethodActivityContract(),
                callback::onNativeAlternativePaymentMethodResult
            )
        }

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: ComponentActivity,
            callback: PONativeAlternativePaymentMethodResultCallback
        ) = PONativeAlternativePaymentMethodLauncher().apply {
            launcher = from.registerForActivityResult(
                PONativeAlternativePaymentMethodActivityContract(),
                from.activityResultRegistry,
                callback::onNativeAlternativePaymentMethodResult
            )
        }
    }

    /**
     * Launches the activity.
     */
    fun launch(configuration: PONativeAlternativePaymentMethodConfiguration) {
        launcher.launch(configuration)
    }
}
