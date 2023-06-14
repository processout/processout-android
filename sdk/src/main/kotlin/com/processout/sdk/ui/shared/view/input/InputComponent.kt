package com.processout.sdk.ui.shared.view.input

import com.processout.sdk.ui.shared.model.InputParameter

internal interface InputComponent : Input {
    var value: String
    val inputParameter: InputParameter?

    fun doAfterValueChanged(action: (value: String) -> Unit)
    fun onKeyboardSubmitClick(action: () -> Unit) {}
    fun onFocused(action: (id: Int) -> Unit) {}
    fun gainFocus() {}
}
