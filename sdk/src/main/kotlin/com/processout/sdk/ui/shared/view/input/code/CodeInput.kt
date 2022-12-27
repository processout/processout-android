package com.processout.sdk.ui.shared.view.input.code

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
import com.processout.sdk.ui.shared.model.InputParameter
import com.processout.sdk.ui.shared.view.extensions.requestFocusAndShowKeyboard
import com.processout.sdk.ui.shared.view.input.Input
import com.processout.sdk.ui.shared.view.input.InputComponent

internal class CodeInput(
    context: Context,
    attrs: AttributeSet? = null,
    override val inputParameter: InputParameter? = null
) : ConstraintLayout(context, attrs, 0), InputComponent {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, null)

    companion object {
        const val LENGTH_MIN = 1
        const val LENGTH_MAX = 6
    }

    private var focusIndex: Int = 0
    private var state: Input.State = Input.State.Default

    private val title: TextView
    private val container: LinearLayout
    private val errorMessage: TextView

    private val editTexts = mutableListOf<CodeEditText>()
    private var afterValueChanged: ((String) -> Unit)? = null

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

    init {
        LayoutInflater.from(
            ContextThemeWrapper(context, R.style.Theme_ProcessOut_Default_Input)
        ).inflate(R.layout.po_code_input, this, true)

        title = findViewById(R.id.po_title)
        container = findViewById(R.id.po_container)
        errorMessage = findViewById(R.id.po_error_message)

        for (index in 0 until length()) {
            addEditText(index)
            setListeners(index)
        }

        initWithInputParameters()
        setState(state)
    }

    private fun length(): Int {
        inputParameter?.let {
            it.parameter.length?.run {
                if (this in LENGTH_MIN..LENGTH_MAX)
                    return this
            }
        }
        return LENGTH_MAX
    }

    private fun initWithInputParameters() {
        id = inputParameter?.id ?: View.generateViewId()
        inputParameter?.let {
            title.text = it.parameter.displayName
            value = it.value
        }
    }

    private fun addEditText(index: Int) {
        val editText = CodeEditText(context)
        editTexts.add(index, editText)
        container.addView(editText)
    }

    override fun setState(state: Input.State) {
        editTexts.forEach {
            it.setState(state)
        }

        when (state) {
            Input.State.Default -> {
                errorMessage.visibility = View.INVISIBLE
            }
            is Input.State.Error -> {
                errorMessage.text = state.message
                errorMessage.visibility = View.VISIBLE
            }
        }

        this.state = state
    }

    private fun setListeners(index: Int) {
        editTexts[index].setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                focusIndex = index
            }
        }

        editTexts[index].doAfterTextChanged {
            if (state is Input.State.Error) {
                setState(Input.State.Default)
            }

            it?.let { text ->
                if (text.isNotEmpty()) {
                    editTexts[index].setSelection(text.length)
                    changeFocus(next = true)
                }
            }

            afterValueChanged?.invoke(value)
        }

        editTexts[index].setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL &&
                event.action == KeyEvent.ACTION_DOWN &&
                index > 0 &&
                (editTexts[index].text.isNullOrEmpty() || editTexts[index].selectionStart == 0)
            ) {
                changeFocus(next = false)
                editTexts[focusIndex].setText(String(), TextView.BufferType.EDITABLE)
            }
            return@setOnKeyListener false
        }
    }

    override fun doAfterValueChanged(action: (value: String) -> Unit) {
        afterValueChanged = action
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
