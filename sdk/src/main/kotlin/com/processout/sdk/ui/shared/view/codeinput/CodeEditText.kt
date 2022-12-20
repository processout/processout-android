package com.processout.sdk.ui.shared.view.codeinput

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.InputType
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.processout.sdk.R
import com.processout.sdk.ui.shared.view.POView

internal class CodeEditText : AppCompatEditText, POView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(
        ContextThemeWrapper(context, R.style.Theme_ProcessOut_CodeInput),
        attrs, R.attr.poCodeEditTextStyle
    )

    private val defaultBackground = initBackground(R.color.poBorderPrimary)
    private val errorBackground = initBackground(R.color.poBorderError)

    init {
        initLayoutParams()
        inputType = InputType.TYPE_CLASS_NUMBER
        setState(POView.State.Default)
    }

    override fun setState(state: POView.State) {
        background = when (state) {
            POView.State.Default -> defaultBackground
            is POView.State.Error -> errorBackground
        }
    }

    private fun initLayoutParams() {
        with(resources) {
            val params = ViewGroup.MarginLayoutParams(
                getDimensionPixelSize(R.dimen.po_codeEditText_width),
                getDimensionPixelSize(R.dimen.po_codeEditText_height)
            )
            val horizontalMargin = (getDimension(R.dimen.po_codeEditText_space) / 2).toInt()
            params.setMargins(horizontalMargin, 0, horizontalMargin, 0)
            layoutParams = params
        }
    }

    private fun initBackground(
        @ColorRes borderColorRes: Int
    ): Drawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = resources.getDimensionPixelSize(R.dimen.po_cornerRadius).toFloat()
        setStroke(
            resources.getDimensionPixelOffset(R.dimen.po_borderWidth),
            ContextCompat.getColor(context, borderColorRes)
        )
        setColor(Color.TRANSPARENT)
    }
}
