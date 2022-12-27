package com.processout.sdk.ui.shared.view.input

internal interface Input {
    fun setState(state: State)

    sealed class State {
        object Default : State()
        data class Error(val message: String) : State()
    }
}
