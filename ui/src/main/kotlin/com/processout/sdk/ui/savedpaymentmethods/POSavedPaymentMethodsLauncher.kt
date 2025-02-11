package com.processout.sdk.ui.savedpaymentmethods

import android.content.Context
import android.content.Intent
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
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsActivityContract.Companion.EXTRA_FORCE_FINISH
import kotlinx.coroutines.CoroutineScope

/**
 * Launcher that starts [SavedPaymentMethodsActivity] and provides the result.
 */
class POSavedPaymentMethodsLauncher private constructor(
    private val parentActivity: ComponentActivity,
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
            parentActivity = from.requireActivity(),
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
            parentActivity = from,
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

    /**
     * Finishes the activity.
     */
    fun finish() {
        Intent(parentActivity, SavedPaymentMethodsActivity::class.java).let {
            it.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            it.putExtra(EXTRA_FORCE_FINISH, true)
            parentActivity.startActivity(it)
        }
    }
}
