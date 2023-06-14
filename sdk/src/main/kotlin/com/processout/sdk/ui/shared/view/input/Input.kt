package com.processout.sdk.ui.shared.view.input

internal interface Input {

    sealed class State {
        data class Default(val editable: Boolean = true) : State()
        data class Error(val message: String?) : State()
    }

    fun setState(state: State)
}
