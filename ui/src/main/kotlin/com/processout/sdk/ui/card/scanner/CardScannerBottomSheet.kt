package com.processout.sdk.ui.card.scanner

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.toActivityResult
import com.processout.sdk.ui.base.BaseBottomSheetDialogFragment
import com.processout.sdk.ui.card.scanner.CardScannerActivityContract.Companion.EXTRA_CONFIGURATION
import com.processout.sdk.ui.card.scanner.CardScannerActivityContract.Companion.EXTRA_RESULT
import com.processout.sdk.ui.card.scanner.CardScannerCompletion.Failure
import com.processout.sdk.ui.card.scanner.CardScannerCompletion.Success
import com.processout.sdk.ui.card.scanner.CardScannerEvent.Dismiss
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.shared.component.screenModeAsState
import kotlin.math.roundToInt

internal class CardScannerBottomSheet : BaseBottomSheetDialogFragment<POScannedCard>() {

    companion object {
        val tag: String = CardScannerBottomSheet::class.java.simpleName
    }

    override var expandable = false
    override val defaultViewHeight by lazy { (screenHeight * 0.5).roundToInt() }

    private var configuration: POCardScannerConfiguration? = null

    private val viewModel: CardScannerViewModel by viewModels {
        CardScannerViewModel.Factory(
            app = requireActivity().application,
            configuration = configuration ?: POCardScannerConfiguration()
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        @Suppress("DEPRECATION")
        configuration = arguments?.getParcelable(EXTRA_CONFIGURATION)
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
                with(screenModeAsState(viewHeight = defaultViewHeight)) {
                    LaunchedEffect(value) { apply(value) }
                }
                // TODO
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configuration?.let { apply(it.cancellation) }
    }

    private fun handle(completion: CardScannerCompletion) =
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

    fun dismiss(failure: ProcessOutResult.Failure) {
        viewModel.onEvent(Dismiss(failure))
        finishWithActivityResult(
            resultCode = Activity.RESULT_CANCELED,
            result = failure.toActivityResult()
        )
    }

    private fun finishWithActivityResult(
        resultCode: Int,
        result: ProcessOutActivityResult<POScannedCard>
    ) {
        setActivityResult(
            resultCode = resultCode,
            extraName = EXTRA_RESULT,
            result = result
        )
        finish()
    }
}
