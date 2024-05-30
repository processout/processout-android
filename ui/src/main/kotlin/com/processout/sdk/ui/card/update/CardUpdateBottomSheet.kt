package com.processout.sdk.ui.card.update

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.toActivityResult
import com.processout.sdk.ui.base.BaseBottomSheetDialogFragment
import com.processout.sdk.ui.card.update.CardUpdateCompletion.Failure
import com.processout.sdk.ui.card.update.CardUpdateCompletion.Success
import com.processout.sdk.ui.card.update.CardUpdateEvent.Dismiss
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.shared.component.screenModeAsState
import com.processout.sdk.ui.shared.extension.dpToPx

internal class CardUpdateBottomSheet : BaseBottomSheetDialogFragment<POCard>() {

    companion object {
        val tag: String = CardUpdateBottomSheet::class.java.simpleName
    }

    override val expandable = false
    override val defaultViewHeight by lazy { 420.dpToPx(requireContext()) }

    private var configuration: POCardUpdateConfiguration? = null

    private val viewModel: CardUpdateViewModel by viewModels {
        CardUpdateViewModel.Factory(
            app = requireActivity().application,
            cardId = configuration?.cardId ?: String(),
            options = configuration?.options ?: POCardUpdateConfiguration.Options()
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        @Suppress("DEPRECATION")
        configuration = arguments?.getParcelable(CardUpdateActivityContract.EXTRA_CONFIGURATION)
        configuration?.run {
            if (cardId.isBlank()) {
                dismiss(
                    ProcessOutResult.Failure(
                        code = POFailure.Code.Generic(),
                        message = "Card ID is blank."
                    )
                )
            }
        }
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

                CardUpdateScreen(
                    state = viewModel.state.collectAsStateWithLifecycle().value,
                    onEvent = remember { viewModel::onEvent },
                    style = CardUpdateScreen.style(custom = configuration?.style)
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configuration?.let { apply(it.options.cancellation) }
    }

    private fun handle(completion: CardUpdateCompletion) =
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
            extraName = CardUpdateActivityContract.EXTRA_RESULT,
            result = result
        )
        finish()
    }
}
