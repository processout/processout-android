package com.processout.sdk.ui.shared.view.input

import com.processout.sdk.ui.shared.style.input.POInputStyle

internal interface Input {
    val style: POInputStyle?

    fun setState(state: State)

    sealed class State {
        data class Default(val editable: Boolean = true) : State()
        data class Error(val message: String?) : State()
    }
}
