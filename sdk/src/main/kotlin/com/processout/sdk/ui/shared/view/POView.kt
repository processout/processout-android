package com.processout.sdk.ui.shared.view

internal interface POView {
    fun setState(state: State)

    sealed class State {
        object Default : State()
        data class Error(val message: String) : State()
    }
}
