package com.processout.sdk.ui.base

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

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

    protected fun allowExpandToFullScreen() {
        containerHeight = ViewGroup.LayoutParams.MATCH_PARENT
    }
}
