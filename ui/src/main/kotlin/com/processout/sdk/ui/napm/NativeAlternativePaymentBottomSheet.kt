package com.processout.sdk.ui.napm

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
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.processout.sdk.core.*
import com.processout.sdk.ui.base.BaseBottomSheetDialogFragment
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.napm.NativeAlternativePaymentActivityContract.Companion.EXTRA_CONFIGURATION
import com.processout.sdk.ui.napm.NativeAlternativePaymentActivityContract.Companion.EXTRA_RESULT
import com.processout.sdk.ui.napm.NativeAlternativePaymentCompletion.Failure
import com.processout.sdk.ui.napm.NativeAlternativePaymentCompletion.Success
import com.processout.sdk.ui.napm.NativeAlternativePaymentEvent.Dismiss
import com.processout.sdk.ui.napm.NativeAlternativePaymentViewModelState.Capture
import com.processout.sdk.ui.napm.PONativeAlternativePaymentConfiguration.Options
import com.processout.sdk.ui.shared.component.isImeVisibleAsState
import com.processout.sdk.ui.shared.component.screenModeAsState
import com.processout.sdk.ui.shared.configuration.POCancellationConfiguration
import com.processout.sdk.ui.shared.extension.dpToPx
import kotlin.math.roundToInt

internal class NativeAlternativePaymentBottomSheet : BaseBottomSheetDialogFragment<POUnit>() {

    companion object {
        val tag: String = NativeAlternativePaymentBottomSheet::class.java.simpleName
    }

    override val expandable = true
    override val defaultViewHeight by lazy { 440.dpToPx(requireContext()) }
    private val maxPeekHeight by lazy { (screenHeight * 0.75).roundToInt() }

    private var configuration: PONativeAlternativePaymentConfiguration? = null

    private val viewModel: NativeAlternativePaymentViewModel by viewModels {
        NativeAlternativePaymentViewModel.Factory(
            app = requireActivity().application,
            invoiceId = configuration?.invoiceId ?: String(),
            gatewayConfigurationId = configuration?.gatewayConfigurationId ?: String(),
            options = configuration?.options ?: Options()
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        @Suppress("DEPRECATION")
        configuration = arguments?.getParcelable(EXTRA_CONFIGURATION)
        configuration?.run {
            if (invoiceId.isBlank() || gatewayConfigurationId.isBlank()) {
                dismiss(
                    ProcessOutResult.Failure(
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
                with(viewModel.completion.collectAsStateWithLifecycle()) {
                    LaunchedEffect(value) { handle(value) }
                }

                val state by viewModel.state.collectAsStateWithLifecycle()
                val isImeVisible by isImeVisibleAsState()
                var viewHeight by remember { mutableIntStateOf(defaultViewHeight) }
                if (state is Capture && !isImeVisible) {
                    viewHeight = maxPeekHeight
                }
                with(screenModeAsState(viewHeight = viewHeight)) {
                    LaunchedEffect(value) {
                        apply(
                            screenMode = value,
                            animate = state is Capture && bottomSheetBehavior.state == STATE_COLLAPSED
                        )
                    }
                }

                NativeAlternativePaymentScreen(
                    state = state,
                    onEvent = remember { viewModel::onEvent },
                    style = NativeAlternativePaymentScreen.style(custom = configuration?.style)
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configuration?.options?.cancellation?.let {
            apply(
                POCancellationConfiguration(
                    backPressed = it.backPressed,
                    dragDown = it.dragDown,
                    touchOutside = it.touchOutside
                )
            )
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
        val isCaptured = when (val state = viewModel.state.value) {
            is Capture -> state.isCaptured
            else -> false
        }
        if (isCaptured) {
            finishWithActivityResult(
                resultCode = Activity.RESULT_OK,
                result = ProcessOutActivityResult.Success(POUnit)
            )
        } else {
            finishWithActivityResult(
                resultCode = Activity.RESULT_CANCELED,
                result = failure.toActivityResult()
            )
        }
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
