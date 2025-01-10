package com.processout.sdk.ui.savedpaymentmethods

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
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.core.POFailure.Code.Generic
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.toActivityResult
import com.processout.sdk.ui.base.BaseBottomSheetDialogFragment
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsActivityContract.Companion.EXTRA_CONFIGURATION
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsActivityContract.Companion.EXTRA_RESULT
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsCompletion.Failure
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsCompletion.Success
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsEvent.Dismiss
import kotlin.math.roundToInt

internal class SavedPaymentMethodsBottomSheet : BaseBottomSheetDialogFragment<POUnit>() {

    companion object {
        val tag: String = SavedPaymentMethodsBottomSheet::class.java.simpleName
    }

    override val expandable = false
    override val defaultViewHeight by lazy { (screenHeight * 0.8).roundToInt() }

    private var configuration: POSavedPaymentMethodsConfiguration? = null

    private val viewModel: SavedPaymentMethodsViewModel by viewModels {
        SavedPaymentMethodsViewModel.Factory(
            app = requireActivity().application,
            configuration = configuration ?: POSavedPaymentMethodsConfiguration(
                invoiceRequest = POInvoiceRequest(invoiceId = String())
            )
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        @Suppress("DEPRECATION")
        configuration = arguments?.getParcelable(EXTRA_CONFIGURATION)
        configuration?.run {
            if (invoiceRequest.invoiceId.isBlank()) {
                dismiss(
                    ProcessOutResult.Failure(
                        code = Generic(),
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

                POText(text = Companion.tag)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configuration?.let { apply(it.cancellation) }
    }

    private fun handle(completion: SavedPaymentMethodsCompletion) =
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
