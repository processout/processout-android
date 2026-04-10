package com.processout.sdk.ui.napm

import android.app.Application
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.processout.sdk.R
import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.response.POAlternativePaymentMethodResponse
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.logger.POLogger
import com.processout.sdk.core.toActivityResult
import com.processout.sdk.ui.apm.POAlternativePaymentMethodCustomTabLauncher
import com.processout.sdk.ui.napm.NativeAlternativePaymentCompletion.Failure
import com.processout.sdk.ui.napm.NativeAlternativePaymentCompletion.Success
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent.WebRedirectResult
import com.processout.sdk.ui.napm.NativeAlternativePaymentSideEffect.WebRedirect
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.Flow.Authorization
import com.processout.sdk.ui.napm.delegate.v2.NativeAlternativePaymentDefaultValuesRequest
import com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentDelegate
import com.processout.sdk.ui.napm.delegate.v2.PONativeAlternativePaymentEvent
import com.processout.sdk.ui.napm.delegate.v2.toResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Launcher that starts [NativeAlternativePaymentActivity] and provides the result.
 */
class PONativeAlternativePaymentLauncher private constructor(
    private val viewModel: NativeAlternativePaymentViewModel,
    private val launcher: ActivityResultLauncher<PONativeAlternativePaymentConfiguration>,
    private val activityOptions: ActivityOptionsCompat,
    private val delegate: PONativeAlternativePaymentDelegate,
    private val callback: (ProcessOutActivityResult<POUnit>) -> Unit,
    private val eventDispatcher: POEventDispatcher = POEventDispatcher.instance
) {

    private lateinit var customTabLauncher: POAlternativePaymentMethodCustomTabLauncher

    companion object {
        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: Fragment,
            delegate: PONativeAlternativePaymentDelegate,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ): PONativeAlternativePaymentLauncher {
            val viewModel: NativeAlternativePaymentViewModel by from.viewModels {
                createViewModelFactory(app = from.requireActivity().application)
            }
            return PONativeAlternativePaymentLauncher(
                viewModel = viewModel,
                launcher = from.registerForActivityResult(
                    NativeAlternativePaymentActivityContract(),
                    callback
                ),
                activityOptions = createActivityOptions(context = from.requireContext()),
                delegate = delegate,
                callback = callback
            ).apply {
                customTabLauncher = POAlternativePaymentMethodCustomTabLauncher.create(
                    from = from,
                    callback = ::handleWebRedirect
                )
                from.viewLifecycleOwnerLiveData.observe(from) { lifecycleOwner ->
                    collectViewModelState(lifecycleOwner)
                    dispatchAllEvents(coroutineScope = lifecycleOwner.lifecycleScope)
                }
            }
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
        ): PONativeAlternativePaymentLauncher {
            val viewModel: NativeAlternativePaymentViewModel by from.viewModels {
                createViewModelFactory(app = from.requireActivity().application)
            }
            return PONativeAlternativePaymentLauncher(
                viewModel = viewModel,
                launcher = from.registerForActivityResult(
                    NativeAlternativePaymentActivityContract(),
                    callback
                ),
                activityOptions = createActivityOptions(context = from.requireContext()),
                delegate = object : PONativeAlternativePaymentDelegate {},
                callback = callback
            ).apply {
                customTabLauncher = POAlternativePaymentMethodCustomTabLauncher.create(
                    from = from,
                    callback = ::handleWebRedirect
                )
                from.viewLifecycleOwnerLiveData.observe(from) { lifecycleOwner ->
                    collectViewModelState(lifecycleOwner)
                    dispatchAllEvents(coroutineScope = lifecycleOwner.lifecycleScope)
                }
            }
        }

        /**
         * Creates the launcher from Fragment.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        @Deprecated(message = "Use alternative function.")
        fun create(
            from: Fragment,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ): PONativeAlternativePaymentLauncher {
            val viewModel: NativeAlternativePaymentViewModel by from.viewModels {
                createViewModelFactory(app = from.requireActivity().application)
            }
            return PONativeAlternativePaymentLauncher(
                viewModel = viewModel,
                launcher = from.registerForActivityResult(
                    NativeAlternativePaymentActivityContract(),
                    callback
                ),
                activityOptions = createActivityOptions(context = from.requireContext()),
                delegate = object : PONativeAlternativePaymentDelegate {},
                callback = callback
            ).apply {
                customTabLauncher = POAlternativePaymentMethodCustomTabLauncher.create(
                    from = from,
                    callback = ::handleWebRedirect
                )
                from.viewLifecycleOwnerLiveData.observe(from) { lifecycleOwner ->
                    collectViewModelState(lifecycleOwner)
                    dispatchAllEvents(coroutineScope = lifecycleOwner.lifecycleScope)
                }
            }
        }

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        fun create(
            from: ComponentActivity,
            delegate: PONativeAlternativePaymentDelegate,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ): PONativeAlternativePaymentLauncher {
            val viewModel: NativeAlternativePaymentViewModel by from.viewModels {
                createViewModelFactory(app = from.application)
            }
            return PONativeAlternativePaymentLauncher(
                viewModel = viewModel,
                launcher = from.registerForActivityResult(
                    NativeAlternativePaymentActivityContract(),
                    from.activityResultRegistry,
                    callback
                ),
                activityOptions = createActivityOptions(context = from),
                delegate = delegate,
                callback = callback
            ).apply {
                customTabLauncher = POAlternativePaymentMethodCustomTabLauncher.create(
                    from = from,
                    callback = ::handleWebRedirect
                )
                collectViewModelState(lifecycleOwner = from)
                dispatchAllEvents(coroutineScope = from.lifecycleScope)
            }
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
        ): PONativeAlternativePaymentLauncher {
            val viewModel: NativeAlternativePaymentViewModel by from.viewModels {
                createViewModelFactory(app = from.application)
            }
            return PONativeAlternativePaymentLauncher(
                viewModel = viewModel,
                launcher = from.registerForActivityResult(
                    NativeAlternativePaymentActivityContract(),
                    from.activityResultRegistry,
                    callback
                ),
                activityOptions = createActivityOptions(context = from),
                delegate = object : PONativeAlternativePaymentDelegate {},
                callback = callback
            ).apply {
                customTabLauncher = POAlternativePaymentMethodCustomTabLauncher.create(
                    from = from,
                    callback = ::handleWebRedirect
                )
                collectViewModelState(lifecycleOwner = from)
                dispatchAllEvents(coroutineScope = from.lifecycleScope)
            }
        }

        /**
         * Creates the launcher from Activity.
         * __Note:__ Required to call in _onCreate()_ to register for activity result.
         */
        @Deprecated(message = "Use alternative function.")
        fun create(
            from: ComponentActivity,
            callback: (ProcessOutActivityResult<POUnit>) -> Unit
        ): PONativeAlternativePaymentLauncher {
            val viewModel: NativeAlternativePaymentViewModel by from.viewModels {
                createViewModelFactory(app = from.application)
            }
            return PONativeAlternativePaymentLauncher(
                viewModel = viewModel,
                launcher = from.registerForActivityResult(
                    NativeAlternativePaymentActivityContract(),
                    from.activityResultRegistry,
                    callback
                ),
                activityOptions = createActivityOptions(context = from),
                delegate = object : PONativeAlternativePaymentDelegate {},
                callback = callback
            ).apply {
                customTabLauncher = POAlternativePaymentMethodCustomTabLauncher.create(
                    from = from,
                    callback = ::handleWebRedirect
                )
                collectViewModelState(lifecycleOwner = from)
                dispatchAllEvents(coroutineScope = from.lifecycleScope)
            }
        }

        private fun createViewModelFactory(app: Application) =
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

        private fun createActivityOptions(context: Context) =
            ActivityOptionsCompat.makeCustomAnimation(
                context, R.anim.po_slide_in_vertical, 0
            )
    }

    private fun collectViewModelState(lifecycleOwner: LifecycleOwner) {
        collectCompletion(lifecycleOwner)
        collectSideEffects(lifecycleOwner)
    }

    private fun collectCompletion(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.completion.collect { completion ->
                    when (completion) {
                        Success -> complete(result = ProcessOutResult.Success(POUnit))
                        is Failure -> complete(result = completion.failure)
                        else -> {}
                    }
                }
            }
        }
    }

    private fun collectSideEffects(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                withContext(Dispatchers.Main.immediate) {
                    viewModel.sideEffects.collect {
                        handle(sideEffect = it)
                    }
                }
            }
        }
    }

    private fun dispatchAllEvents(coroutineScope: CoroutineScope) {
        dispatchEvents(coroutineScope)
        dispatchDefaultValues(coroutineScope)
    }

    private fun dispatchEvents(coroutineScope: CoroutineScope) {
        eventDispatcher.subscribe<PONativeAlternativePaymentEvent>(
            coroutineScope
        ) { delegate.onEvent(it) }
    }

    private fun dispatchDefaultValues(coroutineScope: CoroutineScope) {
        eventDispatcher.subscribeForRequest<NativeAlternativePaymentDefaultValuesRequest>(
            coroutineScope
        ) { request ->
            coroutineScope.launch {
                val defaultValues = delegate.defaultValues(
                    gatewayConfigurationId = request.gatewayConfigurationId,
                    parameters = request.parameters
                )
                eventDispatcher.send(request.toResponse(defaultValues))
            }
        }
    }

    /**
     * Launches the payment.
     */
    fun launch(configuration: PONativeAlternativePaymentConfiguration) {
        if (configuration.redirect?.enableHeadlessMode == true) {
            POLogger.info("Starting native alternative payment in headless mode.")
            viewModel.start(configuration)
        } else {
            launcher.launch(
                input = configuration,
                options = activityOptions
            )
        }
    }

    private fun handle(sideEffect: NativeAlternativePaymentSideEffect) {
        if (sideEffect is WebRedirect) {
            customTabLauncher.launch(
                uri = sideEffect.redirectUrl.toUri(),
                returnUrl = sideEffect.returnUrl
            )
        }
    }

    private fun handleWebRedirect(result: ProcessOutResult<POAlternativePaymentMethodResponse>) {
        viewModel.onEvent(WebRedirectResult(result))
    }

    private fun complete(result: ProcessOutResult<POUnit>) {
        viewModel.reset()
        callback(result.toActivityResult())
    }
}
