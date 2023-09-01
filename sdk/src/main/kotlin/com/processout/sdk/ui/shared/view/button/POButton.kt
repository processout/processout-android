package com.processout.sdk.ui.shared.view.button

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.view.ContextThemeWrapper
import com.google.android.material.button.MaterialButton
import com.processout.sdk.R
import com.processout.sdk.ui.nativeapm.applyButtonStatesStyle
import com.processout.sdk.ui.nativeapm.applyStyle
import com.processout.sdk.ui.shared.style.button.POButtonStyle
import com.processout.sdk.ui.shared.view.extension.buttonCircularProgressDrawable
import com.processout.sdk.ui.shared.view.extension.dpToPx

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

    private var state: State = State.ENABLED
    private var style: POButtonStyle? = null
    private var label: String = text.toString()
    private val defaultElevation = elevation
    private var progressDrawable = buttonCircularProgressDrawable(context, textColors.defaultColor)

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
        applyButtonStatesStyle(style)
        setState(state)
    }

    private fun applyEnabledStyle() {
        elevation = style?.normal?.elevationDp?.dpToPx(context)?.toFloat() ?: defaultElevation
        style?.normal?.let { applyStyle(it) }
    }

    private fun applyDisabledStyle() {
        elevation = style?.disabled?.elevationDp?.dpToPx(context)?.toFloat() ?: 0f
        style?.disabled?.let { applyStyle(it) }
    }
}
