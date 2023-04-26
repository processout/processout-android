package com.processout.sdk.ui.shared.view.input.dropdown

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.processout.sdk.R
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterValue
import com.processout.sdk.ui.shared.model.InputParameter
import com.processout.sdk.ui.shared.style.input.POInputStyle
import com.processout.sdk.ui.shared.view.extensions.hideKeyboard
import com.processout.sdk.ui.shared.view.input.Input
import com.processout.sdk.ui.shared.view.input.InputComponent

internal class ExposedDropdownInput(
    context: Context,
    attrs: AttributeSet? = null,
    override val inputParameter: InputParameter? = null,
    override val style: POInputStyle? = null
) : LinearLayout(context, attrs, 0), InputComponent {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, null, null)

    private var state: Input.State = inputParameter?.state ?: Input.State.Default()

    private val title: TextView
    private val dropdownAutoComplete: MaterialAutoCompleteTextView
    private val errorMessage: TextView

    private var adapter: ParameterValueAdapter? = null
    private var afterValueChanged: ((String) -> Unit)? = null
    private var onFocusedAction: ((Int) -> Unit)? = null

    override var value: String = String()
        set(value) {
            field = value
            afterValueChanged?.invoke(value)
        }

    init {
        LayoutInflater.from(
            ContextThemeWrapper(context, R.style.Theme_ProcessOut_Default_Input)
        ).inflate(R.layout.po_exposed_dropdown_input, this, true)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL

        title = findViewById(R.id.po_title)
        dropdownAutoComplete = findViewById(R.id.po_dropdown_auto_complete)
        errorMessage = findViewById(R.id.po_error_message)

        setListeners()
        initWithInputParameters()
        applyState(state)
    }

    private fun initWithInputParameters() {
        id = inputParameter?.viewId ?: View.generateViewId()

        val options = inputParameter?.parameter?.availableValues ?: emptyList()
        adapter = ParameterValueAdapter(context, R.layout.po_exposed_dropdown_item, options)
        dropdownAutoComplete.setAdapter(adapter)

        options.find { it.default == true }?.also { setValue(it) }
    }

    private fun setListeners() {
        dropdownAutoComplete.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                hideKeyboard()
                onFocusedAction?.invoke(id)
            }
        }

        dropdownAutoComplete.setOnItemClickListener { _, _, position, _ ->
            adapter?.getItem(position)?.also { setValue(it) }
        }
    }

    private fun setValue(parameter: ParameterValue) {
        value = parameter.value
        dropdownAutoComplete.setText(parameter.displayName, false)
    }

    override fun doAfterValueChanged(action: (value: String) -> Unit) {
        afterValueChanged = action
        action(value)
    }

    override fun onFocused(action: (id: Int) -> Unit) {
        onFocusedAction = action
    }

    override fun gainFocus() {
        if (dropdownAutoComplete.isFocused.not()) {
            dropdownAutoComplete.requestFocus()
        }
    }

    override fun setState(state: Input.State) {
        if (this.state == state) return
        applyState(state)
    }

    private fun applyState(state: Input.State) {
        // TODO: apply states style
        this.state = state
    }
}
