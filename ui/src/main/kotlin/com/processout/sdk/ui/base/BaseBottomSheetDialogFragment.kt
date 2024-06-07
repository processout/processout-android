@file:Suppress("MemberVisibilityCanBePrivate")

package com.processout.sdk.ui.base

import android.animation.ValueAnimator
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
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.processout.sdk.core.POFailure
import com.processout.sdk.core.ProcessOutActivityResult
import com.processout.sdk.core.ProcessOutResult
import com.processout.sdk.ui.shared.component.ScreenMode
import com.processout.sdk.ui.shared.component.ScreenMode.Fullscreen
import com.processout.sdk.ui.shared.component.ScreenMode.Window
import com.processout.sdk.ui.shared.configuration.POCancellationConfiguration
import com.processout.sdk.ui.shared.extension.screenSize

internal abstract class BaseBottomSheetDialogFragment<T : Parcelable> : BottomSheetDialogFragment() {

    protected abstract val expandable: Boolean
    protected abstract val defaultViewHeight: Int
    protected val screenHeight by lazy { requireContext().screenSize().height }
    protected var animationDurationMillis: Long = 400

    private val bottomSheetDialog by lazy { requireDialog() as BottomSheetDialog }
    private val bottomSheetBehavior by lazy { bottomSheetDialog.behavior }

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

    private var peekHeightValueAnimator: ValueAnimator? = null
    private var viewHeightValueAnimator: ValueAnimator? = null

    private var screenMode: ScreenMode? = null
    private var cancellationConfiguration = POCancellationConfiguration()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apply(Window(height = defaultViewHeight, availableHeight = screenHeight))
        apply(cancellationConfiguration)
        dispatchBackPressed()
    }

    protected fun apply(screenMode: ScreenMode, animate: Boolean = false) {
        when (screenMode) {
            is Window -> setHeight(
                peekHeight = screenMode.height,
                viewHeight = if (expandable && bottomSheetBehavior.state == STATE_EXPANDED)
                    screenMode.availableHeight else screenMode.height,
                expandable = expandable,
                animate = this.screenMode is Window &&
                        bottomSheetBehavior.state == STATE_COLLAPSED &&
                        animate
            )
            is Fullscreen -> setHeight(
                peekHeight = screenMode.screenHeight,
                viewHeight = ViewGroup.LayoutParams.WRAP_CONTENT,
                expandable = true,
                animate = false
            )
        }
        this.screenMode = screenMode
    }

    private fun setHeight(
        peekHeight: Int,
        viewHeight: Int,
        expandable: Boolean,
        animate: Boolean
    ) {
        if (animate) {
            peekHeightValueAnimator = ValueAnimator.ofInt(bottomSheetBehavior.peekHeight, peekHeight)
                .apply {
                    addUpdateListener {
                        val animatedValue = it.animatedValue as Int
                        containerHeight = if (expandable) ViewGroup.LayoutParams.MATCH_PARENT else animatedValue
                        bottomSheetBehavior.peekHeight = animatedValue
                    }
                    duration = animationDurationMillis
                    start()
                }
            view?.updateLayoutParams {
                viewHeightValueAnimator = ValueAnimator.ofInt(height, viewHeight)
                    .apply {
                        addUpdateListener {
                            height = it.animatedValue as Int
                        }
                        duration = animationDurationMillis
                        start()
                    }
            }
        } else {
            containerHeight = if (expandable) ViewGroup.LayoutParams.MATCH_PARENT else peekHeight
            bottomSheetBehavior.peekHeight = peekHeight
            view?.updateLayoutParams {
                height = viewHeight
            }
        }
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (expandable && screenMode is Window) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        cancelAnimations()
    }

    private fun cancelAnimations() {
        listOf(peekHeightValueAnimator, viewHeightValueAnimator).forEach {
            it?.removeAllListeners()
            it?.removeAllUpdateListeners()
            it?.cancel()
        }
    }
}
