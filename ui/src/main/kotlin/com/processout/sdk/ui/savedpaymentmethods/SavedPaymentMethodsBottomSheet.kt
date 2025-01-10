package com.processout.sdk.ui.savedpaymentmethods

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.base.BaseBottomSheetDialogFragment
import com.processout.sdk.ui.core.component.POText
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import com.processout.sdk.ui.savedpaymentmethods.SavedPaymentMethodsActivityContract.Companion.EXTRA_CONFIGURATION
import kotlin.math.roundToInt

internal class SavedPaymentMethodsBottomSheet : BaseBottomSheetDialogFragment<POUnit>() {

    companion object {
        val tag: String = SavedPaymentMethodsBottomSheet::class.java.simpleName
    }

    override val expandable = false
    override val defaultViewHeight by lazy { (screenHeight * 0.8).roundToInt() }

    private var configuration: POSavedPaymentMethodsConfiguration? = null

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
