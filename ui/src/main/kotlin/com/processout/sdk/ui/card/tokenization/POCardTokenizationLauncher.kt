package com.processout.sdk.ui.card.tokenization

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.processout.sdk.R
import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.request.POCardTokenizationPreferredSchemeRequest
import com.processout.sdk.api.model.request.POCardTokenizationProcessingRequest
import com.processout.sdk.api.model.request.POCardTokenizationShouldContinueRequest
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.api.model.response.toResponse
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.ui.card.tokenization.delegate.CardTokenizationEligibilityRequest
import com.processout.sdk.ui.card.tokenization.delegate.POCardTokenizationDelegate
import com.processout.sdk.ui.card.tokenization.delegate.toResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Launcher that starts [CardTokenizationActivity] and provides the result.
 */
class POCardTokenizationLauncher private constructor(
    private val scope: CoroutineScope,
    private val launcher: ActivityResultLauncher<POCardTokenizationConfiguration>,
    private val activityOptions: ActivityOptionsCompat,
    private val delegate: POCardTokenizationDelegate,
    private val eventDispatcher: POEventDispatcher = POEventDispatcher
) {

    companion object {
        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: Fragment,
            delegate: POCardTokenizationDelegate,
            callback: (ProcessOutActivityResult<POCard>) -> Unit
        ) = POCardTokenizationLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                CardTokenizationActivityContract(),
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
        ) = POCardTokenizationLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                CardTokenizationActivityContract(),
                callback
            ),
            activityOptions = createActivityOptions(from.requireContext()),
            delegate = object : POCardTokenizationDelegate {}
        )

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: ComponentActivity,
            delegate: POCardTokenizationDelegate,
            callback: (ProcessOutActivityResult<POCard>) -> Unit
        ) = POCardTokenizationLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                CardTokenizationActivityContract(),
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
        ) = POCardTokenizationLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                CardTokenizationActivityContract(),
                from.activityResultRegistry,
                callback
            ),
            activityOptions = createActivityOptions(from),
            delegate = object : POCardTokenizationDelegate {}
        )

        private fun createActivityOptions(context: Context) =
            ActivityOptionsCompat.makeCustomAnimation(
                context, R.anim.po_slide_in_vertical, 0
            )
    }

    init {
        dispatchEvents()
        dispatchTokenizedCard()
        dispatchEligibility()
        dispatchPreferredScheme()
        dispatchShouldContinue()
    }

    private fun dispatchEvents() {
        eventDispatcher.subscribe<POCardTokenizationEvent>(
            coroutineScope = scope
        ) { delegate.onEvent(it) }
    }

    private fun dispatchTokenizedCard() {
        eventDispatcher.subscribeForRequest<POCardTokenizationProcessingRequest>(
            coroutineScope = scope
        ) { request ->
            scope.launch {
                val result = delegate.processTokenizedCard(
                    card = request.card,
                    saveCard = request.saveCard
                )
                eventDispatcher.send(request.toResponse(result))
            }
        }
    }

    private fun dispatchEligibility() {
        eventDispatcher.subscribeForRequest<CardTokenizationEligibilityRequest>(
            coroutineScope = scope
        ) { request ->
            scope.launch {
                val eligibility = delegate.evaluateEligibility(
                    iin = request.iin,
                    issuerInformation = request.issuerInformation
                )
                eventDispatcher.send(request.toResponse(eligibility))
            }
        }
    }

    private fun dispatchPreferredScheme() {
        eventDispatcher.subscribeForRequest<POCardTokenizationPreferredSchemeRequest>(
            coroutineScope = scope
        ) { request ->
            scope.launch {
                val preferredScheme = delegate.preferredScheme(request.issuerInformation)
                eventDispatcher.send(request.toResponse(preferredScheme))
            }
        }
    }

    private fun dispatchShouldContinue() {
        eventDispatcher.subscribeForRequest<POCardTokenizationShouldContinueRequest>(
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
    fun launch(configuration: POCardTokenizationConfiguration) {
        launcher.launch(configuration, activityOptions)
    }
}
