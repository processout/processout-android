package com.processout.sdk.ui.base

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.addCallback
import androidx.core.view.updateLayoutParams
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.shared.configuration.BottomSheetCancellationConfiguration

internal abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    protected val bottomSheetDialog by lazy { requireDialog() as BottomSheetDialog }
    protected val bottomSheetBehavior by lazy { bottomSheetDialog.behavior }

    protected var containerHeight: Int = ViewGroup.LayoutParams.WRAP_CONTENT
        set(value) {
            val bottomSheet: FrameLayout = requireDialog().findViewById(
                com.google.android.material.R.id.design_bottom_sheet
            )
            bottomSheet.updateLayoutParams {
                height = value
            }
            field = value
        }

    private var cancellationConfiguration = BottomSheetCancellationConfiguration()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyCancellationConfiguration(cancellationConfiguration)
        dispatchBackPressed()
    }

    protected fun allowExpandToFullScreen() {
        containerHeight = ViewGroup.LayoutParams.MATCH_PARENT
    }

    protected fun applyCancellationConfiguration(
        configuration: BottomSheetCancellationConfiguration
    ) {
        with(bottomSheetDialog) {
            with(configuration) {
                isCancelable = dragDown
                setCanceledOnTouchOutside(touchOutside)
            }
        }
        cancellationConfiguration = configuration
    }

    private fun dispatchBackPressed() {
        bottomSheetDialog.onBackPressedDispatcher.addCallback(this) {
            if (cancellationConfiguration.backPressed) {
                onCancellation(
                    ProcessOutResult.Failure(
                        code = POFailure.Code.Cancelled,
                        message = "Cancelled by user with back press or gesture."
                    )
                )
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        onCancellation(
            ProcessOutResult.Failure(
                code = POFailure.Code.Cancelled,
                message = "Cancelled by user with swipe or outside touch."
            )
        )
    }

    protected abstract fun onCancellation(failure: ProcessOutResult.Failure)
}
