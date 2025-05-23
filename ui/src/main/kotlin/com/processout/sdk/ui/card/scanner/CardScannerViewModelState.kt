package com.processout.sdk.ui.card.scanner

import com.processout.sdk.ui.card.scanner.recognition.POScannedCard
import com.processout.sdk.ui.core.state.POActionState

internal data class CardScannerViewModelState(
    val loading: Boolean,
    val isCameraPermissionGranted: Boolean,
    val title: String,
    val description: String,
    val currentCard: POScannedCard?,
    val torchAction: POActionState,
    val cancelAction: POActionState?
)
