package com.processout.sdk.ui.napm

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
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.toActivityResult
import com.processout.sdk.ui.base.BaseBottomSheetDialogFragment
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.napm.NativeAlternativePaymentActivityContract.Companion.EXTRA_CONFIGURATION
import com.processout.sdk.ui.napm.NativeAlternativePaymentActivityContract.Companion.EXTRA_RESULT
import com.processout.sdk.ui.napm.NativeAlternativePaymentCompletion.Failure
import com.processout.sdk.ui.napm.NativeAlternativePaymentCompletion.Success
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent.Dismiss
import com.processout.sdk.ui.shared.composable.screenModeAsState
import com.processout.sdk.ui.shared.extension.dpToPx

internal class NativeAlternativePaymentBottomSheet : BaseBottomSheetDialogFragment<POUnit>() {

    companion object {
        val tag: String = NativeAlternativePaymentBottomSheet::class.java.simpleName
    }

    override val defaultViewHeight by lazy { 440.dpToPx(requireContext()) }
    override val expandable = true

    private var configuration: PONativeAlternativePaymentConfiguration? = null

    private val viewModel: NativeAlternativePaymentViewModel by viewModels {
        NativeAlternativePaymentViewModel.Factory(
            app = requireActivity().application,
            configuration = configuration ?: PONativeAlternativePaymentConfiguration(
                gatewayConfigurationId = String(),
                invoiceId = String()
            )
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

                NativeAlternativePaymentScreen(
                    state = viewModel.state.collectAsStateWithLifecycle().value,
                    onEvent = remember { viewModel::onEvent },
                    style = NativeAlternativePaymentScreen.style(custom = configuration?.style)
                )
            }
        }
    }

    private fun handle(completion: NativeAlternativePaymentCompletion) =
        when (completion) {
            Success -> finishWithActivityResult(
                resultCode = Activity.RESULT_OK,
                result = ProcessOutActivityResult.Success(POUnit)
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
        result: ProcessOutActivityResult<POUnit>
    ) {
        setActivityResult(
            resultCode = resultCode,
            extraName = EXTRA_RESULT,
            result = result
        )
        finish()
    }
}
