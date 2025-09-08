package com.processout.sdk.ui.card.tokenization.component

import android.content.Context
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.processout.sdk.api.dispatcher.POEventDispatcher
import com.processout.sdk.api.model.event.POCardTokenizationEvent
import com.processout.sdk.api.model.request.POCardTokenizationPreferredSchemeRequest
import com.processout.sdk.api.model.request.POCardTokenizationProcessingRequest
import com.processout.sdk.api.model.request.POCardTokenizationShouldContinueRequest
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.api.model.response.toResponse
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.getOrNull
import com.processout.sdk.ui.card.scanner.POCardScannerLauncher
import com.processout.sdk.ui.card.tokenization.CardTokenizationCompletion.Failure
import com.processout.sdk.ui.card.tokenization.CardTokenizationCompletion.Success
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent.Action
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent.CardScannerResult
import com.processout.sdk.ui.card.tokenization.CardTokenizationInteractorState.ActionId
import com.processout.sdk.ui.card.tokenization.CardTokenizationSideEffect.CardScanner
import com.processout.sdk.ui.card.tokenization.CardTokenizationViewModel
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration.Button
import com.processout.sdk.ui.card.tokenization.delegate.CardTokenizationEligibilityRequest
import com.processout.sdk.ui.card.tokenization.delegate.POCardTokenizationDelegate
import com.processout.sdk.ui.card.tokenization.delegate.POCardTokenizationState
import com.processout.sdk.ui.card.tokenization.delegate.toResponse
import com.processout.sdk.ui.card.tokenization.screen.CardTokenizationContent
import com.processout.sdk.ui.card.tokenization.screen.CardTokenizationScreen
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.shared.configuration.POBottomSheetConfiguration
import com.processout.sdk.ui.shared.configuration.POBottomSheetConfiguration.Height.WrapContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Component that handles card tokenization view.
 *
 * @param[view] Card tokenization view.
 */
class POCardTokenizationViewComponent private constructor(
    val view: View,
    private var configuration: POCardTokenizationConfiguration,
    private val viewModel: CardTokenizationViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val cardScannerLauncher: POCardScannerLauncher,
    private val delegate: POCardTokenizationDelegate,
    private val callback: (ProcessOutResult<POCard>) -> Unit,
    private val eventDispatcher: POEventDispatcher = POEventDispatcher
) {

    private val scope = lifecycleOwner.lifecycleScope

    companion object {
        /**
         * Creates the card tokenization view component from a Fragment.
         * __Note:__ Must be called in _onCreate()_.
         */
        fun create(
            from: Fragment,
            configuration: POCardTokenizationViewComponentConfiguration,
            delegate: POCardTokenizationDelegate,
            callback: (ProcessOutResult<POCard>) -> Unit
        ): POCardTokenizationViewComponent {
            val configuration = configuration.map()
            val viewModel: CardTokenizationViewModel by from.viewModels {
                CardTokenizationViewModel.Factory(
                    app = from.requireActivity().application,
                    configuration = configuration,
                    legacyEventDispatcher = null
                )
            }
            return POCardTokenizationViewComponent(
                view = createView(
                    context = from.requireContext(),
                    viewModel = viewModel,
                    style = configuration.style
                ),
                configuration = configuration,
                viewModel = viewModel,
                lifecycleOwner = from,
                cardScannerLauncher = POCardScannerLauncher.create(
                    from = from,
                    callback = { result ->
                        viewModel.onEvent(event = CardScannerResult(card = result.getOrNull()))
                    }
                ),
                delegate = delegate,
                callback = callback
            )
        }

        /**
         * Creates the card tokenization view component from an Activity.
         * __Note:__ Must be called in _onCreate()_.
         */
        fun create(
            from: ComponentActivity,
            configuration: POCardTokenizationViewComponentConfiguration,
            delegate: POCardTokenizationDelegate,
            callback: (ProcessOutResult<POCard>) -> Unit
        ): POCardTokenizationViewComponent {
            val configuration = configuration.map()
            val viewModel: CardTokenizationViewModel by from.viewModels {
                CardTokenizationViewModel.Factory(
                    app = from.application,
                    configuration = configuration,
                    legacyEventDispatcher = null
                )
            }
            return POCardTokenizationViewComponent(
                view = createView(
                    context = from,
                    viewModel = viewModel,
                    style = configuration.style
                ),
                configuration = configuration,
                viewModel = viewModel,
                lifecycleOwner = from,
                cardScannerLauncher = POCardScannerLauncher.create(
                    from = from,
                    callback = { result ->
                        viewModel.onEvent(event = CardScannerResult(card = result.getOrNull()))
                    }
                ),
                delegate = delegate,
                callback = callback
            )
        }

        private fun POCardTokenizationViewComponentConfiguration.map() =
            POCardTokenizationConfiguration(
                cvcRequired = cvcRequired,
                cardholderNameRequired = cardholderNameRequired,
                cardScanner = cardScanner,
                preferredScheme = preferredScheme,
                billingAddress = billingAddress,
                savingAllowed = savingAllowed,
                submitButton = Button(),
                cancelButton = cancelButton,
                bottomSheet = POBottomSheetConfiguration(
                    height = WrapContent,
                    expandable = false
                ),
                metadata = metadata,
                style = style,
                _submitButton = submitButton
            )

        private fun createView(
            context: Context,
            viewModel: CardTokenizationViewModel,
            style: POCardTokenizationConfiguration.Style?
        ): View = ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ProcessOutTheme {
                    CardTokenizationContent(
                        state = viewModel.state.collectAsStateWithLifecycle().value,
                        onEvent = remember { viewModel::onEvent },
                        style = CardTokenizationScreen.style(custom = style),
                        withActionsContainer = true
                    )
                }
            }
        }
    }

    init {
        dispatchEvents()
        dispatchState()
        dispatchTokenizedCard()
        dispatchEligibility()
        dispatchPreferredScheme()
        dispatchShouldContinue()
        collectSideEffects()
        collectCompletion()
        viewModel.start()
    }

    private fun dispatchEvents() {
        eventDispatcher.subscribe<POCardTokenizationEvent>(
            coroutineScope = scope
        ) { delegate.onEvent(it) }
    }

    private fun dispatchState() {
        eventDispatcher.subscribe<POCardTokenizationState>(
            coroutineScope = scope
        ) { delegate.onStateChanged(it) }
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

    private fun collectSideEffects() {
        scope.launch {
            lifecycleOwner.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                withContext(Dispatchers.Main.immediate) {
                    viewModel.sideEffects.collect { sideEffect ->
                        when (sideEffect) {
                            CardScanner -> configuration.cardScanner?.configuration?.let {
                                cardScannerLauncher.launch(configuration = it)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun collectCompletion() {
        scope.launch {
            lifecycleOwner.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                viewModel.completion.collect { completion ->
                    when (completion) {
                        is Success -> callback(ProcessOutResult.Success(value = completion.card))
                        is Failure -> callback(completion.failure)
                        else -> {}
                    }
                }
            }
        }
    }

    /**
     * Submits the form for card tokenization.
     */
    fun tokenize() {
        viewModel.onEvent(event = Action(id = ActionId.SUBMIT))
    }

    /**
     * Cancels the ongoing card tokenization.
     */
    fun cancel() {
        viewModel.onEvent(event = Action(id = ActionId.CANCEL))
    }

    /**
     * Restarts the card tokenization.
     *
     * @param[configuration] Optional new configuration. Pass _null_ to restart with the current configuration.
     */
    fun restart(configuration: POCardTokenizationViewComponentConfiguration? = null) {
        viewModel.reset()
        if (configuration != null) {
            val configuration = configuration.map()
            this.configuration = configuration
            viewModel.start(configuration)
        } else {
            viewModel.start()
        }
    }
}
