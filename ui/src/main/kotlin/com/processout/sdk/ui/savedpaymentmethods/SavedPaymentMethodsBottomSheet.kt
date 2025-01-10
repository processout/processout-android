package com.processout.sdk.ui.savedpaymentmethods

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import com.processout.sdk.api.model.request.POInvoiceRequest
import com.processout.sdk.core.POFailure.Code.Generic
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.base.BaseBottomSheetDialogFragment
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsActivityContract.Companion.EXTRA_CONFIGURATION
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
                viewModel.onEvent(
                    Dismiss(
                        ProcessOutResult.Failure(
                            code = Generic(),
                            message = "Invalid configuration."
                        )
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
                POText(text = Companion.tag)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configuration?.let { apply(it.cancellation) }
    }

    override fun onCancellation(failure: ProcessOutResult.Failure) {}
}
