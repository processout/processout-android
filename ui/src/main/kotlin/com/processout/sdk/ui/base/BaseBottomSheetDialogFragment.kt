package com.processout.sdk.ui.base

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.addCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.shared.composable.ScreenMode
import com.processout.sdk.ui.shared.configuration.POCancellationConfiguration
import com.processout.sdk.ui.shared.extension.screenSize

internal abstract class BaseBottomSheetDialogFragment<T : Parcelable> : BottomSheetDialogFragment() {

    private val bottomSheetDialog by lazy { requireDialog() as BottomSheetDialog }
    private val bottomSheetBehavior by lazy { bottomSheetDialog.behavior }

    protected val screenHeight by lazy { requireContext().screenSize().height }
    private var containerHeight: Int = ViewGroup.LayoutParams.WRAP_CONTENT
        set(value) {
            val bottomSheet: FrameLayout = requireDialog().findViewById(
                com.google.android.material.R.id.design_bottom_sheet
            )
            bottomSheet.updateLayoutParams {
                height = value
            }
            field = value
        }

    private var screenMode: ScreenMode? = null
    private var cancellationConfiguration = POCancellationConfiguration()

    protected abstract val defaultViewHeight: Int
    protected abstract val expandable: Boolean

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apply(ScreenMode.Window(height = defaultViewHeight, availableHeight = screenHeight))
        apply(cancellationConfiguration)
        dispatchBackPressed()
    }

    protected fun apply(screenMode: ScreenMode) {
        when (screenMode) {
            is ScreenMode.Window -> {
                val viewHeight = if (expandable && bottomSheetBehavior.state == STATE_EXPANDED)
                    screenMode.availableHeight else screenMode.height
                setHeight(
                    peekHeight = screenMode.height,
                    viewHeight = viewHeight,
                    expandable = expandable
                )
            }
            is ScreenMode.Fullscreen -> setHeight(
                peekHeight = screenMode.screenHeight,
                viewHeight = ViewGroup.LayoutParams.WRAP_CONTENT,
                expandable = true
            )
        }
        this.screenMode = screenMode
    }

    private fun setHeight(
        peekHeight: Int,
        viewHeight: Int,
        expandable: Boolean
    ) {
        containerHeight = if (expandable) ViewGroup.LayoutParams.MATCH_PARENT else peekHeight
        bottomSheetBehavior.peekHeight = peekHeight
        view?.updateLayoutParams {
            height = viewHeight
        }
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (expandable && screenMode is ScreenMode.Window) {
                view?.run {
                    val viewCoordinateY = IntArray(2)
                        .also { getLocationOnScreen(it) }.let { it[1] }

                    val windowInsets = ViewCompat.getRootWindowInsets(bottomSheet)
                    val navigationBarHeight = windowInsets?.getInsets(
                        WindowInsetsCompat.Type.navigationBars()
                    )?.bottom ?: 0

                    var imeHeight = windowInsets?.getInsets(
                        WindowInsetsCompat.Type.ime()
                    )?.bottom ?: 0
                    if (imeHeight != 0) {
                        imeHeight -= navigationBarHeight
                    }

                    var updatedHeight = screenHeight - imeHeight - viewCoordinateY
                    if (updatedHeight < bottomSheetBehavior.peekHeight) {
                        updatedHeight = bottomSheetBehavior.peekHeight
                    }
                    updateLayoutParams {
                        height = updatedHeight
                    }
                }
            }
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {}
    }

    override fun onResume() {
        super.onResume()
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
    }

    override fun onPause() {
        super.onPause()
        bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback)
    }

    protected fun apply(configuration: POCancellationConfiguration) {
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
                        message = "Cancelled by the user with back press or gesture."
                    )
                )
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) =
        onCancellation(
            ProcessOutResult.Failure(
                code = POFailure.Code.Cancelled,
                message = "Cancelled by the user with swipe or outside touch."
            )
        )

    protected abstract fun onCancellation(failure: ProcessOutResult.Failure)

    protected fun setActivityResult(
        resultCode: Int,
        extraName: String,
        result: ProcessOutActivityResult<T>
    ) {
        if (isAdded) {
            requireActivity().setResult(
                resultCode,
                Intent().putExtra(extraName, result)
            )
        }
    }

    protected fun finish() {
        if (isAdded) {
            dismissAllowingStateLoss()
            requireActivity().finish()
        }
    }
}
