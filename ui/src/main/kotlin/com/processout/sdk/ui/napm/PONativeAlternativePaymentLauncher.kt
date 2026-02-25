package com.processout.sdk.ui.napm

import android.app.Application
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.processout.sdk.R
import com.processout.sdk.api.ProcessOut
import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentAuthorizationRequest
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentRedirectConfirmation
import com.processout.sdk.api.model.request.napm.v2.PONativeAlternativePaymentTokenizationRequest
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentRedirect
import com.processout.sdk.api.model.response.napm.v2.PONativeAlternativePaymentRedirect.RedirectType
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
import com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentEvent.DidFail
import com.processout.sdk.ui.napm.delegate.v2.toResponse
import com.processout.sdk.ui.shared.extension.openDeepLink
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Launcher that starts [NativeAlternativePaymentActivity] and provides the result.
 */
class PONativeAlternativePaymentLauncher private constructor(
    private val hostActivity: ComponentActivity,
    private val app: Application,
    private val scope: CoroutineScope,
    private val launcher: ActivityResultLauncher<PONativeAlternativePaymentConfiguration>,
    private val activityOptions: ActivityOptionsCompat,
    private val delegate: PONativeAlternativePaymentDelegate,
    private val callback: (ProcessOutActivityResult<POUnit>) -> Unit,
    private val eventDispatcher: POEventDispatcher = POEventDispatcher.instance,
    private val invoicesService: POInvoicesService = ProcessOut.instance.invoices,
    private val customerTokensService: POCustomerTokensService = ProcessOut.instance.customerTokens
) {

    private val viewModel: NativeAlternativePaymentViewModel by hostActivity.viewModels {
        NativeAlternativePaymentViewModel.Factory(
            app = app,
            configuration = PONativeAlternativePaymentConfiguration(
                flow = Authorization(
                    invoiceId = String(),
                    gatewayConfigurationId = String()
                ),
                header = null
            )
        )
    }

    private lateinit var customTabLauncher: POAlternativePaymentMethodCustomTabLauncher

    private object LocalCache {
        var configuration: PONativeAlternativePaymentConfiguration? = null
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
            hostActivity = from.requireActivity(),
            app = from.requireActivity().application,
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
                callback = ::handleWebRedirect
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
            hostActivity = from.requireActivity(),
            app = from.requireActivity().application,
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
                callback = ::handleWebRedirect
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
            hostActivity = from.requireActivity(),
            app = from.requireActivity().application,
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
                callback = ::handleWebRedirect
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
            hostActivity = from,
            app = from.application,
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
                callback = ::handleWebRedirect
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
            hostActivity = from,
            app = from.application,
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
                callback = ::handleWebRedirect
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
            hostActivity = from,
            app = from.application,
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
                callback = ::handleWebRedirect
            )
        }

        private fun createActivityOptions(context: Context) =
            ActivityOptionsCompat.makeCustomAnimation(
                context, R.anim.po_slide_in_vertical, 0
            )
    }

    init {
        collectViewModelCompletion()
        dispatchEvents()
        dispatchDefaultValues()
    }

    private fun collectViewModelCompletion() {
        hostActivity.lifecycleScope.launch {
            hostActivity.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.completion.collect { completion ->
                    when (completion) {
                        NativeAlternativePaymentCompletion.Success ->
                            completeHeadlessMode(result = ProcessOutResult.Success(value = POUnit))
                        is NativeAlternativePaymentCompletion.Failure ->
                            completeHeadlessMode(result = completion.failure)
                        else -> {}
                    }
                }
            }
        }
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
        LocalCache.configuration = configuration
        continuePayment(configuration)
    }

    private fun continuePayment(
        configuration: PONativeAlternativePaymentConfiguration,
        redirectConfirmation: PONativeAlternativePaymentRedirectConfirmation? = null
    ) {
        scope.launch {
            when (val flow = configuration.flow) {
                is Authorization -> authorize(flow, configuration, redirectConfirmation)
                is Tokenization -> tokenize(flow, configuration, redirectConfirmation)
            }
        }
    }

    private suspend fun authorize(
        flow: Authorization,
        configuration: PONativeAlternativePaymentConfiguration,
        redirectConfirmation: PONativeAlternativePaymentRedirectConfirmation? = null
    ) {
        val request = PONativeAlternativePaymentAuthorizationRequest(
            invoiceId = flow.invoiceId,
            gatewayConfigurationId = flow.gatewayConfigurationId,
            source = flow.customerTokenId,
            redirectConfirmation = redirectConfirmation
        )
        invoicesService.authorize(request)
            .onSuccess { response ->
                val updatedConfiguration = configuration.copy(
                    flow = flow.copy(initialResponse = response)
                )
                LocalCache.configuration = updatedConfiguration
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
        configuration: PONativeAlternativePaymentConfiguration,
        redirectConfirmation: PONativeAlternativePaymentRedirectConfirmation? = null
    ) {
        val request = PONativeAlternativePaymentTokenizationRequest(
            customerId = flow.customerId,
            customerTokenId = flow.customerTokenId,
            gatewayConfigurationId = flow.gatewayConfigurationId,
            redirectConfirmation = redirectConfirmation
        )
        customerTokensService.tokenize(request)
            .onSuccess { response ->
                val updatedConfiguration = configuration.copy(
                    flow = flow.copy(initialResponse = response)
                )
                LocalCache.configuration = updatedConfiguration
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
            PENDING -> viewModel.start(configuration)
            SUCCESS -> {
                POLogger.info("Success: payment completed.")
                completeHeadlessMode(result = ProcessOutResult.Success(value = POUnit))
            }
            UNKNOWN -> {
                val failure = ProcessOutResult.Failure(
                    code = Internal(),
                    message = "Unsupported payment state."
                )
                POLogger.error(
                    message = "%s", failure,
                    attributes = configuration.logAttributes
                )
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
        when (redirect.type) {
            RedirectType.WEB -> webRedirect(
                redirectUrl = redirect.url,
                configuration = configuration
            )
            RedirectType.DEEP_LINK -> deepLinkRedirect(
                redirectUrl = redirect.url,
                configuration = configuration
            )
            RedirectType.UNKNOWN -> {
                val failure = ProcessOutResult.Failure(
                    code = Internal(),
                    message = "Unknown redirect type: ${redirect.rawType}"
                )
                POLogger.error(
                    message = "Unexpected response: %s", failure,
                    attributes = configuration.logAttributes
                )
                completeHeadlessMode(result = failure)
            }
        }
    }

    private fun webRedirect(
        redirectUrl: String,
        configuration: PONativeAlternativePaymentConfiguration
    ) {
        val returnUrl = configuration.redirect?.returnUrl
        if (returnUrl.isNullOrBlank()) {
            val failure = ProcessOutResult.Failure(
                code = Generic(),
                message = "Return URL is missing in configuration during web redirect flow."
            )
            POLogger.warn(
                message = "Failed headless web redirect: %s", failure,
                attributes = configuration.logAttributes
            )
            completeHeadlessMode(result = failure)
            return
        }
        customTabLauncher.launch(
            uri = redirectUrl.toUri(),
            returnUrl = returnUrl
        )
    }

    private fun handleWebRedirect(result: ProcessOutResult<POAlternativePaymentMethodResponse>) {
        val configuration = LocalCache.configuration
        result.onSuccess {
            if (configuration == null) {
                val failure = ProcessOutResult.Failure(
                    code = Internal(),
                    message = "Configuration is not cached when handling web redirect result."
                )
                POLogger.error(message = "Failed headless web redirect: %s", failure)
                completeHeadlessMode(result = failure)
                return
            }
            val confirmationRequired = when (val flow = configuration.flow) {
                is Authorization -> flow.initialResponse?.redirect?.confirmationRequired
                is Tokenization -> flow.initialResponse?.redirect?.confirmationRequired
            }
            val redirectConfirmation = if (confirmationRequired == true)
                PONativeAlternativePaymentRedirectConfirmation(success = true) else null
            continuePayment(configuration, redirectConfirmation)
        }.onFailure { failure ->
            POLogger.warn(
                message = "Failed headless web redirect: %s", failure,
                attributes = configuration?.logAttributes
            )
            completeHeadlessMode(result = failure)
        }
    }

    private fun deepLinkRedirect(
        redirectUrl: String,
        configuration: PONativeAlternativePaymentConfiguration
    ) {
        val didOpenUrl = app.openDeepLink(url = redirectUrl)
        val confirmationRequired = when (val flow = configuration.flow) {
            is Authorization -> flow.initialResponse?.redirect?.confirmationRequired
            is Tokenization -> flow.initialResponse?.redirect?.confirmationRequired
        }
        val redirectConfirmation = if (confirmationRequired == true)
            PONativeAlternativePaymentRedirectConfirmation(success = didOpenUrl) else null
        continuePayment(configuration, redirectConfirmation)
    }

    private fun completeHeadlessMode(result: ProcessOutResult<POUnit>) {
        result.onFailure { failure ->
            scope.launch {
                eventDispatcher.send(
                    event = DidFail(
                        failure = failure,
                        paymentState = when (val flow = LocalCache.configuration?.flow) {
                            is Authorization -> flow.initialResponse?.state ?: UNKNOWN
                            is Tokenization -> flow.initialResponse?.state ?: UNKNOWN
                            null -> UNKNOWN
                        }
                    )
                )
            }
        }
        LocalCache.configuration = null
        viewModel.reset()
        callback(result.toActivityResult())
    }
}
