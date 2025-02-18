package com.processout.sdk.ui.card.scanner

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.base.BaseBottomSheetDialogFragment
import com.processout.sdk.ui.card.scanner.CardScannerActivityContract.Companion.EXTRA_CONFIGURATION
import com.processout.sdk.ui.card.scanner.CardScannerActivityContract.Companion.EXTRA_RESULT
import com.processout.sdk.ui.core.theme.ProcessOutTheme
import kotlin.math.roundToInt

internal class CardScannerBottomSheet : BaseBottomSheetDialogFragment<POScannedCard>() {

    companion object {
        val tag: String = CardScannerBottomSheet::class.java.simpleName
    }

    override var expandable = false
    override val defaultViewHeight by lazy { (screenHeight * 0.5).roundToInt() }

    private var configuration: POCardScannerConfiguration? = null

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
                // TODO
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configuration?.let { apply(it.cancellation) }
    }

    override fun onCancellation(failure: ProcessOutResult.Failure) {
        // TODO
    }

    private fun finishWithActivityResult(
        resultCode: Int,
        result: ProcessOutActivityResult<POScannedCard>
    ) {
        setActivityResult(
            resultCode = resultCode,
            extraName = EXTRA_RESULT,
            result = result
        )
        finish()
    }
}
