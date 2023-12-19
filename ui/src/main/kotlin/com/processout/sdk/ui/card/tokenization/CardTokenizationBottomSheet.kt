package com.processout.sdk.ui.card.tokenization

import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.base.BaseBottomSheetDialogFragment

internal class CardTokenizationBottomSheet : BaseBottomSheetDialogFragment<POCardTokenizationResponse>() {

    companion object {
        val tag: String = CardTokenizationBottomSheet::class.java.simpleName
    }

    override fun onCancellation(failure: ProcessOutResult.Failure) {
        // TODO
        finish()
    }
}
