package com.processout.sdk.ui.shared.view.input.text

import android.content.Context
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.widget.doAfterTextChanged
import com.processout.sdk.R
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterType
import com.processout.sdk.ui.nativeapm.applyControlsTintColor
import com.processout.sdk.ui.nativeapm.applyStyle
import com.processout.sdk.ui.shared.model.InputParameter
import com.processout.sdk.ui.shared.style.StyleConstants
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
    private val style: POInputStyle? = null
) : LinearLayout(context, attrs, 0), InputComponent {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, null)

    private var state: Input.State = inputParameter?.state ?: Input.State.Default()

    private val title: TextView
    private val editText: EditText
    private val errorMessage: TextView

    private var defaultBackground = defaultOutlineBackground(context, R.color.po_border_default)
    private var errorBackground = defaultOutlineBackground(context, R.color.po_text_error)

    @ColorInt
    private var defaultControlsTintColor = ContextCompat.getColor(context, R.color.po_text_primary)

    @ColorInt
    private var errorControlsTintColor = ContextCompat.getColor(context, R.color.po_text_error)

    private var afterValueChanged: ((String) -> Unit)? = null
    private var keyboardSubmitClick: (() -> Unit)? = null
    private var onFocusedAction: ((Int) -> Unit)? = null

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
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL

        title = findViewById(R.id.po_title)
        editText = findViewById(R.id.po_edit_text)
        errorMessage = findViewById(R.id.po_error_message)

        style?.normal?.field?.let {
            defaultBackground = outlineBackground(context, it)
            defaultControlsTintColor = it.controlsTintColor
        }
        style?.error?.field?.let {
            errorBackground = outlineBackground(context, it)
            errorControlsTintColor = it.controlsTintColor
        }

        setListeners()
        initWithInputParameters()
        applyState(state)
    }

    private fun initWithInputParameters() {
        id = inputParameter?.viewId ?: View.generateViewId()
        inputParameter?.let {
            title.text = it.parameter.displayName
            with(editText) {
                id = it.focusableViewId
                hint = it.hint
                inputType = it.toInputType()
                it.keyboardAction?.let { action ->
                    imeOptions = action.imeOptions
                    nextFocusForwardId = action.nextFocusForwardId
                }
            }
            value = it.value
        }
    }

    private fun setListeners() {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                onFocusedAction?.invoke(id)
            }
        }

        if (inputParameter?.type() == ParameterType.PHONE) {
            editText.filters = arrayOf(InputFilter { source, _, _, destination, _, destinationEnd ->
                filterPhoneNumber(source, destination.toString(), destinationEnd)
            })
            editText.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        }

        editText.doAfterTextChanged {
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

    private fun filterPhoneNumber(source: CharSequence, destination: String, destinationEnd: Int) =
        if (source.isNotEmpty()) {
            if (destination.isEmpty())
                if (source.startsWith("+")) source
                else "+$source"
            else source
        } else {
            if (destination == "+") "+"
            else if (destinationEnd == 1) "+"
            else source
        }

    override fun gainFocus() {
        if (editText.isFocused.not()) {
            editText.requestFocusAndShowKeyboard()
        }
    }

    override fun setState(state: Input.State) {
        if (this.state == state) return
        applyState(state)
    }

    private fun applyState(state: Input.State) {
        when (state) {
            is Input.State.Default -> {
                style?.normal?.let { applyStateStyle(it) }
                editText.isEnabled = state.editable
                editText.background = defaultBackground
                editText.highlightColor = ColorUtils.setAlphaComponent(
                    defaultControlsTintColor, StyleConstants.HIGHLIGHT_COLOR_ALPHA
                )
                editText.applyControlsTintColor(defaultControlsTintColor)
                errorMessage.text = String()
                errorMessage.visibility = View.INVISIBLE
            }
            is Input.State.Error -> {
                style?.error?.let { applyStateStyle(it) }
                editText.isEnabled = true
                editText.background = errorBackground
                editText.highlightColor = ColorUtils.setAlphaComponent(
                    errorControlsTintColor, StyleConstants.HIGHLIGHT_COLOR_ALPHA
                )
                editText.applyControlsTintColor(errorControlsTintColor)
                errorMessage.text = state.message
                errorMessage.visibility = View.VISIBLE
            }
        }
        this.state = state
    }

    private fun applyStateStyle(stateStyle: POInputStateStyle) {
        title.applyStyle(stateStyle.title)
        editText.applyStyle(stateStyle.field.text)
        stateStyle.field.hintTextColor.let {
            editText.setHintTextColor(it)
        }
        errorMessage.applyStyle(stateStyle.description)
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
