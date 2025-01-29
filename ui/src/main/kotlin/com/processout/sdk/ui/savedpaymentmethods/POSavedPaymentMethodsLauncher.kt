package com.processout.sdk.ui.savedpaymentmethods

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.processout.sdk.R
import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.event.POSavedPaymentMethodsEvent
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import kotlinx.coroutines.CoroutineScope

/**
 * Launcher that starts [SavedPaymentMethodsActivity] and provides the result.
 */
/** @suppress */
@ProcessOutInternalApi
class POSavedPaymentMethodsLauncher private constructor(
    private val scope: CoroutineScope,
    private val launcher: ActivityResultLauncher<POSavedPaymentMethodsConfiguration>,
    private val activityOptions: ActivityOptionsCompat,
    private val delegate: POSavedPaymentMethodsDelegate,
    private val eventDispatcher: POEventDispatcher = POEventDispatcher
) {

    companion object {
        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: Fragment,
            delegate: POSavedPaymentMethodsDelegate,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ) = POSavedPaymentMethodsLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                SavedPaymentMethodsActivityContract(),
                callback
            ),
            activityOptions = createActivityOptions(from.requireContext()),
            delegate = delegate
        )

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: ComponentActivity,
            delegate: POSavedPaymentMethodsDelegate,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ) = POSavedPaymentMethodsLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                SavedPaymentMethodsActivityContract(),
                from.activityResultRegistry,
                callback
            ),
            activityOptions = createActivityOptions(from),
            delegate = delegate
        )

        private fun createActivityOptions(context: Context) =
            ActivityOptionsCompat.makeCustomAnimation(
                context, R.anim.po_slide_in_vertical, 0
            )
    }

    init {
        dispatchEvents()
    }

    private fun dispatchEvents() {
        eventDispatcher.subscribe<POSavedPaymentMethodsEvent>(
            coroutineScope = scope
        ) { delegate.onEvent(it) }
    }

    /**
     * Launches the activity.
     */
    fun launch(configuration: POSavedPaymentMethodsConfiguration) {
        launcher.launch(configuration, activityOptions)
    }
}
