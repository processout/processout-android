package com.processout.sdk.ui.shared.model

import com.processout.sdk.ui.shared.view.button.POButton

internal sealed class SecondaryActionUiModel {

    internal data class Cancel(
        val text: String,
        val state: POButton.State,
        val disabledForMillis: Long,
        val confirmation: ActionConfirmation
    ) : SecondaryActionUiModel()

    fun copyWith(state: POButton.State) =
        when (this) {
            is Cancel -> copy(state = state)
        }
}
