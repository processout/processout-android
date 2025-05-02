package com.processout.sdk.ui.card.update

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.processout.sdk.R
import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.event.POCardUpdateEvent
import com.processout.sdk.api.model.request.POCardUpdateShouldContinueRequest
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.api.model.response.toResponse
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.ui.card.update.delegate.POCardUpdateDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Launcher that starts [CardUpdateActivity] and provides the result.
 */
class POCardUpdateLauncher private constructor(
    private val scope: CoroutineScope,
    private val launcher: ActivityResultLauncher<POCardUpdateConfiguration>,
    private val activityOptions: ActivityOptionsCompat,
    private val delegate: POCardUpdateDelegate,
    private val eventDispatcher: POEventDispatcher = POEventDispatcher
) {

    companion object {
        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: Fragment,
            delegate: POCardUpdateDelegate,
            callback: (ProcessOutActivityResult<POCard>) -> Unit
        ) = POCardUpdateLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                CardUpdateActivityContract(),
                callback
            ),
            activityOptions = createActivityOptions(from.requireContext()),
            delegate = delegate
        )

        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        @Deprecated(message = "Use alternative function.")
        fun create(
            from: Fragment,
            callback: (ProcessOutActivityResult<POCard>) -> Unit
        ) = POCardUpdateLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                CardUpdateActivityContract(),
                callback
            ),
            activityOptions = createActivityOptions(from.requireContext()),
            delegate = object : POCardUpdateDelegate {}
        )

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: ComponentActivity,
            delegate: POCardUpdateDelegate,
            callback: (ProcessOutActivityResult<POCard>) -> Unit
        ) = POCardUpdateLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                CardUpdateActivityContract(),
                from.activityResultRegistry,
                callback
            ),
            activityOptions = createActivityOptions(from),
            delegate = delegate
        )

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        @Deprecated(message = "Use alternative function.")
        fun create(
            from: ComponentActivity,
            callback: (ProcessOutActivityResult<POCard>) -> Unit
        ) = POCardUpdateLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                CardUpdateActivityContract(),
                from.activityResultRegistry,
                callback
            ),
            activityOptions = createActivityOptions(from),
            delegate = object : POCardUpdateDelegate {}
        )

        private fun createActivityOptions(context: Context) =
            ActivityOptionsCompat.makeCustomAnimation(
                context, R.anim.po_slide_in_vertical, 0
            )
    }

    init {
        dispatchEvents()
        dispatchShouldContinue()
    }

    private fun dispatchEvents() {
        eventDispatcher.subscribe<POCardUpdateEvent>(
            coroutineScope = scope
        ) { delegate.onEvent(it) }
    }

    private fun dispatchShouldContinue() {
        eventDispatcher.subscribeForRequest<POCardUpdateShouldContinueRequest>(
            coroutineScope = scope
        ) { request ->
            scope.launch {
                val shouldContinue = delegate.shouldContinue(request.failure)
                eventDispatcher.send(request.toResponse(shouldContinue))
            }
        }
    }

    /**
     * Launches the activity.
     */
    fun launch(configuration: POCardUpdateConfiguration) {
        launcher.launch(configuration, activityOptions)
    }
}
