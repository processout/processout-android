package com.processout.sdk.ui.shared.view.input.dropdown

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.processout.sdk.R
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterValue
import com.processout.sdk.ui.nativeapm.applyStyle
import com.processout.sdk.ui.shared.model.InputParameter
import com.processout.sdk.ui.shared.style.POBorderStyle
import com.processout.sdk.ui.shared.style.input.POInputFieldStyle
import com.processout.sdk.ui.shared.style.input.POInputStateStyle
import com.processout.sdk.ui.shared.style.input.POInputStyle
import com.processout.sdk.ui.shared.view.extensions.defaultOutlineBackground
import com.processout.sdk.ui.shared.view.extensions.dpToPx
import com.processout.sdk.ui.shared.view.extensions.hideKeyboard
import com.processout.sdk.ui.shared.view.extensions.outlineBackground
import com.processout.sdk.ui.shared.view.input.Input
import com.processout.sdk.ui.shared.view.input.InputComponent

internal class ExposedDropdownInput(
    context: Context,
    attrs: AttributeSet? = null,
    override val inputParameter: InputParameter? = null,
    override val style: POInputStyle? = null,
    private val dropdownMenuStyle: POInputFieldStyle? = null
) : LinearLayout(context, attrs, 0), InputComponent {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, null, null, null)

    private var state: Input.State = inputParameter?.state ?: Input.State.Default()

    private val title: TextView
    private val dropdownLayout: TextInputLayout
    private val dropdownAutoComplete: MaterialAutoCompleteTextView
    private val errorMessage: TextView

    private var defaultBackground = defaultOutlineBackground(context, R.color.poBorderPrimary)
    private var errorBackground = defaultOutlineBackground(context, R.color.poBorderError)

    @ColorInt
    private var defaultControlsTintColor = ContextCompat.getColor(context, R.color.poTextPrimary)

    @ColorInt
    private var errorControlsTintColor = ContextCompat.getColor(context, R.color.poTextError)

    private var defaultDropdownBackground = outlineBackground(
        cornerRadiusPx = resources.getDimensionPixelSize(R.dimen.po_cornerRadius).toFloat(),
        borderWidthPx = 0,
        borderColor = Color.TRANSPARENT,
        backgroundColor = ContextCompat.getColor(context, R.color.poBackgroundGreyLight)
    )

    private var adapter: ParameterValueAdapter? = null
    private var afterValueChanged: ((String) -> Unit)? = null
    private var onFocusedAction: ((Int) -> Unit)? = null

    override var value: String = String()

    init {
        LayoutInflater.from(
            ContextThemeWrapper(context, R.style.Theme_ProcessOut_Default_Input)
        ).inflate(R.layout.po_exposed_dropdown_input, this, true)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL

        title = findViewById(R.id.po_title)
        dropdownLayout = findViewById(R.id.po_dropdown_layout)
        dropdownAutoComplete = findViewById(R.id.po_dropdown_auto_complete)
        errorMessage = findViewById(R.id.po_error_message)

        style?.normal?.field?.let {
            defaultBackground = outlineBackground(context, it)
            defaultControlsTintColor = it.controlsTintColor
        }
        style?.error?.field?.let {
            errorBackground = outlineBackground(context, it)
            errorControlsTintColor = it.controlsTintColor
        }
        val dropdownBackground = dropdownMenuStyle?.let {
            outlineBackground(context, it)
        } ?: defaultDropdownBackground
        dropdownAutoComplete.setDropDownBackgroundDrawable(dropdownBackground)
        dropdownAutoComplete.dropDownVerticalOffset = resources.getDimensionPixelSize(
            R.dimen.po_dropdown_menu_offsetVertical
        )

        setListeners()
        initWithInputParameters()
        applyState(state)
    }

    private fun initWithInputParameters() {
        id = inputParameter?.viewId ?: View.generateViewId()
        title.text = inputParameter?.parameter?.displayName

        val options = inputParameter?.parameter?.availableValues ?: emptyList()
        adapter = ParameterValueAdapter(
            context,
            R.layout.po_exposed_dropdown_item,
            options,
            dropdownMenuStyle?.text
        )
        dropdownAutoComplete.setAdapter(adapter)

        inputParameter?.value?.let { value ->
            if (value.isNotBlank()) {
                options.find { it.value == value }?.also { setValue(it) }
            }
        }
    }

    private fun setListeners() {
        dropdownAutoComplete.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                hideKeyboard()
                onFocusedAction?.invoke(id)
            }
        }

        dropdownAutoComplete.setOnItemClickListener { _, _, position, _ ->
            adapter?.getItem(position)?.also {
                setValue(it)
                afterValueChanged?.invoke(value)
            }
        }
    }

    private fun setValue(parameter: ParameterValue) {
        value = parameter.value
        dropdownAutoComplete.setText(parameter.displayName, false)
    }

    override fun doAfterValueChanged(action: (value: String) -> Unit) {
        afterValueChanged = action
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
        when (state) {
            is Input.State.Default -> {
                style?.normal?.let { applyStateStyle(it) }
                dropdownAutoComplete.isEnabled = state.editable
                dropdownLayout.background = defaultBackground
                dropdownLayout.setEndIconTintList(ColorStateList.valueOf(defaultControlsTintColor))
                errorMessage.text = String()
                errorMessage.visibility = View.INVISIBLE
            }
            is Input.State.Error -> {
                style?.error?.let { applyStateStyle(it) }
                dropdownAutoComplete.isEnabled = true
                dropdownLayout.background = errorBackground
                dropdownLayout.setEndIconTintList(ColorStateList.valueOf(errorControlsTintColor))
                errorMessage.text = state.message
                errorMessage.visibility = View.VISIBLE
            }
        }
        this.state = state
    }

    private fun applyStateStyle(stateStyle: POInputStateStyle) {
        title.applyStyle(stateStyle.title)
        dropdownAutoComplete.applyStyle(stateStyle.field.text)
        errorMessage.applyStyle(stateStyle.description)
        applyRippleStyle(stateStyle.field.border)
    }

    private fun applyRippleStyle(borderStyle: POBorderStyle) {
        borderStyle.radiusDp.dpToPx(context).toFloat().let {
            dropdownLayout.setBoxCornerRadii(it, it, it, it)
        }
    }
}
