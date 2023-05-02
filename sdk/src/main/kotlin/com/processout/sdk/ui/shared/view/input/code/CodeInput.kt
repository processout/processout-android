package com.processout.sdk.ui.shared.view.input.code

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.widget.doAfterTextChanged
import com.processout.sdk.R
import com.processout.sdk.ui.nativeapm.applyStyle
import com.processout.sdk.ui.shared.model.InputParameter
import com.processout.sdk.ui.shared.style.input.POInputStateStyle
import com.processout.sdk.ui.shared.style.input.POInputStyle
import com.processout.sdk.ui.shared.view.extensions.requestFocusAndShowKeyboard
import com.processout.sdk.ui.shared.view.input.Input
import com.processout.sdk.ui.shared.view.input.InputComponent

internal class CodeInput(
    context: Context,
    attrs: AttributeSet? = null,
    override val inputParameter: InputParameter? = null,
    override val style: POInputStyle? = null
) : LinearLayout(context, attrs, 0), InputComponent {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, null, null)

    companion object {
        const val LENGTH_MIN = 1
        const val LENGTH_MAX = 6
    }

    private var focusIndex: Int = 0
    private var state: Input.State = inputParameter?.state ?: Input.State.Default()

    private val title: TextView
    private val container: LinearLayout
    private val errorMessage: TextView

    private val editTexts = mutableListOf<CodeEditText>()
    private var afterValueChanged: ((String) -> Unit)? = null
    private var keyboardSubmitClick: (() -> Unit)? = null
    private var onFocusedAction: ((Int) -> Unit)? = null

    override var value: String
        get() {
            var value = String()
            editTexts.forEach {
                value += it.text?.firstOrNull() ?: " "
            }
            return value
        }
        set(value) {
            val newValue = value.replace(Regex("[^\\d ]"), String())
            editTexts.forEachIndexed { index, editText ->
                val char = newValue.getOrNull(index)
                val text = char?.let {
                    if (it.isWhitespace()) String() else it.toString()
                }
                editText.setText(text, TextView.BufferType.EDITABLE)
            }
        }

    init {
        LayoutInflater.from(
            ContextThemeWrapper(context, R.style.Theme_ProcessOut_Default_Input)
        ).inflate(R.layout.po_code_input, this, true)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL

        title = findViewById(R.id.po_title)
        errorMessage = findViewById(R.id.po_error_message)
        container = findViewById(R.id.po_container)
        container.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) changeFocus(next = true)
        }

        for (index in 0 until length()) {
            addEditText(index)
            setListeners(index)
        }

        initWithInputParameters()
        applyState(state)
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
        id = inputParameter?.viewId ?: View.generateViewId()
        inputParameter?.let {
            title.text = it.parameter.displayName
            container.id = it.focusableViewId
            it.keyboardAction?.let { action ->
                editTexts.forEach { editText ->
                    editText.imeOptions = action.imeOptions
                    editText.nextFocusForwardId = action.nextFocusForwardId
                }
            }
            value = it.value
        }
    }

    private fun addEditText(index: Int) {
        val editText = CodeEditText(context, style = style)
        editTexts.add(index, editText)
        container.addView(editText)
    }

    override fun setState(state: Input.State) {
        if (this.state == state) return
        applyState(state)
    }

    private fun applyState(state: Input.State) {
        editTexts.forEach {
            it.setState(state)
        }
        when (state) {
            is Input.State.Default -> {
                style?.normal?.let { applyStateStyle(it) }
                errorMessage.text = String()
                errorMessage.visibility = View.INVISIBLE
            }
            is Input.State.Error -> {
                style?.error?.let { applyStateStyle(it) }
                errorMessage.text = state.message
                errorMessage.visibility = View.VISIBLE
            }
        }
        this.state = state
    }

    private fun applyStateStyle(stateStyle: POInputStateStyle) {
        title.applyStyle(stateStyle.title)
        errorMessage.applyStyle(stateStyle.description)
    }

    private fun setListeners(index: Int) {
        editTexts[index].setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                focusIndex = index
                onFocusedAction?.invoke(id)
            }
        }

        editTexts[index].filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            val trimmedSource = source.replace(Regex("\\D"), String())
            if (trimmedSource.length > 1) {
                clear()
                value = trimmedSource
                return@InputFilter String()
            } else {
                if (editTexts[index].text.isNullOrEmpty().not() && trimmedSource.isNotEmpty()) {
                    return@InputFilter String()
                }
                return@InputFilter trimmedSource
            }
        })

        editTexts[index].doAfterTextChanged {
            it?.let { text ->
                if (text.isNotEmpty()) {
                    editTexts[index].setSelection(text.length)
                    changeFocus(next = true)
                }
            }
            afterValueChanged?.invoke(value)
        }

        editTexts[index].setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                keyboardSubmitClick?.invoke()
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
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
        gainFocus()
    }

    override fun gainFocus() {
        if (editTexts[focusIndex].isFocused.not()) {
            editTexts[focusIndex].requestFocusAndShowKeyboard()
        }
    }

    private fun resetFocus() {
        focusIndex = 0
        changeFocus(next = true)
    }

    private fun isFilled(): Boolean {
        editTexts.forEach {
            if (it.text.isNullOrEmpty()) return false
        }
        return true
    }

    private fun clear() {
        editTexts.forEach {
            it.setText(String(), TextView.BufferType.EDITABLE)
        }
    }

    override fun doAfterValueChanged(action: (value: String) -> Unit) {
        afterValueChanged = action
    }

    override fun onKeyboardSubmitClick(action: () -> Unit) {
        keyboardSubmitClick = action
    }

    override fun onFocused(action: (id: Int) -> Unit) {
        onFocusedAction = action
    }
}
