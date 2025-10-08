package com.processout.sdk.ui.napm

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentAuthorizationRequest
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentTokenizationRequest
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentRedirect
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentState
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentState.*
import com.processout.sdk.api.service.POCustomerTokensService
import com.processout.sdk.api.service.POInvoicesService
import com.processout.sdk.core.*
import com.processout.sdk.core.POFailure.Code.Generic
import com.processout.sdk.core.POFailure.Code.Internal
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.ui.apm.POAlternativePaymentMethodCustomTabLauncher
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.Flow.Authorization
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.Flow.Tokenization
import com.processout.sdk.ui.napm.delegate.v2.NativeAlternativePaymentDefaultValuesRequest
import com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentDelegate
import com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentEvent
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
    private val callback: (ProcessOutActivityResult<POUnit>) -> Unit,
    private val eventDispatcher: POEventDispatcher = POEventDispatcher.instance,
    private val invoicesService: POInvoicesService = ProcessOut.instance.invoices,
    private val customerTokensService: POCustomerTokensService = ProcessOut.instance.customerTokens
) {

    private lateinit var customTabLauncher: POAlternativePaymentMethodCustomTabLauncher

    private object ConfigurationCache {
        var value: PONativeAlternativePaymentConfiguration? = null
    }

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
            callback = callback
        ).apply {
            customTabLauncher = POAlternativePaymentMethodCustomTabLauncher.create(
                from = from,
                callback = ::handleRedirect
            )
        }

        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         *
         * @param[delegate] __Deprecated__: not used.
         */
        @Deprecated(message = "Use alternative function.")
        fun create(
            from: Fragment,
            delegate: com.processout.sdk.ui.napm.delegate.PONativeAlternativePaymentDelegate,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ) = PONativeAlternativePaymentLauncher(
            scope = from.lifecycleScope,
            launcher = from.registerForActivityResult(
                NativeAlternativePaymentActivityContract(),
                callback
            ),
            activityOptions = createActivityOptions(from.requireContext()),
            delegate = object : PONativeAlternativePaymentDelegate {},
            callback = callback
        ).apply {
            customTabLauncher = POAlternativePaymentMethodCustomTabLauncher.create(
                from = from,
                callback = ::handleRedirect
            )
        }

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
            callback = callback
        ).apply {
            customTabLauncher = POAlternativePaymentMethodCustomTabLauncher.create(
                from = from,
                callback = ::handleRedirect
            )
        }

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
            callback = callback
        ).apply {
            customTabLauncher = POAlternativePaymentMethodCustomTabLauncher.create(
                from = from,
                callback = ::handleRedirect
            )
        }

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         *
         * @param[delegate] __Deprecated__: not used.
         */
        @Deprecated(message = "Use alternative function.")
        fun create(
            from: ComponentActivity,
            delegate: com.processout.sdk.ui.napm.delegate.PONativeAlternativePaymentDelegate,
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
            callback = callback
        ).apply {
            customTabLauncher = POAlternativePaymentMethodCustomTabLauncher.create(
                from = from,
                callback = ::handleRedirect
            )
        }

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
            callback = callback
        ).apply {
            customTabLauncher = POAlternativePaymentMethodCustomTabLauncher.create(
                from = from,
                callback = ::handleRedirect
            )
        }

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
    }

    private fun dispatchDefaultValues() {
        eventDispatcher.subscribeForRequest<NativeAlternativePaymentDefaultValuesRequest>(
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
    }

    /**
     * Launches the activity.
     */
    fun launch(configuration: PONativeAlternativePaymentConfiguration) {
        if (configuration.redirect?.enableHeadlessMode == true) {
            launchHeadlessMode(configuration)
        } else {
            launchActivity(configuration)
        }
    }

    private fun launchActivity(configuration: PONativeAlternativePaymentConfiguration) {
        launcher.launch(
            input = configuration,
            options = activityOptions
        )
    }

    private fun launchHeadlessMode(configuration: PONativeAlternativePaymentConfiguration) {
        POLogger.info("Starting native alternative payment in headless mode.")
        ConfigurationCache.value = configuration
        scope.launch {
            when (val flow = configuration.flow) {
                is Authorization -> authorize(flow, configuration)
                is Tokenization -> tokenize(flow, configuration)
            }
        }
    }

    private suspend fun authorize(
        flow: Authorization,
        configuration: PONativeAlternativePaymentConfiguration
    ) {
        val request = PONativeAlternativePaymentAuthorizationRequest(
            invoiceId = flow.invoiceId,
            gatewayConfigurationId = flow.gatewayConfigurationId,
            source = flow.customerTokenId
        )
        invoicesService.authorize(request)
            .onSuccess { response ->
                val updatedConfiguration = configuration.copy(
                    flow = flow.copy(initialResponse = response)
                )
                ConfigurationCache.value = updatedConfiguration
                handlePaymentState(
                    state = response.state,
                    redirect = response.redirect,
                    configuration = updatedConfiguration
                )
            }.onFailure { failure ->
                POLogger.info("Failed to fetch authorization details: %s", failure)
                completeHeadlessMode(result = failure)
            }
    }

    private suspend fun tokenize(
        flow: Tokenization,
        configuration: PONativeAlternativePaymentConfiguration
    ) {
        val request = PONativeAlternativePaymentTokenizationRequest(
            customerId = flow.customerId,
            customerTokenId = flow.customerTokenId,
            gatewayConfigurationId = flow.gatewayConfigurationId
        )
        customerTokensService.tokenize(request)
            .onSuccess { response ->
                val updatedConfiguration = configuration.copy(
                    flow = flow.copy(initialResponse = response)
                )
                ConfigurationCache.value = updatedConfiguration
                handlePaymentState(
                    state = response.state,
                    redirect = response.redirect,
                    configuration = updatedConfiguration
                )
            }.onFailure { failure ->
                POLogger.info("Failed to fetch tokenization details: %s", failure)
                completeHeadlessMode(result = failure)
            }
    }

    private fun handlePaymentState(
        state: PONativeAlternativePaymentState,
        redirect: PONativeAlternativePaymentRedirect?,
        configuration: PONativeAlternativePaymentConfiguration
    ) {
        when (state) {
            NEXT_STEP_REQUIRED -> handleNextStep(redirect, configuration)
            PENDING -> launchActivity(configuration)
            SUCCESS ->
                if (configuration.success != null) {
                    launchActivity(configuration)
                } else {
                    POLogger.info("Success: payment completed.")
                    completeHeadlessMode(result = ProcessOutResult.Success(value = POUnit))
                }
            UNKNOWN -> {
                val failure = ProcessOutResult.Failure(
                    code = Internal(),
                    message = "Unsupported payment state."
                )
                POLogger.error(message = "%s", failure)
                completeHeadlessMode(result = failure)
            }
        }
    }

    private fun handleNextStep(
        redirect: PONativeAlternativePaymentRedirect?,
        configuration: PONativeAlternativePaymentConfiguration
    ) {
        if (redirect == null) {
            launchActivity(configuration)
            return
        }
        val returnUrl = configuration.redirect?.returnUrl
        if (returnUrl.isNullOrBlank()) {
            val failure = ProcessOutResult.Failure(
                code = Generic(),
                message = "Return URL is missing in configuration during redirect flow."
            )
            POLogger.warn(message = "Failed headless redirect: %s", failure)
            completeHeadlessMode(result = failure)
            return
        }
        customTabLauncher.launch(
            uri = redirect.url.toUri(),
            returnUrl = returnUrl
        )
    }

    private fun handleRedirect(result: ProcessOutResult<POAlternativePaymentMethodResponse>) {
        result.onSuccess {
            val configuration = ConfigurationCache.value
            if (configuration == null) {
                val failure = ProcessOutResult.Failure(
                    code = Internal(),
                    message = "Configuration is not cached when handling a redirect result."
                )
                POLogger.error(message = "Failed headless redirect: %s", failure)
                completeHeadlessMode(result = failure)
                return
            }
            scope.launch {
                when (val flow = configuration.flow) {
                    is Authorization -> authorize(flow, configuration)
                    is Tokenization -> tokenize(flow, configuration)
                }
            }
        }.onFailure { failure ->
            POLogger.warn(message = "Failed headless redirect: %s", failure)
            completeHeadlessMode(result = failure)
        }
    }

    private fun completeHeadlessMode(result: ProcessOutResult<POUnit>) {
        ConfigurationCache.value = null
        callback(result.toActivityResult())
    }
}
