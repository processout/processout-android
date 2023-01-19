package com.processout.sdk.ui.shared.view.button

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.button.MaterialButton
import com.processout.sdk.R
import com.processout.sdk.ui.nativeapm.applyStyle
import com.processout.sdk.ui.shared.style.button.POButtonStyle
import com.processout.sdk.ui.shared.view.extensions.buttonCircularProgressDrawable

internal class POButton(
    context: Context,
    attrs: AttributeSet? = null
) : MaterialButton(
    ContextThemeWrapper(context, R.style.Theme_ProcessOut_Default),
    attrs
) {

    enum class State {
        ENABLED,
        DISABLED,
        PROGRESS
    }

    private var state: State = State.DISABLED
    private var style: POButtonStyle? = null
    private var label: String = text.toString()

    private var progressDrawable = buttonCircularProgressDrawable(
        context,
        ContextCompat.getColor(context, R.color.poButtonTextPrimary)
    )

    init {
        setState(state)
    }

    override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
        super.onTextChanged(charSequence, i, i1, i2)
        label = charSequence.toString()
    }

    fun setState(state: State) {
        when (state) {
            State.ENABLED -> {
                isEnabled = true
                isClickable = true
                text = label
                icon = null
                applyEnabledStyle()
            }
            State.DISABLED -> {
                isEnabled = false
                isClickable = false
                text = label
                icon = null
                applyDisabledStyle()
            }
            State.PROGRESS -> {
                isEnabled = true
                isClickable = false
                text = null
                icon = progressDrawable
                applyEnabledStyle()
            }
        }
        this.state = state
    }

    fun applyStyle(style: POButtonStyle) {
        this.style = style
        progressDrawable = buttonCircularProgressDrawable(context, style.progressIndicatorColor)

        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_pressed),
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_enabled)
            ),
            intArrayOf(
                style.highlightedBackgroundColor,
                style.normal.backgroundColor,
                style.disabled.backgroundColor
            )
        )
        ViewCompat.setBackgroundTintList(this, colorStateList)

        setState(state)
    }

    private fun applyEnabledStyle() {
        style?.normal?.text?.let {
            (this as TextView).applyStyle(it)
        }
        style?.normal?.border?.let { applyStyle(it) }
    }

    private fun applyDisabledStyle() {
        style?.disabled?.text?.let {
            (this as TextView).applyStyle(it)
        }
        style?.disabled?.border?.let { applyStyle(it) }
    }
}
