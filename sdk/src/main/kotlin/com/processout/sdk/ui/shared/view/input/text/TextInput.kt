package com.processout.sdk.ui.shared.view.input.text

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import com.processout.sdk.R
import com.processout.sdk.ui.nativeapm.applyStyle
import com.processout.sdk.ui.shared.model.InputParameter
import com.processout.sdk.ui.shared.style.input.POInputStateStyle
import com.processout.sdk.ui.shared.style.input.POInputStyle
import com.processout.sdk.ui.shared.view.extensions.defaultOutlineBackground
import com.processout.sdk.ui.shared.view.extensions.outlineBackground
import com.processout.sdk.ui.shared.view.extensions.requestFocusAndShowKeyboard
import com.processout.sdk.ui.shared.view.input.Input
import com.processout.sdk.ui.shared.view.input.InputComponent

internal class TextInput(
    context: Context,
    attrs: AttributeSet? = null,
    override val inputParameter: InputParameter? = null,
    override val style: POInputStyle? = null
) : ConstraintLayout(context, attrs, 0), InputComponent {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, null)

    private var state: Input.State = Input.State.Default

    private val title: TextView
    private val editText: EditText
    private val errorMessage: TextView

    private var defaultBackground = defaultOutlineBackground(context, R.color.poBorderPrimary)
    private var errorBackground = defaultOutlineBackground(context, R.color.poBorderError)

    private var afterValueChanged: ((String) -> Unit)? = null
    private var keyboardSubmitClick: (() -> Unit)? = null

    override var value: String
        get() = editText.text.toString()
        set(value) {
            editText.setText(value, TextView.BufferType.EDITABLE)
            editText.setSelection(editText.length())
        }

    init {
        LayoutInflater.from(
            ContextThemeWrapper(context, R.style.Theme_ProcessOut_Default_Input)
        ).inflate(R.layout.po_text_input, this, true)

        title = findViewById(R.id.po_title)
        editText = findViewById(R.id.po_edit_text)
        errorMessage = findViewById(R.id.po_error_message)

        style?.normal?.field?.let {
            defaultBackground = outlineBackground(context, it)
        }
        style?.error?.field?.let {
            errorBackground = outlineBackground(context, it)
        }

        setListeners()
        initWithInputParameters()
        setState(state)
    }

    private fun initWithInputParameters() {
        id = inputParameter?.id ?: View.generateViewId()
        editText.inputType = inputParameter?.toInputType() ?: InputType.TYPE_CLASS_TEXT
        editText.imeOptions = EditorInfo.IME_ACTION_DONE
        inputParameter?.let {
            title.text = it.parameter.displayName
            editText.hint = it.hint
            value = it.value
        }
    }

    private fun setListeners() {
        editText.doAfterTextChanged {
            if (state is Input.State.Error) {
                setState(Input.State.Default)
            }
            afterValueChanged?.invoke(value)
        }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                keyboardSubmitClick?.invoke()
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }
    }

    override fun setState(state: Input.State) {
        when (state) {
            Input.State.Default -> {
                style?.normal?.let { applyStateStyle(it) }
                editText.background = defaultBackground
                errorMessage.visibility = View.INVISIBLE
            }
            is Input.State.Error -> {
                style?.error?.let { applyStateStyle(it) }
                editText.background = errorBackground
                errorMessage.text = state.message
                errorMessage.visibility = View.VISIBLE
            }
        }

        this.state = state
    }

    override fun doAfterValueChanged(action: (value: String) -> Unit) {
        afterValueChanged = action
    }

    override fun onKeyboardSubmitClick(action: () -> Unit) {
        keyboardSubmitClick = action
    }

    override fun requestFocusAndShowKeyboard() {
        editText.requestFocusAndShowKeyboard()
    }

    private fun applyStateStyle(stateStyle: POInputStateStyle) {
        title.applyStyle(stateStyle.title)
        editText.applyStyle(stateStyle.field.text)
        stateStyle.field.hintTextColor?.let {
            editText.setHintTextColor(it)
        }
        errorMessage.applyStyle(stateStyle.description)
    }
}
