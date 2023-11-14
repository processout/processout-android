package com.processout.sdk.ui.card.update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.processout.sdk.ui.base.BaseBottomSheetDialogFragment
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.shared.extension.dpToPx
import java.util.UUID

internal class CardUpdateBottomSheet : BaseBottomSheetDialogFragment() {

    companion object {
        val tag: String = CardUpdateBottomSheet::class.java.simpleName
        private const val DEFAULT_HEIGHT_DP = 400
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            ProcessOutTheme {
                val viewModel: CardUpdateViewModel = viewModel(
                    factory = CardUpdateViewModel.Factory(
                        app = requireActivity().application,
                        cardId = UUID.randomUUID().toString()
                    )
                )

                when (val completionState = viewModel.completionState.collectAsStateWithLifecycle().value) {
                    is CardUpdateCompletionState.Success -> {}
                    is CardUpdateCompletionState.Failure -> requireActivity().finish()
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
}
