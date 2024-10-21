package com.processout.sdk.ui.checkout

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.event.PODynamicCheckoutEvent
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent
import com.processout.sdk.api.model.request.PODynamicCheckoutInvoiceAuthorizationRequest
import com.processout.sdk.api.model.request.PODynamicCheckoutInvoiceRequest
import com.processout.sdk.api.model.response.toResponse
import com.processout.sdk.api.service.PO3DSService
import com.processout.sdk.api.service.proxy3ds.POProxy3DSServiceRequest
import com.processout.sdk.api.service.proxy3ds.POProxy3DSServiceRequest.*
import com.processout.sdk.api.service.proxy3ds.POProxy3DSServiceResponse
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
    private val scope: CoroutineScope,
    private val launcher: ActivityResultLauncher<PODynamicCheckoutConfiguration>,
    private val activityOptions: ActivityOptionsCompat,
    private val delegate: PODynamicCheckoutDelegate,
    private val threeDSService: PO3DSService,
    private val eventDispatcher: POEventDispatcher = POEventDispatcher
) {

    companion object {
        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: Fragment,
            delegate: PODynamicCheckoutDelegate,
            threeDSService: PO3DSService,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ) = PODynamicCheckoutLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                DynamicCheckoutActivityContract(),
                callback
            ),
            activityOptions = createActivityOptions(from.requireContext()),
            delegate = delegate,
            threeDSService = threeDSService
        )

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: ComponentActivity,
            delegate: PODynamicCheckoutDelegate,
            threeDSService: PO3DSService,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ) = PODynamicCheckoutLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                DynamicCheckoutActivityContract(),
                from.activityResultRegistry,
                callback
            ),
            activityOptions = createActivityOptions(from),
            delegate = delegate,
            threeDSService = threeDSService
        )

        private fun createActivityOptions(context: Context) =
            ActivityOptionsCompat.makeCustomAnimation(
                context, R.anim.po_slide_in_vertical, 0
            )
    }

    init {
        dispatchEvents()
        dispatchInvoice()
        dispatchInvoiceAuthorizationRequest()
        dispatch3DSService()
    }

    private fun dispatchEvents() {
        eventDispatcher.subscribe<PODynamicCheckoutEvent>(
            coroutineScope = scope
        ) { delegate.onEvent(it) }
        eventDispatcher.subscribe<POCardTokenizationEvent>(
            coroutineScope = scope
        ) { delegate.onEvent(it) }
        eventDispatcher.subscribe<PONativeAlternativePaymentMethodEvent>(
            coroutineScope = scope
        ) { delegate.onEvent(it) }
    }

    private fun dispatchInvoice() {
        eventDispatcher.subscribeForRequest<PODynamicCheckoutInvoiceRequest>(
            coroutineScope = scope
        ) { request ->
            scope.launch {
                val invoiceRequest = delegate.newInvoice(
                    currentInvoice = request.currentInvoice,
                    invalidationReason = request.invalidationReason
                )
                eventDispatcher.send(request.toResponse(invoiceRequest))
            }
        }
    }

    private fun dispatchInvoiceAuthorizationRequest() {
        eventDispatcher.subscribeForRequest<PODynamicCheckoutInvoiceAuthorizationRequest>(
            coroutineScope = scope
        ) { request ->
            scope.launch {
                val invoiceAuthorizationRequest = delegate.invoiceAuthorizationRequest(
                    request = request.request,
                    paymentMethod = request.paymentMethod
                )
                eventDispatcher.send(request.toResponse(invoiceAuthorizationRequest))
            }
        }
    }

    private fun dispatch3DSService() {
        eventDispatcher.subscribeForRequest<POProxy3DSServiceRequest>(
            coroutineScope = scope
        ) { request ->
            when (request) {
                is Authentication -> threeDSService.authenticationRequest(request.configuration) {
                    scope.launch {
                        eventDispatcher.send(
                            POProxy3DSServiceResponse.Authentication(
                                uuid = request.uuid,
                                result = it
                            )
                        )
                    }
                }
                is Challenge -> threeDSService.handle(request.challenge) {
                    scope.launch {
                        eventDispatcher.send(
                            POProxy3DSServiceResponse.Challenge(
                                uuid = request.uuid,
                                result = it
                            )
                        )
                    }
                }
                is Redirect -> threeDSService.handle(request.redirect) {
                    scope.launch {
                        eventDispatcher.send(
                            POProxy3DSServiceResponse.Redirect(
                                uuid = request.uuid,
                                result = it
                            )
                        )
                    }
                }
                is Cleanup -> threeDSService.cleanup()
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
