package com.processout.sdk.ui.shared.model

import com.processout.sdk.ui.shared.view.button.POButton

internal data class SecondaryAction(
    val text: String,
    val state: POButton.State = POButton.State.DISABLED
)
