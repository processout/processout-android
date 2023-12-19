package com.processout.sdk.ui.card.tokenization

import android.app.Activity
import android.content.Context
import androidx.fragment.app.viewModels
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.toActivityResult
import com.processout.sdk.ui.base.BaseBottomSheetDialogFragment
import com.processout.sdk.ui.card.tokenization.CardTokenizationActivityContract.Companion.EXTRA_CONFIGURATION
import com.processout.sdk.ui.card.tokenization.CardTokenizationActivityContract.Companion.EXTRA_RESULT
import com.processout.sdk.ui.card.tokenization.CardTokenizationEvent.Dismiss

internal class CardTokenizationBottomSheet : BaseBottomSheetDialogFragment<POCardTokenizationResponse>() {

    companion object {
        val tag: String = CardTokenizationBottomSheet::class.java.simpleName
    }

    private var configuration: POCardTokenizationConfiguration? = null

    private val viewModel: CardTokenizationViewModel by viewModels {
        CardTokenizationViewModel.Factory(
            app = requireActivity().application,
            configuration = configuration ?: POCardTokenizationConfiguration()
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        @Suppress("DEPRECATION")
        configuration = arguments?.getParcelable(EXTRA_CONFIGURATION)
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
        result: ProcessOutActivityResult<POCardTokenizationResponse>
    ) {
        setActivityResult(
            resultCode = resultCode,
            extraName = EXTRA_RESULT,
            result = result
        )
        finish()
    }
}
