package com.processout.sdk.ui.checkout

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent
import com.processout.sdk.api.model.request.PODynamicCheckoutInvoiceRequest
import com.processout.sdk.api.model.response.toResponse
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.ui.R
import com.processout.sdk.ui.core.annotation.ProcessOutInternalApi
import kotlinx.coroutines.*

/**
 * Launcher that starts [DynamicCheckoutActivity] and provides the result.
 */
/** @suppress */
@ProcessOutInternalApi
class PODynamicCheckoutLauncher private constructor(
    private val launcher: ActivityResultLauncher<PODynamicCheckoutConfiguration>,
    private val activityOptions: ActivityOptionsCompat,
    private val delegate: PODynamicCheckoutDelegate,
    private val lifecycleOwner: LifecycleOwner,
    private val eventDispatcher: POEventDispatcher = POEventDispatcher
) {

    private val lifecycleScope = lifecycleOwner.lifecycleScope

    companion object {
        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: Fragment,
            delegate: PODynamicCheckoutDelegate,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ) = PODynamicCheckoutLauncher(
            launcher = from.registerForActivityResult(
                DynamicCheckoutActivityContract(),
                callback
            ),
            activityOptions = createActivityOptions(from.requireContext()),
            delegate = delegate,
            lifecycleOwner = from
        )

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: ComponentActivity,
            delegate: PODynamicCheckoutDelegate,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ) = PODynamicCheckoutLauncher(
            launcher = from.registerForActivityResult(
                DynamicCheckoutActivityContract(),
                from.activityResultRegistry,
                callback
            ),
            activityOptions = createActivityOptions(from),
            delegate = delegate,
            lifecycleOwner = from
        )

        private fun createActivityOptions(context: Context) =
            ActivityOptionsCompat.makeCustomAnimation(
                context, R.anim.po_slide_in_vertical, 0
            )
    }

    init {
        dispatchEvents()
        dispatchInvoice()
    }

    private fun dispatchEvents() {
        eventDispatcher.subscribe<POCardTokenizationEvent>(
            coroutineScope = lifecycleScope
        ) { delegate.onEvent(it) }
        eventDispatcher.subscribe<PONativeAlternativePaymentMethodEvent>(
            coroutineScope = lifecycleScope
        ) { delegate.onEvent(it) }
    }

    private fun dispatchInvoice() {
        eventDispatcher.subscribeForRequest<PODynamicCheckoutInvoiceRequest>(
            coroutineScope = lifecycleScope
        ) { request ->
            lifecycleScope.launch {
                val invoiceRequest = delegate.newInvoice(
                    currentInvoice = request.currentInvoice,
                    invalidationReason = request.invalidationReason
                )
                eventDispatcher.send(request.toResponse(invoiceRequest))
            }
        }
    }

    /**
     * Launches the activity.
     */
    fun launch(configuration: PODynamicCheckoutConfiguration) {
        launcher.launch(configuration, activityOptions)
    }
}
