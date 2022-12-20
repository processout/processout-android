package com.processout.sdk.ui.shared.view.codeinput

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import com.processout.sdk.R
import com.processout.sdk.ui.nativeapm.InputParameter
import com.processout.sdk.ui.shared.view.InputComponent
import com.processout.sdk.ui.shared.view.POView
import com.processout.sdk.ui.shared.view.extensions.requestFocusAndShowKeyboard

internal class CodeInput : ConstraintLayout, InputComponent {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(
        ContextThemeWrapper(context, R.style.Theme_ProcessOut_CodeInput), attrs, 0
    ) {
        initialize()
    }

    constructor(context: Context, inputParameter: InputParameter) : super(
        ContextThemeWrapper(context, R.style.Theme_ProcessOut_CodeInput), null, 0
    ) {
        this.inputParameter = inputParameter
        initialize()
    }

    companion object {
        const val LENGTH_MIN = 1
        const val LENGTH_MAX = 6
    }

    private var inputParameter: InputParameter? = null
    private var length: Int = LENGTH_MAX
    private var focusIndex: Int = 0
    private var state: POView.State = POView.State.Default

    private lateinit var title: TextView
    private lateinit var container: LinearLayout
    private lateinit var errorMessage: TextView

    private val editTexts = mutableListOf<CodeEditText>()

    override var value: String
        get() {
            var value = String()
            editTexts.forEach {
                value += it.text?.firstOrNull() ?: " "
            }
            return value
        }
        set(value) {
            val newValue = value.replace(Regex("[^0-9 ]"), String())
            editTexts.forEachIndexed { index, editText ->
                val char = newValue.getOrNull(index)
                val text = char?.let {
                    if (it.isWhitespace()) String() else it.toString()
                }
                editText.setText(text, TextView.BufferType.EDITABLE)
            }
            if (isNotFilled()) {
                resetFocus()
            }
        }

    private fun initialize() {
        LayoutInflater.from(context).inflate(R.layout.po_code_input, this, true)
        title = findViewById(R.id.po_title)
        container = findViewById(R.id.po_container)
        errorMessage = findViewById(R.id.po_error_message)

        inputParameter?.let {
            it.parameter.length?.run {
                if (this in LENGTH_MIN..LENGTH_MAX) length = this
            }
            title.text = it.parameter.displayName
        }

        for (index in 0 until length) {
            addEditText(index)
            setListeners(index)
        }

        setState(state)
    }

    override fun setState(state: POView.State) {
        editTexts.forEach {
            it.setState(state)
        }

        when (state) {
            POView.State.Default -> {
                errorMessage.visibility = View.INVISIBLE
            }
            is POView.State.Error -> {
                errorMessage.text = state.message
                errorMessage.visibility = View.VISIBLE
            }
        }

        this.state = state
    }

    private fun addEditText(index: Int) {
        val editText = CodeEditText(context)
        editTexts.add(index, editText)
        container.addView(editText)
    }

    private fun setListeners(index: Int) {
        editTexts[index].setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                focusIndex = index
            }
        }

        editTexts[index].doAfterTextChanged {
            if (state is POView.State.Error) {
                setState(POView.State.Default)
            }

            it?.let { text ->
                if (text.isNotEmpty()) {
                    editTexts[index].setSelection(text.length)
                    changeFocus(next = true)
                }
            }
        }

        editTexts[index].setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN &&
                (editTexts[index].text.isNullOrEmpty() || editTexts[index].selectionStart == 0)
            ) {
                changeFocus(next = false)
                editTexts[focusIndex].setText(String(), TextView.BufferType.EDITABLE)
            }
            return@setOnKeyListener false
        }
    }

    private fun resetFocus() {
        focusIndex = 0
        changeFocus(next = true)
    }

    private fun changeFocus(next: Boolean) {
        if (next) {
            // lookup for next empty 'editText' from current focus until the end
            var nextFocusIndex = focusIndex
            for (index in focusIndex until editTexts.size) {
                if (editTexts[index].text.isNullOrEmpty()) {
                    nextFocusIndex = index
                    break
                }
            }
            // lookup for next empty 'editText' from the start
            if (nextFocusIndex == focusIndex) {
                nextFocusIndex = editTexts.indexOfFirst { it.text.isNullOrEmpty() }
            }
            // change focus if next empty 'editText' was found,
            // otherwise keep focus on the current 'editText'
            if (nextFocusIndex != -1) {
                focusIndex = nextFocusIndex
            }
        } else {
            focusIndex--
        }

        when {
            focusIndex < 0 -> focusIndex = 0
            focusIndex >= editTexts.size -> focusIndex = editTexts.size - 1
        }
        requestFocusAndShowKeyboard()
    }

    override fun requestFocusAndShowKeyboard() {
        editTexts[focusIndex].requestFocusAndShowKeyboard()
    }

    private fun isFilled(): Boolean {
        editTexts.forEach {
            if (it.text.isNullOrEmpty()) return false
        }
        return true
    }

    private fun isNotFilled() = isFilled().not()
}
