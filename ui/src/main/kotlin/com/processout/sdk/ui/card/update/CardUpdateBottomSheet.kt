package com.processout.sdk.ui.card.update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.base.BaseBottomSheetDialogFragment
import com.processout.sdk.ui.card.update.CardUpdateCompletionState.Failure
import com.processout.sdk.ui.card.update.CardUpdateCompletionState.Success
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.shared.extension.dpToPx
import java.util.UUID

internal class CardUpdateBottomSheet : BaseBottomSheetDialogFragment() {

    companion object {
        val tag: String = CardUpdateBottomSheet::class.java.simpleName
        private const val DEFAULT_HEIGHT_DP = 400
    }

    private val viewModel: CardUpdateViewModel by viewModels {
        CardUpdateViewModel.Factory(
            app = requireActivity().application,
            cardId = UUID.randomUUID().toString()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            ProcessOutTheme {
                when (val completionState = viewModel.completionState.collectAsStateWithLifecycle().value) {
                    is Success -> {}
                    is Failure -> requireActivity().finish()
                    else -> {}
                }

                CardUpdateScreen(
                    state = viewModel.state.collectAsStateWithLifecycle().value,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHeight(DEFAULT_HEIGHT_DP.dpToPx(requireContext()))
    }

    private fun setHeight(height: Int) {
        containerHeight = height
        bottomSheetBehavior.peekHeight = height
    }

    override fun onCancellation(failure: ProcessOutResult.Failure) {
        requireActivity().finish()
    }
}
