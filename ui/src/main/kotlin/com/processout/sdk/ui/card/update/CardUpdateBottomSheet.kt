package com.processout.sdk.ui.card.update

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.processout.sdk.api.model.response.POCard
import com.processout.sdk.core.*
import com.processout.sdk.ui.base.BaseBottomSheetDialogFragment
import com.processout.sdk.ui.card.update.CardUpdateCompletion.Failure
import com.processout.sdk.ui.card.update.CardUpdateCompletion.Success
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.shared.extension.dpToPx

internal class CardUpdateBottomSheet : BaseBottomSheetDialogFragment<POCard>() {

    companion object {
        val tag: String = CardUpdateBottomSheet::class.java.simpleName
        private const val DEFAULT_HEIGHT_DP = 420
    }

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
                finishWithActivityResult(
                    resultCode = Activity.RESULT_CANCELED,
                    result = ProcessOutActivityResult.Failure(
                        code = POFailure.Code.Generic(),
                        message = "Invalid configuration."
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
                handle(viewModel.completion.collectAsStateWithLifecycle().value)
                CardUpdateScreen(
                    state = viewModel.state.collectAsStateWithLifecycle().value,
                    onEvent = viewModel::onEvent,
                    style = CardUpdateScreen.style(custom = configuration?.style)
                )
            }
        }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHeight(DEFAULT_HEIGHT_DP.dpToPx(requireContext()))
        configuration?.let { apply(it.options.cancellation) }
    }

    private fun setHeight(height: Int) {
        containerHeight = height
        bottomSheetBehavior.peekHeight = height
    }

    override fun onCancellation(failure: ProcessOutResult.Failure) =
        finishWithActivityResult(
            resultCode = Activity.RESULT_CANCELED,
            result = failure.toActivityResult()
        )

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
