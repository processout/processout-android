package com.processout.sdk.ui.card.scanner

import com.processout.sdk.ui.core.state.POActionState

internal data class CardScannerViewModelState(
    val title: String,
    val description: String,
    val cancelAction: POActionState?
)
