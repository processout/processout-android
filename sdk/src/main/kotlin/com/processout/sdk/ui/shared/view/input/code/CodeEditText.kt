package com.processout.sdk.ui.shared.view.input.code

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.InputType
import android.util.AttributeSet
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.processout.sdk.R
import com.processout.sdk.ui.nativeapm.applyControlsTintColor
import com.processout.sdk.ui.nativeapm.applyStyle
import com.processout.sdk.ui.shared.style.input.POInputStyle
import com.processout.sdk.ui.shared.view.extensions.defaultOutlineBackground
import com.processout.sdk.ui.shared.view.extensions.outlineBackground
import com.processout.sdk.ui.shared.view.input.Input
import kotlin.math.roundToInt

internal class CodeEditText(
    context: Context,
    attrs: AttributeSet? = null,
    override val style: POInputStyle? = null
) : AppCompatEditText(
    ContextThemeWrapper(context, R.style.Theme_ProcessOut_Default_Input),
    attrs,
    R.attr.poCodeEditTextStyle
), Input, ActionMode.Callback {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, null)

    private var defaultBackground = defaultOutlineBackground(context, R.color.poBorderPrimary)
    private var errorBackground = defaultOutlineBackground(context, R.color.poBorderError)

    @ColorInt
    private var defaultControlsTintColor = ContextCompat.getColor(context, R.color.poTextPrimary)

    @ColorInt
    private var errorControlsTintColor = ContextCompat.getColor(context, R.color.poTextError)

    init {
        id = View.generateViewId()
        initLayoutParams()
        disableActionMode()
        inputType = InputType.TYPE_CLASS_NUMBER or
                InputType.TYPE_NUMBER_VARIATION_NORMAL or
                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        imeOptions = EditorInfo.IME_ACTION_DONE
        isLongClickable = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO
        }

        highlightColor = Color.TRANSPARENT
        style?.normal?.field?.let {
            defaultBackground = outlineBackground(context, it)
            defaultControlsTintColor = it.controlsTintColor
        }
        style?.error?.field?.let {
            errorBackground = outlineBackground(context, it)
            errorControlsTintColor = it.controlsTintColor
        }

        setState(Input.State.Default())
    }

    override fun setState(state: Input.State) {
        when (state) {
            is Input.State.Default -> {
                isEnabled = state.editable
                background = defaultBackground
                applyControlsTintColor(defaultControlsTintColor)
                style?.normal?.field?.text?.let { applyStyle(it) }
            }
            is Input.State.Error -> {
                isEnabled = true
                background = errorBackground
                applyControlsTintColor(errorControlsTintColor)
                style?.error?.field?.text?.let { applyStyle(it) }
            }
        }
    }

    private fun initLayoutParams() {
        with(resources) {
            val params = LinearLayout.LayoutParams(
                getDimensionPixelSize(R.dimen.po_codeEditText_width),
                getDimensionPixelSize(R.dimen.po_codeEditText_height)
            )
            val horizontalMargin = (getDimension(R.dimen.po_codeEditText_space) / 2).roundToInt()
            params.setMargins(horizontalMargin, 0, horizontalMargin, 0)
            layoutParams = params
        }
    }

    private fun disableActionMode() {
        customSelectionActionModeCallback = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            customInsertionActionModeCallback = this
        }
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        menu.clear()
        menu.close()
        mode.finish()
        return false
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false
    override fun onActionItemClicked(mode: ActionMode, item: MenuItem) = false
    override fun onDestroyActionMode(mode: ActionMode) {}
    override fun isSuggestionsEnabled() = false
}
