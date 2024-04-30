package com.processout.sdk.ui.napm

import android.app.Activity
import com.processout.sdk.core.POUnit
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.core.toActivityResult
import com.processout.sdk.ui.base.BaseBottomSheetDialogFragment
import com.processout.sdk.ui.shared.extension.dpToPx

internal class NativeAlternativePaymentBottomSheet : BaseBottomSheetDialogFragment<POUnit>() {

    companion object {
        val tag: String = NativeAlternativePaymentBottomSheet::class.java.simpleName
    }

    override val defaultViewHeight by lazy { 440.dpToPx(requireContext()) }
    override val expandable = true

    override fun onCancellation(failure: ProcessOutResult.Failure) = dismiss(failure)

    private fun dismiss(failure: ProcessOutResult.Failure) {
        // TODO: send event to VM
        finishWithActivityResult(
            resultCode = Activity.RESULT_CANCELED,
            result = failure.toActivityResult()
        )
    }

    private fun finishWithActivityResult(
        resultCode: Int,
        result: ProcessOutActivityResult<POUnit>
    ) {
        // TODO
        finish()
    }
}
