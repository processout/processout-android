package com.processout.sdk.ui.nativeapm

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment

class PONativeAlternativePaymentMethodLauncher private constructor() {

    private lateinit var launcher: ActivityResultLauncher<PONativeAlternativePaymentMethodConfiguration>

    companion object {
        fun create(
            from: Fragment,
            callback: PONativeAlternativePaymentMethodResultCallback
        ) = PONativeAlternativePaymentMethodLauncher().apply {
            launcher = from.registerForActivityResult(
                PONativeAlternativePaymentMethodActivityContract(),
                callback::onNativeAlternativePaymentMethodResult
            )
        }

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

    fun launch(configuration: PONativeAlternativePaymentMethodConfiguration) {
        launcher.launch(configuration)
    }
}
