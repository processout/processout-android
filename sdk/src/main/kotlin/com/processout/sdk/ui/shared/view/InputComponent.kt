package com.processout.sdk.ui.shared.view

internal interface InputComponent : POView {
    var value: String
    fun requestFocusAndShowKeyboard()
}
