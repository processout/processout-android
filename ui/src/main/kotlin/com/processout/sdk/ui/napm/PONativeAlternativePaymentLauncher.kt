package com.processout.sdk.ui.napm

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.processout.sdk.R
import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.request.PONativeAlternativePaymentMethodDefaultValuesRequest
import com.processout.sdk.api.model.response.toResponse
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.annotation.ProcessOutInternalApi
import com.processout.sdk.ui.napm.delegate.PONativeAlternativePaymentDelegate
import com.processout.sdk.ui.napm.delegate.PONativeAlternativePaymentEvent
import com.processout.sdk.ui.napm.delegate.v2.NativeAlternativePaymentDefaultValuesRequest
import com.processout.sdk.ui.napm.delegate.v2.toResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Launcher that starts [NativeAlternativePaymentActivity] and provides the result.
 */
class PONativeAlternativePaymentLauncher private constructor(
    private val scope: CoroutineScope,
    private val launcher: ActivityResultLauncher<PONativeAlternativePaymentConfiguration>,
    private val activityOptions: ActivityOptionsCompat,
    private val delegate: PONativeAlternativePaymentDelegate,
    private val delegateV2: com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentDelegate,
    private val eventDispatcher: POEventDispatcher = POEventDispatcher
) {

    companion object {
        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: Fragment,
            delegate: PONativeAlternativePaymentDelegate,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ) = PONativeAlternativePaymentLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                NativeAlternativePaymentActivityContract(),
                callback
            ),
            activityOptions = createActivityOptions(from.requireContext()),
            delegate = delegate,
            delegateV2 = object : com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentDelegate {}
        )

        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        /** @suppress */
        @ProcessOutInternalApi
        fun create(
            from: Fragment,
            delegate: com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentDelegate,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ) = PONativeAlternativePaymentLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                NativeAlternativePaymentActivityContract(),
                callback
            ),
            activityOptions = createActivityOptions(from.requireContext()),
            delegate = object : PONativeAlternativePaymentDelegate {},
            delegateV2 = delegate
        )

        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        @Deprecated(message = "Use alternative function.")
        fun create(
            from: Fragment,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ) = PONativeAlternativePaymentLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                NativeAlternativePaymentActivityContract(),
                callback
            ),
            activityOptions = createActivityOptions(from.requireContext()),
            delegate = object : PONativeAlternativePaymentDelegate {},
            delegateV2 = object : com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentDelegate {}
        )

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: ComponentActivity,
            delegate: PONativeAlternativePaymentDelegate,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ) = PONativeAlternativePaymentLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                NativeAlternativePaymentActivityContract(),
                from.activityResultRegistry,
                callback
            ),
            activityOptions = createActivityOptions(from),
            delegate = delegate,
            delegateV2 = object : com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentDelegate {}
        )

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        /** @suppress */
        @ProcessOutInternalApi
        fun create(
            from: ComponentActivity,
            delegate: com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentDelegate,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ) = PONativeAlternativePaymentLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                NativeAlternativePaymentActivityContract(),
                from.activityResultRegistry,
                callback
            ),
            activityOptions = createActivityOptions(from),
            delegate = object : PONativeAlternativePaymentDelegate {},
            delegateV2 = delegate
        )

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        @Deprecated(message = "Use alternative function.")
        fun create(
            from: ComponentActivity,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ) = PONativeAlternativePaymentLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                NativeAlternativePaymentActivityContract(),
                from.activityResultRegistry,
                callback
            ),
            activityOptions = createActivityOptions(from),
            delegate = object : PONativeAlternativePaymentDelegate {},
            delegateV2 = object : com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentDelegate {}
        )

        private fun createActivityOptions(context: Context) =
            ActivityOptionsCompat.makeCustomAnimation(
                context, R.anim.po_slide_in_vertical, 0
            )
    }

    init {
        dispatchEvents()
        dispatchDefaultValues()
    }

    private fun dispatchEvents() {
        eventDispatcher.subscribe<PONativeAlternativePaymentEvent>(
            coroutineScope = scope
        ) { delegate.onEvent(it) }
        eventDispatcher.subscribe<com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentEvent>(
            coroutineScope = scope
        ) { delegateV2.onEvent(it) }
    }

    private fun dispatchDefaultValues() {
        eventDispatcher.subscribeForRequest<PONativeAlternativePaymentMethodDefaultValuesRequest>(
            coroutineScope = scope
        ) { request ->
            scope.launch {
                val defaultValues = delegate.defaultValues(
                    gatewayConfigurationId = request.gatewayConfigurationId,
                    parameters = request.parameters
                )
                eventDispatcher.send(request.toResponse(defaultValues))
            }
        }
        eventDispatcher.subscribeForRequest<NativeAlternativePaymentDefaultValuesRequest>(
            coroutineScope = scope
        ) { request ->
            scope.launch {
                val defaultValues = delegateV2.defaultValues(
                    gatewayConfigurationId = request.gatewayConfigurationId,
                    parameters = request.parameters
                )
                eventDispatcher.send(request.toResponse(defaultValues))
            }
        }
    }

    /**
     * Launches the activity.
     */
    fun launch(configuration: PONativeAlternativePaymentConfiguration) {
        launcher.launch(configuration, activityOptions)
    }
}
