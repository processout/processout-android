package com.processout.sdk.ui.checkout

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.processout.sdk.R
import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.event.PONativeAlternativePaymentMethodEvent
import com.processout.sdk.api.model.event.POSavedPaymentMethodsEvent
import com.processout.sdk.api.model.request.*
import com.processout.sdk.api.model.response.toResponse
import com.processout.sdk.api.service.PO3DSService
import com.processout.sdk.api.service.proxy3ds.POProxy3DSServiceRequest
import com.processout.sdk.api.service.proxy3ds.POProxy3DSServiceRequest.*
import com.processout.sdk.api.service.proxy3ds.POProxy3DSServiceResponse
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.ui.checkout.delegate.*
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
    private val threeDSService: PO3DSService,
    private val delegate: PODynamicCheckoutDelegate,
    private val eventDispatcher: POEventDispatcher = POEventDispatcher
) {

    companion object {
        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: Fragment,
            threeDSService: PO3DSService,
            delegate: PODynamicCheckoutDelegate,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ) = PODynamicCheckoutLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                DynamicCheckoutActivityContract(),
                callback
            ),
            activityOptions = createActivityOptions(from.requireContext()),
            threeDSService = threeDSService,
            delegate = delegate
        )

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: ComponentActivity,
            threeDSService: PO3DSService,
            delegate: PODynamicCheckoutDelegate,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ) = PODynamicCheckoutLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                DynamicCheckoutActivityContract(),
                from.activityResultRegistry,
                callback
            ),
            activityOptions = createActivityOptions(from),
            threeDSService = threeDSService,
            delegate = delegate
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
        dispatchPreferredScheme()
        dispatchDefaultValues()
        dispatchAlternativePaymentConfiguration()
        dispatchSavedPaymentMethodsConfiguration()
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
        eventDispatcher.subscribe<POSavedPaymentMethodsEvent>(
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
        eventDispatcher.subscribeForRequest<DynamicCheckoutInvoiceAuthorizationRequest>(
            coroutineScope = scope
        ) { request ->
            scope.launch {
                val invoiceAuthorizationRequest = delegate.invoiceAuthorizationRequest(
                    paymentMethod = request.paymentMethod,
                    request = request.request
                )
                eventDispatcher.send(request.toResponse(invoiceAuthorizationRequest))
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

    private fun dispatchDefaultValues() {
        eventDispatcher.subscribeForRequest<DynamicCheckoutAlternativePaymentDefaultValuesRequest>(
            coroutineScope = scope
        ) { request ->
            scope.launch {
                val defaultValues = delegate.defaultValues(
                    paymentMethod = request.paymentMethod,
                    parameters = request.parameters
                )
                eventDispatcher.send(request.toResponse(defaultValues))
            }
        }
    }

    private fun dispatchAlternativePaymentConfiguration() {
        eventDispatcher.subscribeForRequest<DynamicCheckoutAlternativePaymentConfigurationRequest>(
            coroutineScope = scope
        ) { request ->
            scope.launch {
                val configuration = delegate.alternativePayment(
                    paymentMethod = request.paymentMethod,
                    configuration = request.configuration
                )
                eventDispatcher.send(request.toResponse(configuration))
            }
        }
    }

    private fun dispatchSavedPaymentMethodsConfiguration() {
        eventDispatcher.subscribeForRequest<DynamicCheckoutSavedPaymentMethodsConfigurationRequest>(
            coroutineScope = scope
        ) { request ->
            scope.launch {
                val configuration = delegate.savedPaymentMethods(request.configuration)
                eventDispatcher.send(request.toResponse(configuration))
            }
        }
    }

    private fun dispatch3DSService() {
        eventDispatcher.subscribeForRequest<POProxy3DSServiceRequest>(
            coroutineScope = scope
        ) { request ->
            when (request) {
                is Authentication -> threeDSService.authenticationRequest(request.configuration) {
                    dispatch(
                        POProxy3DSServiceResponse.Authentication(
                            uuid = request.uuid,
                            result = it
                        )
                    )
                }
                is Challenge -> threeDSService.handle(request.challenge) {
                    dispatch(
                        POProxy3DSServiceResponse.Challenge(
                            uuid = request.uuid,
                            result = it
                        )
                    )
                }
                is Redirect -> threeDSService.handle(request.redirect) {
                    dispatch(
                        POProxy3DSServiceResponse.Redirect(
                            uuid = request.uuid,
                            result = it
                        )
                    )
                }
                is Cleanup -> {
                    threeDSService.cleanup()
                    dispatch(POProxy3DSServiceResponse.Close(uuid = request.uuid))
                }
            }
        }
    }

    private fun dispatch(response: POProxy3DSServiceResponse) {
        scope.launch { eventDispatcher.send(response) }
    }

    /**
     * Launches the activity.
     */
    fun launch(configuration: PODynamicCheckoutConfiguration) {
        launcher.launch(configuration, activityOptions)
    }
}
