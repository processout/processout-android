package com.processout.sdk.ui.shared.view.input.radio

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import com.google.android.material.radiobutton.MaterialRadioButton
import com.processout.sdk.R
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterValue
import com.processout.sdk.ui.nativeapm.applyStyle
import com.processout.sdk.ui.shared.model.InputParameter
import com.processout.sdk.ui.shared.style.input.POInputStateStyle
import com.processout.sdk.ui.shared.style.input.POInputStyle
import com.processout.sdk.ui.shared.view.input.Input
import com.processout.sdk.ui.shared.view.input.InputComponent

internal class RadioInput(
    context: Context,
    attrs: AttributeSet? = null,
    override val inputParameter: InputParameter? = null,
    override val style: POInputStyle? = null
) : LinearLayout(context, attrs, 0), InputComponent {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, null)

    private var state: Input.State = inputParameter?.state ?: Input.State.Default()

    private val title: TextView
    private val radioGroup: RadioGroup
    private val radioButtons = mutableListOf<MaterialRadioButton>()
    private val errorMessage: TextView

    override var value: String = String()
    private var afterValueChanged: ((String) -> Unit)? = null

    init {
        LayoutInflater.from(
            ContextThemeWrapper(context, R.style.Theme_ProcessOut_Default_Input)
        ).inflate(R.layout.po_radio_input, this, true)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL

        title = findViewById(R.id.po_title)
        radioGroup = findViewById(R.id.po_radio_group)
        errorMessage = findViewById(R.id.po_error_message)

        initWithInputParameters()
        applyState(state)
    }

    private fun initWithInputParameters() {
        id = inputParameter?.viewId ?: View.generateViewId()
        title.text = inputParameter?.parameter?.displayName

        val options = inputParameter?.parameter?.availableValues ?: emptyList()
        options.forEachIndexed { index, parameterValue ->
            addRadioButton(index, parameterValue)
        }

        inputParameter?.value?.let { value ->
            if (value.isNotBlank()) {
                radioButtons.find { it.tag == value }?.also {
                    it.isChecked = true
                }
            }
        }
    }

    private fun addRadioButton(index: Int, parameterValue: ParameterValue) {
        MaterialRadioButton(context).let {
            it.id = index
            it.text = parameterValue.displayName
            it.tag = parameterValue.value

            it.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    value = buttonView.tag as String
                    afterValueChanged?.invoke(value)
                }
            }

            radioButtons.add(index, it)
            radioGroup.addView(it)
        }
    }

    override fun doAfterValueChanged(action: (value: String) -> Unit) {
        afterValueChanged = action
    }

    override fun setState(state: Input.State) {
        if (this.state == state) return
        applyState(state)
    }

    private fun applyState(state: Input.State) {
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
}
