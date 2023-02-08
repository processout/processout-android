package com.processout.sdk.ui.shared.view.input

import com.processout.sdk.ui.shared.style.input.POInputStyle

internal interface Input {
    val style: POInputStyle?

    fun setState(state: State)

    sealed class State {
        object Default : State()
        data class Error(val message: String?) : State()
    }
}
