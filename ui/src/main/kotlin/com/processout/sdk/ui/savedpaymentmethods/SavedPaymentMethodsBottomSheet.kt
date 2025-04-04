package com.processout.sdk.ui.savedpaymentmethods

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
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
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsActivityContract.Companion.EXTRA_CONFIGURATION
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsActivityContract.Companion.EXTRA_RESULT
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsCompletion.Failure
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsCompletion.Success
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsEvent.Dismiss
import com.processout.sdk.ui.shared.component.displayCutoutHeight
import com.processout.sdk.ui.shared.component.screenModeAsState
import com.processout.sdk.ui.shared.configuration.POBottomSheetConfiguration.Height.Fixed
import com.processout.sdk.ui.shared.configuration.POBottomSheetConfiguration.Height.WrapContent
import com.processout.sdk.ui.shared.extension.dpToPx
import kotlin.math.roundToInt

internal class SavedPaymentMethodsBottomSheet : BaseBottomSheetDialogFragment<POUnit>() {

    companion object {
        val tag: String = SavedPaymentMethodsBottomSheet::class.java.simpleName
    }

    override var expandable = false
    override val defaultViewHeight by lazy { 330.dpToPx(requireContext()) }
    override val animationDurationMillis: Long = 300

    private var configuration: POSavedPaymentMethodsConfiguration? = null
    private val viewHeightConfiguration by lazy { configuration?.bottomSheet?.height ?: WrapContent }

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
            if (invoiceRequest.invoiceId.isBlank() || invoiceRequest.clientSecret.isNullOrBlank()) {
                dismiss(
                    ProcessOutResult.Failure(
                        code = Generic(),
                        message = "Invalid configuration: 'invoiceId' and 'clientSecret' is required."
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
            val isLightTheme = !isSystemInDarkTheme()
            ProcessOutTheme(isLightTheme = isLightTheme) {
                with(viewModel.completion.collectAsStateWithLifecycle()) {
                    LaunchedEffect(value) { handle(value) }
                }
                val displayCutoutHeight = displayCutoutHeight()
                val defaultViewHeight = defaultViewHeight + displayCutoutHeight
                var viewHeight by remember { mutableIntStateOf(defaultViewHeight) }
                with(screenModeAsState(viewHeight = viewHeight)) {
                    LaunchedEffect(value) {
                        apply(
                            screenMode = value,
                            animate = viewHeightConfiguration is WrapContent
                        )
                    }
                }
                SavedPaymentMethodsScreen(
                    state = viewModel.state.collectAsStateWithLifecycle().value,
                    onEvent = remember { viewModel::onEvent },
                    onContentHeightChanged = { contentHeight ->
                        viewHeight = when (val height = viewHeightConfiguration) {
                            is Fixed -> (screenHeight * height.fraction + displayCutoutHeight).roundToInt()
                            WrapContent -> contentHeight.coerceAtLeast(defaultViewHeight)
                        }
                    },
                    isLightTheme = isLightTheme,
                    style = SavedPaymentMethodsScreen.style(custom = configuration?.style)
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configuration?.let {
            expandable = it.bottomSheet.expandable
            apply(it.bottomSheet.cancellation)
        }
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

    fun dismiss(failure: ProcessOutResult.Failure) {
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
