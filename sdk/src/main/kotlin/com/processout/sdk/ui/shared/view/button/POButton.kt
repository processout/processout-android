package com.processout.sdk.ui.shared.view.button

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.view.ContextThemeWrapper
import com.google.android.material.button.MaterialButton
import com.processout.sdk.R
import com.processout.sdk.ui.shared.view.extensions.indeterminateCircularProgressDrawable

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

    private var label: String = text.toString()

    private val progressDrawable = indeterminateCircularProgressDrawable(
        context,
        context.resources.getDimensionPixelSize(R.dimen.po_button_circularProgressIndicator_size),
        R.color.poButtonTextPrimary
    )

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
            }
            State.DISABLED -> {
                isEnabled = false
                isClickable = false
                text = label
                icon = null
            }
            State.PROGRESS -> {
                isEnabled = true
                isClickable = false
                text = null
                icon = progressDrawable
            }
        }
    }
}
