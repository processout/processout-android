package com.processout.sdk.ui.shared.view.input.code

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatEditText
import com.processout.sdk.R
import com.processout.sdk.ui.shared.view.extensions.outline
import com.processout.sdk.ui.shared.view.input.Input
import kotlin.math.roundToInt

internal class CodeEditText(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatEditText(
    ContextThemeWrapper(context, R.style.Theme_ProcessOut_Default_Input),
    attrs,
    R.attr.poCodeEditTextStyle
), Input {

    private val defaultBackground = outline(context, R.color.poBorderPrimary)
    private val errorBackground = outline(context, R.color.poBorderError)

    init {
        initLayoutParams()
        inputType = InputType.TYPE_CLASS_NUMBER
        imeOptions = EditorInfo.IME_ACTION_DONE
        setState(Input.State.Default)
    }

    override fun setState(state: Input.State) {
        background = when (state) {
            Input.State.Default -> defaultBackground
            is Input.State.Error -> errorBackground
        }
    }

    private fun initLayoutParams() {
        with(resources) {
            val params = ViewGroup.MarginLayoutParams(
                getDimensionPixelSize(R.dimen.po_codeEditText_width),
                getDimensionPixelSize(R.dimen.po_codeEditText_height)
            )
            val horizontalMargin = (getDimension(R.dimen.po_codeEditText_space) / 2).roundToInt()
            params.setMargins(horizontalMargin, 0, horizontalMargin, 0)
            layoutParams = params
        }
    }
}
