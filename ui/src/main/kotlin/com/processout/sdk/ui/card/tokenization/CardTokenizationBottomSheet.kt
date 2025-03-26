package com.processout.sdk.ui.card.tokenization

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.processout.sdk.api.dispatcher.PODefaultEventDispatchers
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.getOrNull
import com.processout.sdk.core.toActivityResult
import com.processout.sdk.ui.base.BaseBottomSheetDialogFragment
import com.processout.sdk.ui.card.scanner.POCardScannerLauncher
import com.processout.sdk.ui.card.tokenization.CardTokenizationActivityContract.Companion.EXTRA_CONFIGURATION
import com.processout.sdk.ui.card.tokenization.CardTokenizationActivityContract.Companion.EXTRA_RESULT
import com.processout.sdk.ui.card.tokenization.CardTokenizationCompletion.Failure
import com.processout.sdk.ui.card.tokenization.CardTokenizationCompletion.Success
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent.CardScannerResult
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent.Dismiss
import com.processout.sdk.ui.card.tokenization.CardTokenizationSideEffect.CardScanner
import com.processout.sdk.ui.card.tokenization.POCardTokenizationConfiguration.Button
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.shared.component.displayCutoutHeight
import com.processout.sdk.ui.shared.component.screenModeAsState
import com.processout.sdk.ui.shared.configuration.POBottomSheetConfiguration.Height.Fixed
import com.processout.sdk.ui.shared.configuration.POBottomSheetConfiguration.Height.WrapContent
import com.processout.sdk.ui.shared.extension.collectImmediately
import kotlin.math.roundToInt

internal class CardTokenizationBottomSheet : BaseBottomSheetDialogFragment<POCard>() {

    companion object {
        val tag: String = CardTokenizationBottomSheet::class.java.simpleName
    }

    override var expandable = false
    override val defaultViewHeight by lazy { (screenHeight * 0.3).roundToInt() }

    private lateinit var configuration: POCardTokenizationConfiguration
    private val viewHeightConfiguration by lazy { configuration.bottomSheet.height }

    private val viewModel: CardTokenizationViewModel by viewModels {
        CardTokenizationViewModel.Factory(
            app = requireActivity().application,
            configuration = configuration,
            eventDispatcher = PODefaultEventDispatchers.defaultCardTokenization
        )
    }

    private lateinit var cardScannerLauncher: POCardScannerLauncher

    override fun onAttach(context: Context) {
        super.onAttach(context)
        @Suppress("DEPRECATION")
        configuration = arguments?.getParcelable(EXTRA_CONFIGURATION)
            ?: POCardTokenizationConfiguration(submitButton = Button())
        cardScannerLauncher = POCardScannerLauncher.create(
            from = this,
            callback = { result ->
                viewModel.onEvent(CardScannerResult(result.getOrNull()))
            }
        )
        viewModel.start()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            ProcessOutTheme {
                with(viewModel.completion.collectAsStateWithLifecycle()) {
                    LaunchedEffect(value) { handle(value) }
                }
                viewModel.sideEffects.collectImmediately { handle(it) }

                var viewHeight by remember { mutableIntStateOf(defaultViewHeight) }
                with(screenModeAsState(viewHeight = viewHeight)) {
                    LaunchedEffect(value) { apply(value) }
                }
                val displayCutoutHeight = displayCutoutHeight()
                CardTokenizationScreen(
                    state = viewModel.state.collectAsStateWithLifecycle().value,
                    onEvent = remember { viewModel::onEvent },
                    onContentHeightChanged = { contentHeight ->
                        viewHeight = when (val height = viewHeightConfiguration) {
                            is Fixed -> (screenHeight * height.fraction + displayCutoutHeight).roundToInt()
                            WrapContent -> contentHeight
                        }
                    },
                    style = CardTokenizationScreen.style(custom = configuration.style)
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandable = configuration.bottomSheet.expandable
        apply(configuration.bottomSheet.cancellation)
    }

    private fun handle(sideEffect: CardTokenizationSideEffect) {
        when (sideEffect) {
            CardScanner -> configuration.cardScanner?.configuration?.let {
                cardScannerLauncher.launch(configuration = it)
            }
        }
    }

    private fun handle(completion: CardTokenizationCompletion) =
        when (completion) {
            is Success -> finishWithActivityResult(
                resultCode = Activity.RESULT_OK,
                result = ProcessOutActivityResult.Success(completion.card)
            )
            is Failure -> finishWithActivityResult(
                resultCode = Activity.RESULT_CANCELED,
                result = completion.failure.toActivityResult()
            )
            else -> {}
        }

    override fun onCancellation(failure: ProcessOutResult.Failure) = dismiss(failure)

    private fun dismiss(failure: ProcessOutResult.Failure) {
        viewModel.onEvent(Dismiss(failure))
        finishWithActivityResult(
            resultCode = Activity.RESULT_CANCELED,
            result = failure.toActivityResult()
        )
    }

    private fun finishWithActivityResult(
        resultCode: Int,
        result: ProcessOutActivityResult<POCard>
    ) {
        setActivityResult(
            resultCode = resultCode,
            extraName = EXTRA_RESULT,
            result = result
        )
        finish()
    }
}
